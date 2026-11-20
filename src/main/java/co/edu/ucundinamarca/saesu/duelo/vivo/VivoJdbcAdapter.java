package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class VivoJdbcAdapter implements VivoPort {

    private static final RowMapper<Sala> SALA = (rs, i) -> {
        Timestamp inicio = rs.getTimestamp("inicia_en");
        long invitado = rs.getLong("invitado_id");
        boolean sinInvitado = rs.wasNull();
        return new Sala(rs.getLong("id"), rs.getString("codigo"), rs.getLong("anfitrion_id"), rs.getString("anfitrion"),
                sinInvitado ? null : invitado, rs.getString("invitado"), rs.getString("tema"), rs.getString("estado"),
                rs.getInt("segundos_por_pregunta"), inicio == null ? null : inicio.toLocalDateTime(), rs.getInt("total"));
    };

    private final JdbcTemplate jdbc;

    public VivoJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> idPorCorreo(String correo) {
        return jdbc.query("SELECT id FROM usuarios WHERE LOWER(correo) = LOWER(?)", (rs, i) -> rs.getLong("id"), correo)
                .stream().findFirst();
    }

    @Override
    public boolean esEstudiante(Long usuarioId) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios u WHERE u.id = ? AND UPPER(REPLACE(u.rol, 'ROLE_', '')) = 'ESTUDIANTE'",
                Long.class, usuarioId);
        return n != null && n > 0;
    }

    @Override
    public boolean codigoExiste(String codigo) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM salas_vivo WHERE codigo = ?", Long.class, codigo);
        return n != null && n > 0;
    }

    @Override
    @Transactional
    public Long crear(String codigo, Long anfitrionId, Long invitadoId, String tema, int segundosPorPregunta,
                      List<Long> preguntaIds, LocalDateTime creadaEn) {
        Long id = jdbc.queryForObject("""
                INSERT INTO salas_vivo (codigo, anfitrion_id, invitado_id, tema, segundos_por_pregunta, creada_en)
                VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                """, Long.class, codigo, anfitrionId, invitadoId, tema, segundosPorPregunta, Timestamp.valueOf(creadaEn));
        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < preguntaIds.size(); i++) {
            filas.add(new Object[]{id, i, preguntaIds.get(i)});
        }
        jdbc.batchUpdate("INSERT INTO sala_preguntas (sala_id, orden, pregunta_id) VALUES (?, ?, ?)", filas);
        return id;
    }

    @Override
    public Optional<Sala> buscar(String codigo) {
        return jdbc.query("""
                SELECT s.*, ua.nombre AS anfitrion, ui.nombre AS invitado,
                       (SELECT COUNT(*) FROM sala_preguntas sp WHERE sp.sala_id = s.id) AS total
                FROM salas_vivo s
                JOIN usuarios ua ON ua.id = s.anfitrion_id
                LEFT JOIN usuarios ui ON ui.id = s.invitado_id
                WHERE s.codigo = ?
                """, SALA, codigo).stream().findFirst();
    }

    @Override
    public boolean unir(Long salaId, Long invitadoId, LocalDateTime iniciaEn) {
        return jdbc.update("""
                UPDATE salas_vivo SET invitado_id = ?, estado = 'JUGANDO', inicia_en = ?
                WHERE id = ? AND estado = 'ESPERANDO' AND anfitrion_id <> ? AND (invitado_id IS NULL OR invitado_id = ?)
                """, invitadoId, Timestamp.valueOf(iniciaEn), salaId, invitadoId, invitadoId) > 0;
    }

    @Override
    public void cambiarEstado(Long salaId, String estado) {
        jdbc.update("UPDATE salas_vivo SET estado = ? WHERE id = ?", estado, salaId);
    }

    @Override
    public List<PreguntaQuiz> preguntas(Long salaId) {
        Map<Long, PreguntaQuiz> porId = new LinkedHashMap<>();
        jdbc.query("""
                SELECT p.id, p.tema, p.dificultad, p.enunciado, p.pista, o.letra, o.texto, o.es_correcta
                FROM sala_preguntas sp
                JOIN preguntas p ON p.id = sp.pregunta_id
                JOIN opciones o ON o.pregunta_id = p.id
                WHERE sp.sala_id = ?
                ORDER BY sp.orden, o.letra
                """, rs -> {
            porId.computeIfAbsent(rs.getLong("id"), id -> {
                try {
                    return new PreguntaQuiz(id, rs.getString("tema"), rs.getString("dificultad"), rs.getString("enunciado"),
                            rs.getString("pista"), new ArrayList<>());
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            }).opciones().add(new PreguntaQuiz.Opcion(rs.getString("letra"), rs.getString("texto"), rs.getBoolean("es_correcta")));
        }, salaId);
        return new ArrayList<>(porId.values());
    }

    @Override
    public boolean responder(Long salaId, Long usuarioId, int orden, String letra, boolean correcta, int puntos, LocalDateTime cuando) {
        return jdbc.update("""
                INSERT INTO sala_respuestas (sala_id, usuario_id, orden, letra, correcta, puntos, respondida_en)
                VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (sala_id, usuario_id, orden) DO NOTHING
                """, salaId, usuarioId, orden, letra, correcta, puntos, Timestamp.valueOf(cuando)) > 0;
    }

    @Override
    public List<Respuesta> respuestas(Long salaId) {
        return jdbc.query("SELECT usuario_id, orden, letra, correcta, puntos FROM sala_respuestas WHERE sala_id = ?",
                (rs, i) -> new Respuesta(rs.getLong("usuario_id"), rs.getInt("orden"), rs.getString("letra"),
                        rs.getBoolean("correcta"), rs.getInt("puntos")), salaId);
    }
}
