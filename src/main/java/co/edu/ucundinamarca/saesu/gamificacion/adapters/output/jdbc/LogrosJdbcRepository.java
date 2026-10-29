package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consultas de solo lectura que necesitan los logros, más el registro
 * de los logros desbloqueados (tabla usuario_logros, V11).
 * Se usa SQL directo porque cruza tablas de varios módulos (práctica, foro, gamificación).
 *
 * Todas las cifras salen en dos consultas (antes eran unas 15, una por condición),
 * porque cada consulta es una ida y vuelta hasta Supabase.
 */
@Repository
public class LogrosJdbcRepository implements LogrosPort {

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

        return jdbc.queryForObject("""
                SELECT
                  (SELECT COUNT(*) FROM respuestas_estudiante WHERE usuario_id = ? AND es_correcta = TRUE) AS aciertos,
                  (SELECT COUNT(*) FROM (
                       SELECT DISTINCT ON (pregunta_id) es_correcta
                       FROM respuestas_estudiante
                       WHERE usuario_id = ?
                       ORDER BY pregunta_id, fecha_intento, id
                   ) primeros WHERE primeros.es_correcta = TRUE) AS primera,
                  (SELECT COALESCE(racha_actual, 0) FROM usuarios WHERE id = ?) AS racha,
                  (SELECT COUNT(*) FROM publicaciones WHERE usuario_id = ?) AS publicaciones,
                  (SELECT COUNT(*) FROM respuestas WHERE usuario_id = ?) AS respuestas_foro,
                  (SELECT COUNT(*) FROM respuestas WHERE usuario_id = ? AND es_oficial = TRUE) AS oficiales,
                  (SELECT COALESCE(SUM(cantidad), 0) FROM puntos_historial WHERE usuario_id = ?) AS puntos
                """, (rs, i) -> new Estadisticas(
                        rs.getLong("aciertos"), rs.getLong("primera"), rs.getLong("racha"),
                        rs.getLong("publicaciones"), rs.getLong("respuestas_foro"), rs.getLong("oficiales"),
                        rs.getLong("puntos"), temas),
                usuarioId, usuarioId, usuarioId, usuarioId, usuarioId, usuarioId, usuarioId);
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
