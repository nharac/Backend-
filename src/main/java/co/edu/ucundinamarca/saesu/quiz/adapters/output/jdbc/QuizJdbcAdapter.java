package co.edu.ucundinamarca.saesu.quiz.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.quiz.domain.model.IntentoQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Quiz con SQL directo sobre quiz_intentos y quiz_preguntas (V20) y el banco de preguntas. */
@Component
public class QuizJdbcAdapter implements QuizRepositoryPort {

    private static final String COLUMNAS_PREGUNTA =
            "p.id, p.tema, p.dificultad, p.enunciado, p.pista, o.letra, o.texto, o.es_correcta";

    private static final RowMapper<IntentoQuiz> INTENTO = (rs, i) -> new IntentoQuiz(
            rs.getLong("id"),
            rs.getLong("usuario_id"),
            rs.getString("tema"),
            rs.getInt("cantidad"),
            instante(rs, "iniciado_en"),
            instante(rs, "limite_en"),
            instante(rs, "finalizado_en"),
            (Integer) rs.getObject("correctas"),
            rs.getBoolean("fuera_de_tiempo"));

    private final JdbcTemplate jdbc;

    public QuizJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PreguntaQuiz> preguntasAlAzar(String tema, int cantidad) {
        // Primero se eligen las preguntas al azar y luego se traen sus opciones, todo en una consulta
        String sql = """
                SELECT %s
                FROM (SELECT id, random() AS azar FROM preguntas
                      WHERE activa = TRUE AND (?::text IS NULL OR tema = ?)
                      ORDER BY azar LIMIT ?) elegidas
                JOIN preguntas p ON p.id = elegidas.id
                JOIN opciones o ON o.pregunta_id = p.id
                ORDER BY elegidas.azar, o.letra
                """.formatted(COLUMNAS_PREGUNTA);
        return agrupar(jdbc.query(sql, (rs, i) -> fila(rs), tema, tema, cantidad));
    }

    @Override
    @Transactional
    public Long crearIntento(Long usuarioId, String tema, Instant iniciadoEn, Instant limiteEn, List<Long> preguntaIds) {
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO quiz_intentos (usuario_id, tema, cantidad, iniciado_en, limite_en)
                    VALUES (?, ?, ?, ?, ?)
                    """, new String[]{"id"});
            ps.setLong(1, usuarioId);
            ps.setString(2, tema);
            ps.setInt(3, preguntaIds.size());
            ps.setObject(4, OffsetDateTime.ofInstant(iniciadoEn, ZoneOffset.UTC));
            ps.setObject(5, OffsetDateTime.ofInstant(limiteEn, ZoneOffset.UTC));
            return ps;
        }, llave);
        Long id = Objects.requireNonNull(llave.getKey(), "La BD no devolvió el id del intento").longValue();

        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < preguntaIds.size(); i++) {
            filas.add(new Object[]{id, i + 1, preguntaIds.get(i)});
        }
        jdbc.batchUpdate("INSERT INTO quiz_preguntas (intento_id, orden, pregunta_id) VALUES (?, ?, ?)", filas);
        return id;
    }

    @Override
    public Optional<IntentoQuiz> buscarIntento(Long intentoId) {
        return jdbc.query("SELECT * FROM quiz_intentos WHERE id = ?", INTENTO, intentoId).stream().findFirst();
    }

    @Override
    public List<PreguntaQuiz> preguntasDelIntento(Long intentoId) {
        String sql = """
                SELECT %s
                FROM quiz_preguntas q
                JOIN preguntas p ON p.id = q.pregunta_id
                JOIN opciones o ON o.pregunta_id = p.id
                WHERE q.intento_id = ?
                ORDER BY q.orden, o.letra
                """.formatted(COLUMNAS_PREGUNTA);
        return agrupar(jdbc.query(sql, (rs, i) -> fila(rs), intentoId));
    }

    @Override
    @Transactional
    public boolean finalizar(Long intentoId, Instant finalizadoEn, int correctas, boolean fueraDeTiempo,
                             List<Eleccion> elecciones) {
        int cambiadas = jdbc.update("""
                UPDATE quiz_intentos SET finalizado_en = ?, correctas = ?, fuera_de_tiempo = ?
                WHERE id = ? AND finalizado_en IS NULL
                """, OffsetDateTime.ofInstant(finalizadoEn, ZoneOffset.UTC), correctas, fueraDeTiempo, intentoId);
        if (cambiadas == 0) {
            return false;
        }
        List<Object[]> filas = elecciones.stream()
                .map(e -> new Object[]{e.letra(), e.correcta(), intentoId, e.preguntaId()})
                .toList();
        jdbc.batchUpdate("""
                UPDATE quiz_preguntas SET letra_elegida = ?, es_correcta = ?
                WHERE intento_id = ? AND pregunta_id = ?
                """, filas);
        return true;
    }

    @Override
    public List<Eleccion> eleccionesDe(Long intentoId) {
        return jdbc.query("""
                SELECT pregunta_id, letra_elegida, COALESCE(es_correcta, FALSE) AS es_correcta
                FROM quiz_preguntas WHERE intento_id = ? ORDER BY orden
                """, (rs, i) -> new Eleccion(rs.getLong("pregunta_id"), rs.getString("letra_elegida"),
                rs.getBoolean("es_correcta")), intentoId);
    }

    @Override
    public List<IntentoQuiz> historial(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT * FROM quiz_intentos
                WHERE usuario_id = ? AND finalizado_en IS NOT NULL
                ORDER BY finalizado_en DESC LIMIT ?
                """, INTENTO, usuarioId, limite);
    }

    @Override
    public Totales totales(Long usuarioId) {
        return jdbc.queryForObject("""
                SELECT COUNT(*) AS intentos,
                       COALESCE(AVG(100.0 * correctas / cantidad), 0) AS promedio,
                       COALESCE(MAX(100.0 * correctas / cantidad), 0) AS mejor
                FROM quiz_intentos
                WHERE usuario_id = ? AND finalizado_en IS NOT NULL
                """, (rs, i) -> new Totales(rs.getLong("intentos"), rs.getDouble("promedio"), rs.getDouble("mejor")),
                usuarioId);
    }

    // ---------------------------------------------------------------

    /** Una fila = una opción; se agrupan por pregunta conservando el orden. */
    private record Fila(Long id, String tema, String dificultad, String enunciado, String pista,
                        PreguntaQuiz.Opcion opcion) {}

    private static Fila fila(ResultSet rs) throws SQLException {
        return new Fila(rs.getLong("id"), rs.getString("tema"), rs.getString("dificultad"),
                rs.getString("enunciado"), rs.getString("pista"),
                new PreguntaQuiz.Opcion(rs.getString("letra"), rs.getString("texto"), rs.getBoolean("es_correcta")));
    }

    private static List<PreguntaQuiz> agrupar(List<Fila> filas) {
        Map<Long, PreguntaQuiz> porId = new LinkedHashMap<>();
        for (Fila f : filas) {
            porId.computeIfAbsent(f.id(), id -> new PreguntaQuiz(id, f.tema(), f.dificultad(), f.enunciado(),
                    f.pista(), new ArrayList<>())).opciones().add(f.opcion());
        }
        return new ArrayList<>(porId.values());
    }

    private static Instant instante(ResultSet rs, String columna) throws SQLException {
        OffsetDateTime t = rs.getObject(columna, OffsetDateTime.class);
        return t == null ? null : t.toInstant();
    }
}
