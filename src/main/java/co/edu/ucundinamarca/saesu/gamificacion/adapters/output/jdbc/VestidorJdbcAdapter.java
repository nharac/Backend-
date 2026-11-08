package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Adaptador JDBC del vestidor de Sumi (sumi_catalogo, sumi_inventario y sumi_estilo). */
@Component
public class VestidorJdbcAdapter implements VestidorPort {

    /** Columnas de sumi_estilo, en orden. */
    public static final List<String> PARTES = List.of("sombrero", "cara", "objeto", "espalda", "bufanda", "fondo");

    private final JdbcTemplate jdbc;

    public VestidorJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UsuarioBasico usuario(String correo) {
        List<UsuarioBasico> u = jdbc.query("SELECT id, rol FROM usuarios WHERE LOWER(correo) = LOWER(?)",
                (rs, i) -> new UsuarioBasico(rs.getLong("id"), rs.getString("rol")), correo);
        return u.isEmpty() ? null : u.get(0);
    }

    @Override
    public Integer precio(String categoria, String prenda) {
        List<Integer> p = jdbc.query("SELECT precio FROM sumi_catalogo WHERE categoria = ? AND prenda = ?",
                (rs, i) -> rs.getInt("precio"), categoria, prenda);
        return p.isEmpty() ? null : p.get(0);
    }

    @Override
    public Set<String> inventario(Long usuarioId) {
        return new HashSet<>(jdbc.query("SELECT categoria, prenda FROM sumi_inventario WHERE usuario_id = ?",
                (rs, i) -> rs.getString("categoria") + ":" + rs.getString("prenda"), usuarioId));
    }

    @Override
    public boolean agregar(Long usuarioId, String categoria, String prenda) {
        return jdbc.update("""
                INSERT INTO sumi_inventario (usuario_id, categoria, prenda, fecha) VALUES (?, ?, ?, NOW())
                ON CONFLICT DO NOTHING
                """, usuarioId, categoria, prenda) > 0;
    }

    @Override
    public Map<String, String> estilo(Long usuarioId) {
        List<Map<String, String>> filas = jdbc.query(
                "SELECT sombrero, cara, objeto, espalda, bufanda, fondo FROM sumi_estilo WHERE usuario_id = ?",
                (rs, i) -> {
                    Map<String, String> m = new LinkedHashMap<>();
                    for (String parte : PARTES) {
                        m.put(parte, rs.getString(parte));
                    }
                    return m;
                }, usuarioId);
        return filas.isEmpty() ? Map.of() : filas.get(0);
    }

    @Override
    public void guardarEstilo(Long usuarioId, Map<String, String> estilo) {
        jdbc.update("""
                INSERT INTO sumi_estilo (usuario_id, sombrero, cara, objeto, espalda, bufanda, fondo, actualizado)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (usuario_id) DO UPDATE SET sombrero = EXCLUDED.sombrero, cara = EXCLUDED.cara,
                    objeto = EXCLUDED.objeto, espalda = EXCLUDED.espalda, bufanda = EXCLUDED.bufanda,
                    fondo = EXCLUDED.fondo, actualizado = NOW()
                """, usuarioId, estilo.get("sombrero"), estilo.get("cara"), estilo.get("objeto"),
                estilo.get("espalda"), estilo.get("bufanda"), estilo.get("fondo"));
    }
}
