package co.edu.ucundinamarca.saesu.tarea.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Tareas con SQL directo. El avance se calcula en la consulta (no se guarda). */
@Component
public class TareaJdbcAdapter implements TareaPort {

    private static final String ES_ESTUDIANTE = "UPPER(REPLACE(%s.rol, 'ROLE_', '')) = 'ESTUDIANTE'";

    /** Preguntas distintas del tema de la tarea que el estudiante resolvió bien desde que se creó. */
    private static final String RESUELTAS = """
            SELECT COUNT(DISTINCT r.pregunta_id)
            FROM respuestas_estudiante r
            JOIN preguntas p ON p.id = r.pregunta_id
            WHERE r.usuario_id = %s AND r.es_correcta AND p.tema = t.tema AND r.fecha_intento >= t.creada_en
            """;

    private static final String SQL_DEL_DOCENTE = """
            SELECT t.id, t.titulo, t.instrucciones, t.tema, t.meta_preguntas, t.fecha_limite, t.estudiante_id,
                   e.nombre AS estudiante, t.creada_en,
                   COUNT(a.id) AS asignados,
                   COUNT(a.id) FILTER (WHERE av.n >= t.meta_preguntas) AS completadas
            FROM tareas t
            LEFT JOIN usuarios e ON e.id = t.estudiante_id
            LEFT JOIN usuarios a ON a.id = t.estudiante_id OR (t.estudiante_id IS NULL AND %s)
            LEFT JOIN LATERAL (%s) av(n) ON TRUE
            WHERE t.docente_id = ?
            GROUP BY t.id, e.nombre
            ORDER BY t.fecha_limite DESC, t.id DESC
            """.formatted(ES_ESTUDIANTE.formatted("a"), RESUELTAS.formatted("a.id"));

    private static final String SQL_DEL_ESTUDIANTE = """
            SELECT t.id, t.titulo, t.instrucciones, t.tema, t.meta_preguntas, t.fecha_limite, d.nombre AS docente,
                   (%s) AS resueltas
            FROM tareas t
            JOIN usuarios d ON d.id = t.docente_id
            WHERE t.estudiante_id = ? OR t.estudiante_id IS NULL
            ORDER BY t.fecha_limite, t.id
            """.formatted(RESUELTAS.formatted("?"));

    private final JdbcTemplate jdbc;

    public TareaJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static LocalDate dia(ResultSet rs, String c) throws SQLException {
        Date d = rs.getDate(c);
        return d == null ? null : d.toLocalDate();
    }

    private static LocalDateTime fecha(ResultSet rs, String c) throws SQLException {
        Timestamp t = rs.getTimestamp(c);
        return t == null ? null : t.toLocalDateTime();
    }

    @Override
    public List<Pendiente> asignacionesQueVencen(LocalDate desde, LocalDate hasta) {
        return jdbc.query("""
                SELECT t.id, t.titulo, t.tema, t.meta_preguntas, t.fecha_limite, a.id AS usuario_id, a.correo, a.nombre,
                       (%s) AS resueltas
                FROM tareas t
                JOIN usuarios a ON a.id = t.estudiante_id OR (t.estudiante_id IS NULL AND %s)
                WHERE t.fecha_limite BETWEEN ? AND ?
                ORDER BY t.fecha_limite, t.id
                """.formatted(RESUELTAS.formatted("a.id"), ES_ESTUDIANTE.formatted("a")),
                (rs, i) -> new Pendiente(rs.getLong("id"), rs.getString("titulo"), rs.getString("tema"), rs.getInt("meta_preguntas"),
                        dia(rs, "fecha_limite"), rs.getLong("usuario_id"), rs.getString("correo"), rs.getString("nombre"),
                        rs.getLong("resueltas")),
                Date.valueOf(desde), Date.valueOf(hasta));
    }

    @Override
    public boolean marcarRecordatorio(Long tareaId, Long usuarioId, LocalDate dia) {
        return jdbc.update("INSERT INTO tarea_recordatorios (tarea_id, usuario_id, dia) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                tareaId, usuarioId, Date.valueOf(dia)) > 0;
    }

    @Override
    public Optional<Long> idPorCorreo(String correo) {
        return jdbc.query("SELECT id FROM usuarios WHERE LOWER(correo) = LOWER(?)", (rs, i) -> rs.getLong("id"), correo)
                .stream().findFirst();
    }

    @Override
    public boolean esEstudiante(Long usuarioId) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios u WHERE u.id = ? AND " + ES_ESTUDIANTE.formatted("u"),
                Long.class, usuarioId);
        return n != null && n > 0;
    }

    @Override
    public Long crear(TareaRequest t, Long docenteId, LocalDateTime creadaEn) {
        String instrucciones = t.instrucciones() == null || t.instrucciones().isBlank() ? null : t.instrucciones().trim();
        return jdbc.queryForObject("""
                INSERT INTO tareas (docente_id, estudiante_id, titulo, instrucciones, tema, meta_preguntas, fecha_limite, creada_en)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Long.class, docenteId, t.estudianteId(), t.titulo().trim(), instrucciones, t.tema(),
                t.metaPreguntas(), Date.valueOf(t.fechaLimite()), Timestamp.valueOf(creadaEn));
    }

    @Override
    public List<FilaTareaDocente> delDocente(Long docenteId) {
        return jdbc.query(SQL_DEL_DOCENTE, (rs, i) -> new FilaTareaDocente(
                rs.getLong("id"), rs.getString("titulo"), rs.getString("instrucciones"), rs.getString("tema"),
                rs.getInt("meta_preguntas"), dia(rs, "fecha_limite"),
                (Long) (rs.getObject("estudiante_id") == null ? null : rs.getLong("estudiante_id")),
                rs.getString("estudiante"), rs.getLong("asignados"), rs.getLong("completadas"), fecha(rs, "creada_en")),
                docenteId);
    }

    @Override
    public boolean eliminar(Long tareaId, Long docenteId) {
        return jdbc.update("DELETE FROM tareas WHERE id = ? AND docente_id = ?", tareaId, docenteId) > 0;
    }

    @Override
    public List<FilaTareaEstudiante> delEstudiante(Long estudianteId) {
        return jdbc.query(SQL_DEL_ESTUDIANTE, (rs, i) -> new FilaTareaEstudiante(
                rs.getLong("id"), rs.getString("titulo"), rs.getString("instrucciones"), rs.getString("tema"),
                rs.getInt("meta_preguntas"), dia(rs, "fecha_limite"), rs.getString("docente"), rs.getLong("resueltas")),
                estudianteId, estudianteId);
    }
}
