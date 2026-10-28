package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Ranking con SQL directo (solo lectura).
 * Solo cuentan los estudiantes (el rol se compara normalizado) y solo quienes tienen puntos en el periodo.
 * Empates: queda arriba quien llegó primero al sistema (id menor), igual en la lista y en "mi posición".
 */
@Component
public class RankingJdbcAdapter implements RankingPort {

    private static final String HOY = "(NOW() AT TIME ZONE 'America/Bogota')::date";
    private static final String RACHA =
            "CASE WHEN u.ultima_fecha_practica >= " + HOY + " - 1 THEN COALESCE(u.racha_actual, 0) ELSE 0 END";

    /** Puntos por estudiante en el periodo, con su posición. */
    private static final String RANKING = """
            WITH puntos AS (
                SELECT u.id, u.nombre, u.foto_perfil_url, %s AS racha, SUM(p.cantidad) AS puntos
                FROM usuarios u
                JOIN puntos_historial p ON p.usuario_id = u.id
                WHERE UPPER(REPLACE(TRIM(u.rol), 'ROLE_', '')) = 'ESTUDIANTE'
                  AND p.fecha >= ?
                GROUP BY u.id
                HAVING SUM(p.cantidad) > 0
            ), ordenado AS (
                SELECT puntos.*,
                       ROW_NUMBER() OVER (ORDER BY puntos DESC, id) AS posicion,
                       COUNT(*) OVER () AS participantes,
                       LAG(puntos) OVER (ORDER BY puntos DESC, id) AS puntos_arriba,
                       LAG(nombre) OVER (ORDER BY puntos DESC, id) AS nombre_arriba
                FROM puntos
            )
            """.formatted(RACHA);

    /** "Desde siempre": cualquier fecha anterior a la primera migración sirve. */
    private static final LocalDateTime INICIO = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final JdbcTemplate jdbc;

    public RankingJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Timestamp ts(LocalDateTime desde) {
        return Timestamp.valueOf(desde == null ? INICIO : desde);
    }

    @Override
    public List<FilaRanking> top(LocalDateTime desde, int limite) {
        return jdbc.query(RANKING + "SELECT * FROM ordenado ORDER BY posicion LIMIT ?",
                (rs, i) -> new FilaRanking(
                        rs.getInt("posicion"),
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("foto_perfil_url"),
                        rs.getLong("puntos"),
                        rs.getInt("racha")),
                ts(desde), limite);
    }

    @Override
    public Optional<PosicionUsuario> posicionDe(Long usuarioId, LocalDateTime desde) {
        return jdbc.query(RANKING + "SELECT * FROM ordenado WHERE id = ?",
                (rs, i) -> new PosicionUsuario(
                        rs.getInt("posicion"),
                        rs.getLong("puntos"),
                        rs.getLong("participantes"),
                        (Long) rs.getObject("puntos_arriba", Long.class),
                        rs.getString("nombre_arriba")),
                ts(desde), usuarioId).stream().findFirst();
    }

    @Override
    public long participantes(LocalDateTime desde) {
        Long n = jdbc.queryForObject(RANKING + "SELECT COUNT(*) FROM ordenado", Long.class, ts(desde));
        return n == null ? 0 : n;
    }

    @Override
    public ResumenPuntos resumenPuntos(Long usuarioId, LocalDateTime inicioHoy, LocalDateTime inicioSemana) {
        List<ResumenPuntos> r = jdbc.query("SELECT " + RACHA + " AS racha, "
                        + "COALESCE((SELECT SUM(cantidad) FILTER (WHERE fecha >= ?) FROM puntos_historial WHERE usuario_id = u.id), 0) AS hoy, "
                        + "COALESCE((SELECT SUM(cantidad) FILTER (WHERE fecha >= ?) FROM puntos_historial WHERE usuario_id = u.id), 0) AS semana, "
                        + "COALESCE((SELECT SUM(cantidad) FROM puntos_historial WHERE usuario_id = u.id), 0) AS total "
                        + "FROM usuarios u WHERE u.id = ?",
                (rs, i) -> new ResumenPuntos(rs.getInt("racha"), rs.getLong("hoy"), rs.getLong("semana"), rs.getLong("total")),
                ts(inicioHoy), ts(inicioSemana), usuarioId);
        return r.isEmpty() ? new ResumenPuntos(0, 0, 0, 0) : r.get(0);
    }

    @Override
    public List<Movimiento> ultimosMovimientos(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT cantidad, motivo, fecha FROM puntos_historial
                WHERE usuario_id = ? ORDER BY fecha DESC, id DESC LIMIT ?
                """,
                (rs, i) -> new Movimiento(rs.getInt("cantidad"), rs.getString("motivo"),
                        rs.getTimestamp("fecha").toLocalDateTime()),
                usuarioId, limite);
    }
}
