package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Cifra;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Consultas de solo lectura que necesitan los logros, más el registro
 * de los logros desbloqueados (tabla usuario_logros, V11).
 * Se usa SQL directo porque cruza tablas de varios módulos (práctica, foro, gamificación).
 *
 * Todas las cifras salen en dos consultas (temas y cifras), porque cada consulta
 * es una ida y vuelta hasta Supabase.
 */
@Repository
public class LogrosJdbcRepository implements LogrosPort {

    /** Las cifras que salen de la consulta CIFRAS (cada columna se llama como la cifra en minúscula). */
    static final List<Cifra> CIFRAS_SQL = List.of(
            Cifra.RESPONDIDAS, Cifra.ACIERTOS, Cifra.PRIMERA, Cifra.AVANZADAS, Cifra.TOTAL_AVANZADAS,
            Cifra.MAX_ACIERTOS_DIA, Cifra.MAX_SEGUIDAS, Cifra.NOCTURNAS, Cifra.MADRUGADAS, Cifra.RACHA,
            Cifra.QUICES, Cifra.QUICES_PERFECTOS, Cifra.PUBLICACIONES, Cifra.RESPUESTAS_FORO,
            Cifra.OFICIALES, Cifra.MENSAJES_TUTOR, Cifra.PUNTOS);

    /**
     * Todas las cifras en una sola consulta (una ida y vuelta a la base de datos).
     * El id del usuario entra una sola vez, en el CTE "u". Las horas se miden en hora de Colombia.
     */
    private static final String CIFRAS = """
            WITH u AS (SELECT CAST(? AS BIGINT) AS id),
            r AS (
                SELECT re.pregunta_id, re.es_correcta, re.fecha_intento, re.id,
                       re.fecha_intento AT TIME ZONE 'America/Bogota' AS local
                FROM respuestas_estudiante re WHERE re.usuario_id = (SELECT id FROM u)
            )
            SELECT
              (SELECT COUNT(*) FROM r) AS respondidas,
              (SELECT COUNT(*) FROM r WHERE es_correcta) AS aciertos,
              (SELECT COUNT(*) FROM (
                   SELECT DISTINCT ON (pregunta_id) es_correcta FROM r ORDER BY pregunta_id, fecha_intento, id
               ) primeros WHERE primeros.es_correcta) AS primera,
              (SELECT COUNT(DISTINCT r.pregunta_id) FROM r JOIN preguntas p ON p.id = r.pregunta_id
                WHERE r.es_correcta AND p.dificultad = 'avanzado' AND p.activa) AS avanzadas,
              (SELECT COUNT(*) FROM preguntas WHERE dificultad = 'avanzado' AND activa) AS total_avanzadas,
              (SELECT COALESCE(MAX(n), 0) FROM (
                   SELECT COUNT(*) AS n FROM r WHERE es_correcta GROUP BY CAST(local AS DATE)) d) AS max_aciertos_dia,
              (SELECT COALESCE(MAX(n), 0) FROM (
                   SELECT COUNT(*) AS n FROM (
                       SELECT es_correcta,
                              SUM(CASE WHEN es_correcta THEN 0 ELSE 1 END) OVER (ORDER BY fecha_intento, id) AS tramo
                       FROM r) t
                   WHERE es_correcta GROUP BY tramo) s) AS max_seguidas,
              (SELECT COUNT(*) FROM r WHERE EXTRACT(HOUR FROM local) >= 22 OR EXTRACT(HOUR FROM local) < 4) AS nocturnas,
              (SELECT COUNT(*) FROM r WHERE EXTRACT(HOUR FROM local) >= 5 AND EXTRACT(HOUR FROM local) < 7) AS madrugadas,
              (SELECT COALESCE(racha_actual, 0) FROM usuarios WHERE id = (SELECT id FROM u)) AS racha,
              (SELECT COUNT(*) FROM quiz_intentos
                WHERE usuario_id = (SELECT id FROM u) AND finalizado_en IS NOT NULL) AS quices,
              (SELECT COUNT(*) FROM quiz_intentos
                WHERE usuario_id = (SELECT id FROM u) AND finalizado_en IS NOT NULL
                  AND cantidad >= 10 AND correctas = cantidad) AS quices_perfectos,
              (SELECT COUNT(*) FROM publicaciones WHERE usuario_id = (SELECT id FROM u)) AS publicaciones,
              (SELECT COUNT(*) FROM respuestas WHERE usuario_id = (SELECT id FROM u)) AS respuestas_foro,
              (SELECT COUNT(*) FROM respuestas WHERE usuario_id = (SELECT id FROM u) AND es_oficial) AS oficiales,
              (SELECT COUNT(*) FROM chat_mensaje
                WHERE usuario_id = (SELECT id FROM u) AND rol_emisor = 'usuario') AS mensajes_tutor,
              (SELECT COALESCE(SUM(cantidad), 0) FROM puntos_historial WHERE usuario_id = (SELECT id FROM u)) AS puntos,
              (SELECT correo FROM usuarios WHERE id = (SELECT id FROM u)) AS correo
            """;

    private final JdbcTemplate jdbc;

    public LogrosJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Estadisticas estadisticas(Long usuarioId) {
        Map<String, long[]> temas = new HashMap<>();
        jdbc.query("""
                SELECT p.tema,
                       COUNT(*) AS total,
                       COUNT(*) FILTER (WHERE EXISTS (
                           SELECT 1 FROM respuestas_estudiante r
                           WHERE r.pregunta_id = p.id AND r.usuario_id = ? AND r.es_correcta = TRUE)) AS resueltas
                FROM preguntas p
                WHERE p.activa
                GROUP BY p.tema
                """, rs -> {
            temas.put(rs.getString("tema"), new long[]{rs.getLong("total"), rs.getLong("resueltas")});
        }, usuarioId);

        Map<Cifra, Long> cifras = new EnumMap<>(Cifra.class);
        String[] correo = {null};
        jdbc.query(CIFRAS, rs -> {
            for (Cifra c : CIFRAS_SQL) {
                cifras.put(c, rs.getLong(c.name().toLowerCase(Locale.ROOT)));
            }
            correo[0] = rs.getString("correo");
        }, usuarioId);
        return new Estadisticas(cifras, temas, correo[0]);
    }

    /** Racha y puntos totales para el perfil, en una sola consulta: {racha, puntos}. */
    @Override
    public long[] rachaYPuntos(Long usuarioId) {
        List<long[]> r = jdbc.query("""
                SELECT COALESCE(u.racha_actual, 0) AS racha,
                       COALESCE((SELECT SUM(cantidad) FROM puntos_historial WHERE usuario_id = u.id), 0) AS puntos
                FROM usuarios u WHERE u.id = ?
                """, (rs, i) -> new long[]{rs.getLong("racha"), rs.getLong("puntos")}, usuarioId);
        return r.isEmpty() ? new long[]{0, 0} : r.get(0);
    }

    /** Logros ya desbloqueados: código → fecha. */
    @Override
    public Map<String, OffsetDateTime> obtenidos(Long usuarioId) {
        Map<String, OffsetDateTime> mapa = new HashMap<>();
        jdbc.query("SELECT codigo, fecha_obtenido FROM usuario_logros WHERE usuario_id = ?",
                rs -> { mapa.put(rs.getString("codigo"), rs.getObject("fecha_obtenido", OffsetDateTime.class)); },
                usuarioId);
        return mapa;
    }

    /** Registra el logro; si ya existía no hace nada (UNIQUE usuario_id + codigo). */
    @Override
    public boolean guardar(Long usuarioId, String codigo) {
        return jdbc.update("""
                INSERT INTO usuario_logros (usuario_id, codigo) VALUES (?, ?)
                ON CONFLICT (usuario_id, codigo) DO NOTHING
                """, usuarioId, codigo) > 0;
    }
}
