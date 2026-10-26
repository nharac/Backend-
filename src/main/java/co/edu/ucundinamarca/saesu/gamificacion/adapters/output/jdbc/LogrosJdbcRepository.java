package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Consultas de solo lectura que necesitan los logros, más el registro
 * de los logros desbloqueados (tabla usuario_logros, V11).
 * Se usa SQL directo porque cruza tablas de varios módulos (práctica, foro, gamificación).
 */
@Repository
public class LogrosJdbcRepository {

    private final JdbcTemplate jdbc;

    public LogrosJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private long contar(String sql, Object... args) {
        Long n = jdbc.queryForObject(sql, Long.class, args);
        return n == null ? 0 : n;
    }

    public long contarAciertos(Long usuarioId) {
        return contar("SELECT COUNT(*) FROM respuestas_estudiante WHERE usuario_id = ? AND es_correcta = TRUE", usuarioId);
    }

    /** Preguntas cuyo PRIMER intento del usuario fue correcto. */
    public long contarAcertadasAlPrimerIntento(Long usuarioId) {
        return contar("""
                SELECT COUNT(*) FROM (
                    SELECT DISTINCT ON (pregunta_id) pregunta_id, es_correcta
                    FROM respuestas_estudiante
                    WHERE usuario_id = ?
                    ORDER BY pregunta_id, fecha_intento, id
                ) primeros
                WHERE primeros.es_correcta = TRUE
                """, usuarioId);
    }

    public long contarPreguntasDelTema(String tema) {
        return contar("SELECT COUNT(*) FROM preguntas WHERE tema = ?", tema);
    }

    /** Preguntas distintas del tema que el usuario ha acertado al menos una vez. */
    public long contarResueltasDelTema(Long usuarioId, String tema) {
        return contar("""
                SELECT COUNT(DISTINCT r.pregunta_id)
                FROM respuestas_estudiante r
                JOIN preguntas p ON p.id = r.pregunta_id
                WHERE r.usuario_id = ? AND r.es_correcta = TRUE AND p.tema = ?
                """, usuarioId, tema);
    }

    public long rachaActual(Long usuarioId) {
        return contar("SELECT COALESCE(racha_actual, 0) FROM usuarios WHERE id = ?", usuarioId);
    }

    public long contarPublicaciones(Long usuarioId) {
        return contar("SELECT COUNT(*) FROM publicaciones WHERE usuario_id = ?", usuarioId);
    }

    public long contarRespuestasForo(Long usuarioId) {
        return contar("SELECT COUNT(*) FROM respuestas WHERE usuario_id = ?", usuarioId);
    }

    public long contarRespuestasOficiales(Long usuarioId) {
        return contar("SELECT COUNT(*) FROM respuestas WHERE usuario_id = ? AND es_oficial = TRUE", usuarioId);
    }

    public long puntosTotales(Long usuarioId) {
        return contar("SELECT COALESCE(SUM(cantidad), 0) FROM puntos_historial WHERE usuario_id = ?", usuarioId);
    }

    /** Logros ya desbloqueados: código → fecha. */
    public Map<String, OffsetDateTime> obtenidos(Long usuarioId) {
        Map<String, OffsetDateTime> mapa = new HashMap<>();
        jdbc.query("SELECT codigo, fecha_obtenido FROM usuario_logros WHERE usuario_id = ?",
                rs -> { mapa.put(rs.getString("codigo"), rs.getObject("fecha_obtenido", OffsetDateTime.class)); },
                usuarioId);
        return mapa;
    }

    /** Registra el logro; si ya existía no hace nada (UNIQUE usuario_id + codigo). */
    public boolean guardar(Long usuarioId, String codigo) {
        return jdbc.update("""
                INSERT INTO usuario_logros (usuario_id, codigo) VALUES (?, ?)
                ON CONFLICT (usuario_id, codigo) DO NOTHING
                """, usuarioId, codigo) > 0;
    }
}
