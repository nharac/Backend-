package co.edu.ucundinamarca.saesu.usuario.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CodigoRecuperacionJdbcAdapter implements CodigoRecuperacionPort {

    private final JdbcTemplate jdbc;

    public CodigoRecuperacionJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<CodigoActivo> buscarActivo(Long usuarioId, int maxIntentos) {
        return jdbc.query("""
                SELECT id, codigo_hash, intentos
                FROM codigos_recuperacion
                WHERE usuario_id = ? AND NOT usado AND expira_en > NOW() AND intentos < ?
                ORDER BY creado_en DESC
                LIMIT 1
                """, (rs, i) -> new CodigoActivo(rs.getLong("id"), rs.getString("codigo_hash"), rs.getInt("intentos")),
                usuarioId, maxIntentos).stream().findFirst();
    }

    @Override
    public Optional<LocalDateTime> fechaUltimoCodigo(Long usuarioId) {
        return jdbc.query("SELECT MAX(creado_en) AS f FROM codigos_recuperacion WHERE usuario_id = ?",
                (rs, i) -> {
                    Timestamp t = rs.getTimestamp("f");
                    return t == null ? null : t.toLocalDateTime();
                }, usuarioId).stream().filter(f -> f != null).findFirst();
    }

    @Override
    public void invalidarActivos(Long usuarioId) {
        jdbc.update("UPDATE codigos_recuperacion SET usado = TRUE WHERE usuario_id = ? AND NOT usado", usuarioId);
    }

    @Override
    public void guardar(Long usuarioId, String codigoHash, int minutosVigencia) {
        jdbc.update("""
                INSERT INTO codigos_recuperacion (usuario_id, codigo_hash, expira_en)
                VALUES (?, ?, NOW() + make_interval(mins => ?))
                """, usuarioId, codigoHash, minutosVigencia);
    }

    @Override
    public void sumarIntento(Long codigoId) {
        jdbc.update("UPDATE codigos_recuperacion SET intentos = intentos + 1 WHERE id = ?", codigoId);
    }

    @Override
    public void marcarUsado(Long codigoId) {
        jdbc.update("UPDATE codigos_recuperacion SET usado = TRUE WHERE id = ?", codigoId);
    }

    @Override
    public void actualizarPassword(Long usuarioId, String passwordHash) {
        jdbc.update("UPDATE usuarios SET password = ? WHERE id = ?", passwordHash, usuarioId);
    }
}
