package co.edu.ucundinamarca.saesu.foro.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.foro.ports.output.UtilesPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Adaptador JDBC de los "Me sirvió" (tabla respuestas_utiles). */
@Component
public class UtilesJdbcAdapter implements UtilesPort {

    private final JdbcTemplate jdbc;

    public UtilesJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long idUsuario(String correo) {
        List<Long> ids = jdbc.query("SELECT id FROM usuarios WHERE LOWER(correo) = LOWER(?)",
                (rs, i) -> rs.getLong("id"), correo);
        return ids.isEmpty() ? null : ids.get(0);
    }

    @Override
    public boolean respuestaVisible(Long respuestaId) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM respuestas WHERE id = ? AND oculta_por IS NULL", Integer.class, respuestaId);
        return n != null && n > 0;
    }

    @Override
    public Map<Long, Long> contarPorPublicacion(Long publicacionId) {
        Map<Long, Long> mapa = new HashMap<>();
        jdbc.query("""
                SELECT ru.respuesta_id, COUNT(*) AS total
                FROM respuestas_utiles ru JOIN respuestas r ON r.id = ru.respuesta_id
                WHERE r.publicacion_id = ?
                GROUP BY ru.respuesta_id
                """, rs -> { mapa.put(rs.getLong("respuesta_id"), rs.getLong("total")); }, publicacionId);
        return mapa;
    }

    @Override
    public Set<Long> marcadasPor(Long publicacionId, Long usuarioId) {
        return new HashSet<>(jdbc.query("""
                SELECT ru.respuesta_id FROM respuestas_utiles ru JOIN respuestas r ON r.id = ru.respuesta_id
                WHERE r.publicacion_id = ? AND ru.usuario_id = ?
                """, (rs, i) -> rs.getLong("respuesta_id"), publicacionId, usuarioId));
    }

    @Override
    public boolean marcar(Long respuestaId, Long usuarioId) {
        return jdbc.update("INSERT INTO respuestas_utiles (respuesta_id, usuario_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                respuestaId, usuarioId) > 0;
    }

    @Override
    public boolean desmarcar(Long respuestaId, Long usuarioId) {
        return jdbc.update("DELETE FROM respuestas_utiles WHERE respuesta_id = ? AND usuario_id = ?", respuestaId, usuarioId) > 0;
    }

    @Override
    public long contar(Long respuestaId) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM respuestas_utiles WHERE respuesta_id = ?", Long.class, respuestaId);
        return n == null ? 0 : n;
    }
}
