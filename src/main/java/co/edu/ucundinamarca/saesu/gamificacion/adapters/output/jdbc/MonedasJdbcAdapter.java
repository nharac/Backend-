package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.MonedasPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/** Adaptador JDBC de las Sumi Monedas (usuarios.monedas y monedas_historial). */
@Component
public class MonedasJdbcAdapter implements MonedasPort {

    private static final String HISTORIAL =
            "INSERT INTO monedas_historial (usuario_id, cantidad, motivo, fecha) VALUES (?, ?, ?, NOW())";

    private final JdbcTemplate jdbc;

    public MonedasJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void sumar(Long usuarioId, int cantidad, String motivo) {
        jdbc.update("UPDATE usuarios SET monedas = monedas + ? WHERE id = ?", cantidad, usuarioId);
        jdbc.update(HISTORIAL, usuarioId, cantidad, recortar(motivo));
    }

    @Override
    public boolean gastar(Long usuarioId, int cantidad, String motivo) {
        int filas = jdbc.update("UPDATE usuarios SET monedas = monedas - ? WHERE id = ? AND monedas >= ?",
                cantidad, usuarioId, cantidad);
        if (filas == 0) {
            return false;
        }
        jdbc.update(HISTORIAL, usuarioId, -cantidad, recortar(motivo));
        return true;
    }

    @Override
    public int saldo(Long usuarioId) {
        List<Integer> saldo = jdbc.query("SELECT monedas FROM usuarios WHERE id = ?", (rs, i) -> rs.getInt("monedas"), usuarioId);
        return saldo.isEmpty() ? 0 : saldo.get(0);
    }

    @Override
    public List<MovimientoMonedaResponse> historial(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT cantidad, motivo, fecha FROM monedas_historial
                WHERE usuario_id = ? ORDER BY fecha DESC, id DESC LIMIT ?
                """, (rs, i) -> new MovimientoMonedaResponse(rs.getInt("cantidad"), rs.getString("motivo"),
                rs.getTimestamp("fecha").toLocalDateTime()), usuarioId, limite);
    }

    private static String recortar(String motivo) {
        return motivo.length() > 150 ? motivo.substring(0, 150) : motivo;
    }
}
