package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort;
import co.edu.ucundinamarca.saesu.core.config.TiempoConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Consultas de solo lectura sobre respuestas_estudiante para el perfil,
 * más la meta semanal (columna usuarios.meta_semanal, V22).
 * Las fechas se agrupan en hora de Colombia para que "hoy" sea el mismo día que ve el estudiante.
 */
@Repository
public class MiAvanceJdbcAdapter implements MiAvancePort {

    private final JdbcTemplate jdbc;

    /** Zona del negocio para cortar los días (SAESU_ZONA_HORARIA). */
    private final ZoneId zona;

    public MiAvanceJdbcAdapter(JdbcTemplate jdbc, ZoneId zona) {
        this.jdbc = jdbc;
        this.zona = zona;
    }

    private String sql(String plantilla) {
        return TiempoConfig.conZona(plantilla, zona);
    }

    @Override
    public List<Dia> actividadDesde(Long usuarioId, LocalDate desde) {
        return jdbc.query(sql("""
                SELECT CAST(fecha_intento AT TIME ZONE {zona} AS DATE) AS dia,
                       COUNT(*) AS respondidas,
                       COUNT(*) FILTER (WHERE es_correcta) AS aciertos
                FROM respuestas_estudiante
                WHERE usuario_id = ?
                  AND CAST(fecha_intento AT TIME ZONE {zona} AS DATE) >= ?
                GROUP BY 1
                ORDER BY 1
                """), (rs, i) -> new Dia(rs.getDate("dia").toLocalDate(), rs.getLong("respondidas"), rs.getLong("aciertos")),
                usuarioId, Date.valueOf(desde));
    }

    @Override
    public List<Rendimiento> rendimiento(Long usuarioId) {
        return jdbc.query("""
                SELECT p.tema, p.dificultad,
                       COUNT(*) AS respondidas,
                       COUNT(*) FILTER (WHERE r.es_correcta) AS correctas
                FROM respuestas_estudiante r
                JOIN preguntas p ON p.id = r.pregunta_id
                WHERE r.usuario_id = ?
                GROUP BY p.tema, p.dificultad
                """, (rs, i) -> new Rendimiento(rs.getString("tema"), rs.getString("dificultad"),
                        rs.getLong("respondidas"), rs.getLong("correctas")),
                usuarioId);
    }

    @Override
    public int metaSemanal(Long usuarioId) {
        List<Integer> meta = jdbc.query("SELECT meta_semanal FROM usuarios WHERE id = ?",
                (rs, i) -> rs.getInt("meta_semanal"), usuarioId);
        return meta.isEmpty() ? 0 : meta.get(0);
    }

    @Override
    public void guardarMetaSemanal(Long usuarioId, int meta) {
        jdbc.update("UPDATE usuarios SET meta_semanal = ? WHERE id = ?", meta, usuarioId);
    }
}
