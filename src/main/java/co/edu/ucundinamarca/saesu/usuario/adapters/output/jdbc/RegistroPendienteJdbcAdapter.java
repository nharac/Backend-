package co.edu.ucundinamarca.saesu.usuario.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Las fechas se comparan con NOW() de la base de datos para no depender de la zona horaria del servidor. */
@Component
public class RegistroPendienteJdbcAdapter implements RegistroPendientePort {

    private final JdbcTemplate jdbc;

    public RegistroPendienteJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Pendiente> buscar(String correo) {
        return jdbc.query("""
                SELECT id, correo, nombre, password_hash, codigo_hash, intentos,
                       expira_en > NOW() AS vigente,
                       EXTRACT(EPOCH FROM (NOW() - ultimo_envio))::BIGINT AS segundos
                FROM registros_pendientes
                WHERE LOWER(correo) = LOWER(?)
                """,
                (rs, i) -> new Pendiente(rs.getLong("id"), rs.getString("correo"), rs.getString("nombre"),
                        rs.getString("password_hash"), rs.getString("codigo_hash"), rs.getInt("intentos"),
                        rs.getBoolean("vigente"), rs.getLong("segundos")),
                correo).stream().findFirst();
    }

    @Override
    public void guardar(String correo, String nombre, String passwordHash, String codigoHash, int minutosVigencia) {
        jdbc.update("""
                INSERT INTO registros_pendientes (correo, nombre, password_hash, codigo_hash, expira_en)
                VALUES (?, ?, ?, ?, NOW() + make_interval(mins => ?))
                ON CONFLICT (correo) DO UPDATE SET
                    nombre = EXCLUDED.nombre, password_hash = EXCLUDED.password_hash,
                    codigo_hash = EXCLUDED.codigo_hash, expira_en = EXCLUDED.expira_en,
                    intentos = 0, ultimo_envio = NOW()
                """, correo, nombre, passwordHash, codigoHash, minutosVigencia);
    }

    @Override
    public void renovarCodigo(Long id, String codigoHash, int minutosVigencia) {
        jdbc.update("""
                UPDATE registros_pendientes
                SET codigo_hash = ?, expira_en = NOW() + make_interval(mins => ?), intentos = 0, ultimo_envio = NOW()
                WHERE id = ?
                """, codigoHash, minutosVigencia, id);
    }

    @Override
    public void sumarIntento(Long id) {
        jdbc.update("UPDATE registros_pendientes SET intentos = intentos + 1 WHERE id = ?", id);
    }

    @Override
    public void eliminar(Long id) {
        jdbc.update("DELETE FROM registros_pendientes WHERE id = ?", id);
    }

    @Override
    public void limpiarAbandonados() {
        jdbc.update("DELETE FROM registros_pendientes WHERE ultimo_envio < NOW() - INTERVAL '1 day'");
    }
}
