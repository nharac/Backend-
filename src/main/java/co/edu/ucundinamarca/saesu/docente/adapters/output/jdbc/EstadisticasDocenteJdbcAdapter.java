package co.edu.ucundinamarca.saesu.docente.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.ActividadDia;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.PreguntaDificil;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse.IntentoReciente;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse.PreguntaConFallos;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Estadísticas del docente con SQL directo (solo lectura).
 * Los roles se comparan normalizados porque en la BD hay "ESTUDIANTE", "estudiante" y "ROLE_ESTUDIANTE".
 * Las fechas "por día" se calculan con la hora de Colombia (Supabase trabaja en UTC).
 */
@Component
public class EstadisticasDocenteJdbcAdapter implements EstadisticasDocentePort {

    private static final String ES_ESTUDIANTE = "UPPER(REPLACE(u.rol, 'ROLE_', '')) = 'ESTUDIANTE'";
    private static final String HOY = "(NOW() AT TIME ZONE 'America/Bogota')::date";

    private static final String SELECT_ESTUDIANTE_PLANTILLA = """
            SELECT u.id, u.nombre, u.correo, u.foto_perfil_url,
                   COALESCE((SELECT SUM(ph.cantidad) FROM puntos_historial ph WHERE ph.usuario_id = u.id), 0) AS puntos,
                   CASE WHEN u.ultima_fecha_practica >= %s - 1
                        THEN COALESCE(u.racha_actual, 0) ELSE 0 END AS racha,
                   COUNT(r.id) AS intentos,
                   COUNT(r.id) FILTER (WHERE r.es_correcta) AS correctas,
                   MAX(r.fecha_intento) AS ultima,
                   u.fecha_registro,
                   q.quices, q.promedio_quiz, q.ultimo_quiz
            FROM usuarios u
            LEFT JOIN LATERAL (
                SELECT COUNT(*) AS quices,
                       AVG(100.0 * qi.correctas / qi.cantidad) AS promedio_quiz,
                       MAX(qi.finalizado_en) AS ultimo_quiz
                FROM quiz_intentos qi
                WHERE qi.usuario_id = u.id AND qi.finalizado_en IS NOT NULL
            ) q ON TRUE
            LEFT JOIN respuestas_estudiante r ON r.usuario_id = u.id
            WHERE %s
            """;

    // Consultas completas armadas una sola vez a partir de constantes (nunca con datos del usuario)
    private static final String GRUPO_ESTUDIANTE = " GROUP BY u.id, q.quices, q.promedio_quiz, q.ultimo_quiz";
    private static final String SQL_LISTAR_ESTUDIANTES = (SELECT_ESTUDIANTE_PLANTILLA + GRUPO_ESTUDIANTE + " ORDER BY LOWER(u.nombre)")
            .formatted(HOY, ES_ESTUDIANTE);
    private static final String SQL_BUSCAR_ESTUDIANTE = (SELECT_ESTUDIANTE_PLANTILLA + " AND u.id = ?" + GRUPO_ESTUDIANTE)
            .formatted(HOY, ES_ESTUDIANTE);

    private static final String PROGRESO_POR_TEMA_PLANTILLA = """
            SELECT r.usuario_id, p.tema, COUNT(*) AS intentos,
                   COUNT(*) FILTER (WHERE r.es_correcta) AS correctas,
                   COUNT(DISTINCT r.pregunta_id) FILTER (WHERE r.es_correcta AND p.activa) AS resueltas
            FROM respuestas_estudiante r
            JOIN preguntas p ON p.id = r.pregunta_id
            JOIN usuarios u ON u.id = r.usuario_id
            WHERE %s
            """;
    private static final String SQL_PROGRESO_TODOS = (PROGRESO_POR_TEMA_PLANTILLA + " GROUP BY r.usuario_id, p.tema")
            .formatted(ES_ESTUDIANTE);
    private static final String SQL_PROGRESO_DE = (PROGRESO_POR_TEMA_PLANTILLA + " AND r.usuario_id = ? GROUP BY r.usuario_id, p.tema")
            .formatted(ES_ESTUDIANTE);

    private static final String ACTIVIDAD = """
            SELECT d::date AS fecha, COUNT(r.id) AS intentos, COUNT(DISTINCT r.usuario_id) AS estudiantes
            FROM generate_series(%1$s - ?, %1$s, INTERVAL '1 day') AS d
            LEFT JOIN respuestas_estudiante r
                   ON (r.fecha_intento AT TIME ZONE 'America/Bogota')::date = d::date
                  %2$s
            GROUP BY d
            ORDER BY d
            """;
    private static final String SQL_ACTIVIDAD_GRUPO = ACTIVIDAD.formatted(HOY,
            "AND r.usuario_id IN (SELECT u.id FROM usuarios u WHERE " + ES_ESTUDIANTE + ")");
    private static final String SQL_ACTIVIDAD_ESTUDIANTE = ACTIVIDAD.formatted(HOY, "AND r.usuario_id = ?");

    private final JdbcTemplate jdbc;

    public EstadisticasDocenteJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static LocalDateTime fecha(ResultSet rs, String columna) throws SQLException {
        Timestamp t = rs.getTimestamp(columna);
        return t == null ? null : t.toLocalDateTime();
    }

    private static final RowMapper<FilaEstudiante> FILA_ESTUDIANTE = (rs, i) -> new FilaEstudiante(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("foto_perfil_url"),
            rs.getLong("puntos"),
            rs.getInt("racha"),
            rs.getLong("intentos"),
            rs.getLong("correctas"),
            fecha(rs, "ultima"),
            fecha(rs, "fecha_registro"),
            rs.getLong("quices"),
            (Double) (rs.getObject("promedio_quiz") == null ? null : rs.getDouble("promedio_quiz")),
            fecha(rs, "ultimo_quiz"));

    @Override
    public List<FilaEstudiante> listarEstudiantes() {
        return jdbc.query(SQL_LISTAR_ESTUDIANTES, FILA_ESTUDIANTE);
    }

    @Override
    public Optional<FilaEstudiante> buscarEstudiante(Long usuarioId) {
        return jdbc.query(SQL_BUSCAR_ESTUDIANTE, FILA_ESTUDIANTE, usuarioId)
                .stream().findFirst();
    }

    @Override
    public List<FilaTemaEstudiante> progresoPorTema(Long usuarioId) {
        RowMapper<FilaTemaEstudiante> mapper = (rs, i) -> new FilaTemaEstudiante(
                rs.getLong("usuario_id"), rs.getString("tema"),
                rs.getLong("intentos"), rs.getLong("correctas"), rs.getLong("resueltas"));
        if (usuarioId == null) {
            return jdbc.query(SQL_PROGRESO_TODOS, mapper);
        }
        return jdbc.query(SQL_PROGRESO_DE, mapper, usuarioId);
    }

    @Override
    public Map<String, Long> preguntasActivasPorTema() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        jdbc.query("SELECT tema, COUNT(*) AS n FROM preguntas WHERE activa GROUP BY tema",
                rs -> { mapa.put(rs.getString("tema"), rs.getLong("n")); });
        return mapa;
    }

    @Override
    public ResumenSemana resumenSemana() {
        return jdbc.queryForObject("""
                SELECT COUNT(DISTINCT r.usuario_id) AS activos, COUNT(*) AS intentos
                FROM respuestas_estudiante r
                JOIN usuarios u ON u.id = r.usuario_id
                WHERE r.fecha_intento >= NOW() - INTERVAL '7 days' AND %s
                """.formatted(ES_ESTUDIANTE),
                (rs, i) -> new ResumenSemana(rs.getLong("activos"), rs.getLong("intentos")));
    }

    @Override
    public List<TemaEstadistica> estadisticasPorTema() {
        return jdbc.query("""
                SELECT t.tema,
                       COUNT(r.id) AS intentos,
                       COUNT(r.id) FILTER (WHERE r.es_correcta) AS correctas,
                       COUNT(DISTINCT r.usuario_id) AS estudiantes,
                       (SELECT COUNT(*) FROM preguntas p2 WHERE p2.tema = t.tema AND p2.activa) AS activas
                FROM (VALUES ('limites'), ('derivadas'), ('integrales')) AS t(tema)
                LEFT JOIN preguntas p ON p.tema = t.tema
                LEFT JOIN respuestas_estudiante r ON r.pregunta_id = p.id
                     AND r.usuario_id IN (SELECT u.id FROM usuarios u WHERE %s)
                GROUP BY t.tema
                ORDER BY CASE t.tema WHEN 'limites' THEN 1 WHEN 'derivadas' THEN 2 ELSE 3 END
                """.formatted(ES_ESTUDIANTE),
                (rs, i) -> {
                    long intentos = rs.getLong("intentos");
                    long correctas = rs.getLong("correctas");
                    return new TemaEstadistica(rs.getString("tema"), intentos, correctas,
                            porcentaje(correctas, intentos), rs.getLong("estudiantes"), rs.getLong("activas"));
                });
    }

    @Override
    public List<PreguntaDificil> preguntasDificiles(int limite) {
        return jdbc.query("""
                SELECT p.id, p.enunciado, p.tema, p.dificultad,
                       COUNT(r.id) AS intentos,
                       COUNT(r.id) FILTER (WHERE NOT r.es_correcta) AS fallos,
                       (SELECT o.texto FROM respuestas_estudiante r2 JOIN opciones o ON o.id = r2.opcion_id
                         WHERE r2.pregunta_id = p.id AND NOT r2.es_correcta
                         GROUP BY o.id, o.texto ORDER BY COUNT(*) DESC, o.id LIMIT 1) AS error_frecuente
                FROM preguntas p
                JOIN respuestas_estudiante r ON r.pregunta_id = p.id
                JOIN usuarios u ON u.id = r.usuario_id
                WHERE p.activa AND %s
                GROUP BY p.id
                HAVING COUNT(r.id) FILTER (WHERE NOT r.es_correcta) > 0
                ORDER BY COUNT(r.id) FILTER (WHERE NOT r.es_correcta)::float / COUNT(r.id) DESC,
                         COUNT(r.id) FILTER (WHERE NOT r.es_correcta) DESC, p.id
                LIMIT ?
                """.formatted(ES_ESTUDIANTE),
                (rs, i) -> {
                    long intentos = rs.getLong("intentos");
                    long fallos = rs.getLong("fallos");
                    return new PreguntaDificil(rs.getLong("id"), rs.getString("enunciado"), rs.getString("tema"),
                            rs.getString("dificultad"), intentos, fallos, porcentaje(fallos, intentos),
                            rs.getString("error_frecuente"));
                }, limite);
    }

    @Override
    public List<ActividadDia> actividad(int dias, Long usuarioId) {
        RowMapper<ActividadDia> mapper = (rs, i) -> new ActividadDia(
                rs.getDate("fecha").toLocalDate(), rs.getLong("intentos"), rs.getLong("estudiantes"));
        int desde = Math.max(dias, 1) - 1;
        return usuarioId == null
                ? jdbc.query(SQL_ACTIVIDAD_GRUPO, mapper, desde)
                : jdbc.query(SQL_ACTIVIDAD_ESTUDIANTE, mapper, desde, usuarioId);
    }

    @Override
    public List<IntentoReciente> ultimosIntentos(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT p.id, p.enunciado, p.tema, o.letra || ') ' || o.texto AS opcion, r.es_correcta, r.fecha_intento
                FROM respuestas_estudiante r
                JOIN preguntas p ON p.id = r.pregunta_id
                JOIN opciones o ON o.id = r.opcion_id
                WHERE r.usuario_id = ?
                ORDER BY r.fecha_intento DESC, r.id DESC
                LIMIT ?
                """,
                (rs, i) -> new IntentoReciente(rs.getLong("id"), rs.getString("enunciado"), rs.getString("tema"),
                        rs.getString("opcion"), rs.getBoolean("es_correcta"), fecha(rs, "fecha_intento")),
                usuarioId, limite);
    }

    @Override
    public List<PreguntaConFallos> preguntasConMasFallos(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT p.id, p.enunciado, p.tema,
                       COUNT(*) FILTER (WHERE NOT r.es_correcta) AS fallos,
                       BOOL_OR(r.es_correcta) AS resuelta
                FROM respuestas_estudiante r
                JOIN preguntas p ON p.id = r.pregunta_id
                WHERE r.usuario_id = ?
                GROUP BY p.id
                HAVING COUNT(*) FILTER (WHERE NOT r.es_correcta) > 0
                ORDER BY fallos DESC, p.id
                LIMIT ?
                """,
                (rs, i) -> new PreguntaConFallos(rs.getLong("id"), rs.getString("enunciado"), rs.getString("tema"),
                        rs.getLong("fallos"), rs.getBoolean("resuelta")),
                usuarioId, limite);
    }

    @Override
    public ParticipacionEstudiante participacion(Long usuarioId) {
        return jdbc.queryForObject("""
                SELECT (SELECT COUNT(*) FROM publicaciones WHERE usuario_id = ? AND activa) AS publicaciones,
                       (SELECT COUNT(*) FROM respuestas WHERE usuario_id = ? AND NOT oculta) AS respuestas,
                       (SELECT COUNT(*) FROM respuestas WHERE usuario_id = ? AND es_oficial) AS oficiales,
                       (SELECT COUNT(*) FROM usuario_logros WHERE usuario_id = ?) AS logros
                """,
                (rs, i) -> new ParticipacionEstudiante(rs.getLong("publicaciones"), rs.getLong("respuestas"),
                        rs.getLong("oficiales"), rs.getLong("logros")),
                usuarioId, usuarioId, usuarioId, usuarioId);
    }

    /** Porcentaje con un decimal (0 si no hay base). */
    static double porcentaje(long parte, long total) {
        return total == 0 ? 0.0 : Math.round(parte * 1000.0 / total) / 10.0;
    }

    @Override
    public List<QuizEstudiante> ultimosQuices(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT id, tema, COALESCE(correctas, 0) AS correctas, cantidad, iniciado_en, finalizado_en, fuera_de_tiempo
                FROM quiz_intentos
                WHERE usuario_id = ? AND finalizado_en IS NOT NULL
                ORDER BY finalizado_en DESC
                LIMIT ?
                """, (rs, i) -> new QuizEstudiante(rs.getLong("id"), rs.getString("tema"), rs.getInt("correctas"),
                        rs.getInt("cantidad"), fecha(rs, "iniciado_en"), fecha(rs, "finalizado_en"),
                        rs.getBoolean("fuera_de_tiempo")), usuarioId, limite);
    }
}
