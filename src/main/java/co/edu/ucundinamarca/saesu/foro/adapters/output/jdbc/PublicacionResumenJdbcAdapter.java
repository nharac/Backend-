package co.edu.ucundinamarca.saesu.foro.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.foro.domain.model.PublicacionResumen;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionResumenPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Listados del foro con SQL directo. Antes se hacía una consulta por publicación para el autor
 * y otra para contar respuestas (con 10 publicaciones eran más de 30 idas a Supabase);
 * aquí todo sale en una sola consulta.
 */
@Component
public class PublicacionResumenJdbcAdapter implements PublicacionResumenPort {

    private static final String SELECT = """
            SELECT p.id, p.titulo, LEFT(p.contenido, %d) AS inicio, p.tema, p.fecha_creacion, p.imagen_url,
                   u.id AS autor_id, u.nombre AS autor_nombre, u.rol AS autor_rol,
                   (SELECT COUNT(*) FROM respuestas r WHERE r.publicacion_id = p.id) AS respuestas
            FROM publicaciones p
            JOIN usuarios u ON u.id = p.usuario_id
            """.formatted(LARGO_INICIO);

    private static final RowMapper<PublicacionResumen> FILA = (rs, i) -> {
        Timestamp fecha = rs.getTimestamp("fecha_creacion");
        return new PublicacionResumen(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("inicio"),
                rs.getString("tema"),
                fecha == null ? null : fecha.toLocalDateTime(),
                rs.getString("imagen_url"),
                rs.getLong("autor_id"),
                rs.getString("autor_nombre"),
                rs.getString("autor_rol"),
                rs.getLong("respuestas"));
    };

    private final JdbcTemplate jdbc;

    public PublicacionResumenJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<PublicacionResumen> listar(String tema, String busqueda, String orden, int page, int size) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE p.activa = TRUE");
        List<Object> args = new ArrayList<>();
        if (tema != null && !tema.isBlank()) {
            sql.append(" AND p.tema = ?");
            args.add(tema);
        }
        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (p.titulo ILIKE ? ESCAPE '\\' OR p.contenido ILIKE ? ESCAPE '\\')");
            String patron = "%" + escaparLike(busqueda.trim()) + "%";
            args.add(patron);
            args.add(patron);
        }
        sql.append(" ORDER BY ").append(ordenSql(orden)).append(" LIMIT ? OFFSET ?");
        int tamano = Math.max(1, Math.min(size, 100));
        args.add(tamano);
        args.add((long) Math.max(page, 0) * tamano);
        return jdbc.query(sql.toString(), FILA, args.toArray());
    }

    @Override
    public List<PublicacionResumen> listarSinRespuestaOficial() {
        return jdbc.query(SELECT + """
                 WHERE p.activa = TRUE
                   AND NOT EXISTS (SELECT 1 FROM respuestas r
                                   WHERE r.publicacion_id = p.id AND r.es_oficial = TRUE)
                 ORDER BY p.fecha_creacion DESC, p.id DESC
                """, FILA);
    }

    private static String ordenSql(String orden) {
        if ("antiguo".equalsIgnoreCase(orden)) return "p.fecha_creacion ASC, p.id ASC";
        if ("respuestas".equalsIgnoreCase(orden)) return "respuestas DESC, p.fecha_creacion DESC, p.id DESC";
        return "p.fecha_creacion DESC, p.id DESC";
    }

    /** Para que un % o _ escrito por el usuario se busque como texto y no como comodín. */
    static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
