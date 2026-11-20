package co.edu.ucundinamarca.saesu.duelo.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.duelo.application.dto.response.CompaneroResponse;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Retos entre compañeros con SQL directo (V32). */
@Component
public class DueloJdbcAdapter implements DueloPort {

    private static final String ES_ESTUDIANTE = "UPPER(REPLACE(u.rol, 'ROLE_', '')) = 'ESTUDIANTE'";

    private static final String SELECT_DUELO = """
            SELECT d.id, d.tema, d.retador_id, ur.nombre AS retador, d.rival_id, uv.nombre AS rival, d.creado_en,
                   (SELECT COUNT(*) FROM duelo_preguntas dp WHERE dp.duelo_id = d.id) AS total,
                   er.correctas AS correctas_retador, er.segundos AS segundos_retador,
                   ev.correctas AS correctas_rival, ev.segundos AS segundos_rival
            FROM duelos d
            JOIN usuarios ur ON ur.id = d.retador_id
            JOIN usuarios uv ON uv.id = d.rival_id
            LEFT JOIN duelo_entregas er ON er.duelo_id = d.id AND er.usuario_id = d.retador_id
            LEFT JOIN duelo_entregas ev ON ev.duelo_id = d.id AND ev.usuario_id = d.rival_id
            """;

    private static Integer entero(ResultSet rs, String c) throws SQLException {
        return rs.getObject(c) == null ? null : rs.getInt(c);
    }

    private static final RowMapper<FilaDuelo> DUELO = (rs, i) -> {
        Timestamp creado = rs.getTimestamp("creado_en");
        return new FilaDuelo(rs.getLong("id"), rs.getString("tema"), rs.getLong("retador_id"), rs.getString("retador"),
                rs.getLong("rival_id"), rs.getString("rival"), creado == null ? null : creado.toLocalDateTime(),
                rs.getInt("total"), entero(rs, "correctas_retador"), entero(rs, "segundos_retador"),
                entero(rs, "correctas_rival"), entero(rs, "segundos_rival"));
    };

    private final JdbcTemplate jdbc;

    public DueloJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> idPorCorreo(String correo) {
        return jdbc.query("SELECT id FROM usuarios WHERE LOWER(correo) = LOWER(?)", (rs, i) -> rs.getLong("id"), correo)
                .stream().findFirst();
    }

    @Override
    public boolean esEstudiante(Long usuarioId) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios u WHERE u.id = ? AND " + ES_ESTUDIANTE, Long.class, usuarioId);
        return n != null && n > 0;
    }

    @Override
    public List<CompaneroResponse> companeros(Long usuarioId) {
        return jdbc.query("SELECT u.id, u.nombre, u.foto_perfil_url FROM usuarios u WHERE u.id <> ? AND " + ES_ESTUDIANTE
                        + " ORDER BY LOWER(u.nombre)",
                (rs, i) -> new CompaneroResponse(rs.getLong("id"), rs.getString("nombre"), rs.getString("foto_perfil_url")),
                usuarioId);
    }

    @Override
    @Transactional
    public Long crear(Long retadorId, Long rivalId, String tema, List<Long> preguntaIds, LocalDateTime creadoEn) {
        Long id = jdbc.queryForObject("""
                INSERT INTO duelos (retador_id, rival_id, tema, creado_en) VALUES (?, ?, ?, ?) RETURNING id
                """, Long.class, retadorId, rivalId, tema, Timestamp.valueOf(creadoEn));
        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < preguntaIds.size(); i++) {
            filas.add(new Object[]{id, i + 1, preguntaIds.get(i)});
        }
        jdbc.batchUpdate("INSERT INTO duelo_preguntas (duelo_id, orden, pregunta_id) VALUES (?, ?, ?)", filas);
        return id;
    }

    @Override
    public List<FilaDuelo> duelosDe(Long usuarioId, int limite) {
        return jdbc.query(SELECT_DUELO + " WHERE d.retador_id = ? OR d.rival_id = ? ORDER BY d.creado_en DESC, d.id DESC LIMIT ?",
                DUELO, usuarioId, usuarioId, limite);
    }

    @Override
    public Optional<FilaDuelo> buscar(Long dueloId, Long usuarioId) {
        return jdbc.query(SELECT_DUELO + " WHERE d.id = ? AND (d.retador_id = ? OR d.rival_id = ?)",
                DUELO, dueloId, usuarioId, usuarioId).stream().findFirst();
    }

    @Override
    public List<PreguntaQuiz> preguntas(Long dueloId) {
        Map<Long, PreguntaQuiz> porId = new LinkedHashMap<>();
        jdbc.query("""
                SELECT p.id, p.tema, p.dificultad, p.enunciado, p.pista, o.letra, o.texto, o.es_correcta
                FROM duelo_preguntas dp
                JOIN preguntas p ON p.id = dp.pregunta_id
                JOIN opciones o ON o.pregunta_id = p.id
                WHERE dp.duelo_id = ?
                ORDER BY dp.orden, o.letra
                """, rs -> {
            porId.computeIfAbsent(rs.getLong("id"), id -> {
                try {
                    return new PreguntaQuiz(id, rs.getString("tema"), rs.getString("dificultad"), rs.getString("enunciado"),
                            rs.getString("pista"), new ArrayList<>());
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            }).opciones().add(new PreguntaQuiz.Opcion(rs.getString("letra"), rs.getString("texto"), rs.getBoolean("es_correcta")));
        }, dueloId);
        return new ArrayList<>(porId.values());
    }

    @Override
    public boolean entregar(Long dueloId, Long usuarioId, int correctas, int segundos, LocalDateTime entregadoEn) {
        return jdbc.update("""
                INSERT INTO duelo_entregas (duelo_id, usuario_id, correctas, segundos, entregado_en)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (duelo_id, usuario_id) DO NOTHING
                """, dueloId, usuarioId, correctas, segundos, Timestamp.valueOf(entregadoEn)) > 0;
    }
}
