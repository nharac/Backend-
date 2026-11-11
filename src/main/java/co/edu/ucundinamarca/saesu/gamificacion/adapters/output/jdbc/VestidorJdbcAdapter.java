package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.ParteSumi;
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

    /** Columnas de sumi_estilo, en orden (vienen del enum ParteSumi, nunca de datos del usuario). */
    public static final List<String> PARTES = ParteSumi.ids();
    private static final String COLUMNAS = String.join(", ", PARTES);
    private static final String SQL_ESTILO = "SELECT " + COLUMNAS + " FROM sumi_estilo WHERE usuario_id = ?";
    private static final String SQL_GUARDAR_ESTILO = "INSERT INTO sumi_estilo (usuario_id, " + COLUMNAS + ", actualizado) VALUES (?, "
            + String.join(", ", java.util.Collections.nCopies(PARTES.size(), "?")) + ", NOW()) "
            + "ON CONFLICT (usuario_id) DO UPDATE SET "
            + PARTES.stream().map(c -> c + " = EXCLUDED." + c).collect(java.util.stream.Collectors.joining(", "))
            + ", actualizado = NOW()";

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
        List<Map<String, String>> filas = jdbc.query(SQL_ESTILO,
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
        List<Object> args = new java.util.ArrayList<>();
        args.add(usuarioId);
        PARTES.forEach(p -> args.add(estilo.get(p)));
        jdbc.update(SQL_GUARDAR_ESTILO, args.toArray());
    }
}
