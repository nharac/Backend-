package co.edu.ucundinamarca.saesu.docente.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse.OpcionDocente;
import co.edu.ucundinamarca.saesu.docente.ports.output.BancoPreguntasPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Banco de preguntas con SQL directo.
 * Las opciones se editan por letra (UPDATE) para conservar su id: los intentos de los
 * estudiantes (respuestas_estudiante.opcion_id) siguen apuntando a la misma opción.
 */
@Component
public class BancoPreguntasJdbcAdapter implements BancoPreguntasPort {

    private final JdbcTemplate jdbc;
    /** Para "IN (:ids)": Spring expande la lista en parámetros, sin armar el SQL a mano. */
    private final NamedParameterJdbcTemplate jdbcConNombres;

    public BancoPreguntasJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.jdbcConNombres = new NamedParameterJdbcTemplate(jdbc);
    }

    private record FilaPregunta(Long id, String tema, String enunciado, String dificultad, String pista,
                                boolean activa, long intentos, long correctas, java.time.LocalDateTime fecha) {}

    @Override
    public List<PreguntaDocenteResponse> listar(String tema, String dificultad, String busqueda, Boolean activa) {
        return consultar(tema, dificultad, busqueda, activa, null);
    }

    private List<PreguntaDocenteResponse> consultar(String tema, String dificultad, String busqueda,
                                                    Boolean activa, Long id) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.tema, p.enunciado, p.dificultad, p.pista, p.activa, p.fecha_actualizacion,
                       COUNT(r.id) AS intentos, COUNT(r.id) FILTER (WHERE r.es_correcta) AS correctas
                FROM preguntas p
                LEFT JOIN respuestas_estudiante r ON r.pregunta_id = p.id
                WHERE 1 = 1
                """);
        List<Object> params = new ArrayList<>();
        if (tema != null && !tema.isBlank()) {
            sql.append(" AND p.tema = ?");
            params.add(tema);
        }
        if (dificultad != null && !dificultad.isBlank()) {
            sql.append(" AND p.dificultad = ?");
            params.add(dificultad);
        }
        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND LOWER(p.enunciado) LIKE ?");
            params.add("%" + busqueda.trim().toLowerCase() + "%");
        }
        if (activa != null) {
            sql.append(" AND p.activa = ?");
            params.add(activa);
        }
        if (id != null) {
            sql.append(" AND p.id = ?");
            params.add(id);
        }
        sql.append("""
                 GROUP BY p.id
                 ORDER BY CASE p.tema WHEN 'limites' THEN 1 WHEN 'derivadas' THEN 2 ELSE 3 END,
                          CASE p.dificultad WHEN 'basico' THEN 1 WHEN 'intermedio' THEN 2 ELSE 3 END, p.id
                """);

        List<FilaPregunta> filas = jdbc.query(sql.toString(), (rs, i) -> {
            Timestamp t = rs.getTimestamp("fecha_actualizacion");
            return new FilaPregunta(rs.getLong("id"), rs.getString("tema"), rs.getString("enunciado"),
                    rs.getString("dificultad"), rs.getString("pista"), rs.getBoolean("activa"),
                    rs.getLong("intentos"), rs.getLong("correctas"), t == null ? null : t.toLocalDateTime());
        }, params.toArray());

        Map<Long, List<OpcionDocente>> opciones = opcionesPorPregunta(filas.stream().map(FilaPregunta::id).toList());

        return filas.stream().map(f -> new PreguntaDocenteResponse(
                f.id(), f.tema(), f.enunciado(), f.dificultad(), f.pista(), f.activa(), f.intentos(),
                EstadisticasDocenteJdbcAdapter.porcentaje(f.correctas(), f.intentos()), f.fecha(),
                opciones.getOrDefault(f.id(), List.of()))).toList();
    }

    private Map<Long, List<OpcionDocente>> opcionesPorPregunta(List<Long> ids) {
        Map<Long, List<OpcionDocente>> mapa = new HashMap<>();
        if (ids.isEmpty()) {
            return mapa;
        }
        jdbcConNombres.query("""
                        SELECT id, pregunta_id, letra, texto, es_correcta FROM opciones
                        WHERE pregunta_id IN (:ids) ORDER BY pregunta_id, letra
                        """, Map.of("ids", ids),
                (ResultSet rs) -> {
                    mapa.computeIfAbsent(rs.getLong("pregunta_id"), k -> new ArrayList<>())
                            .add(new OpcionDocente(rs.getLong("id"), rs.getString("letra").trim(),
                                    rs.getString("texto"), rs.getBoolean("es_correcta")));
                });
        return mapa;
    }

    @Override
    public Optional<PreguntaDocenteResponse> buscarPorId(Long id) {
        return consultar(null, null, null, null, id).stream().findFirst();
    }

    @Override
    public boolean existeEnunciado(String enunciado, Long excluirId) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM preguntas WHERE LOWER(TRIM(enunciado)) = LOWER(TRIM(?)) AND id <> ?",
                Long.class, enunciado, excluirId == null ? -1L : excluirId);
        return n != null && n > 0;
    }

    @Override
    public Long crear(PreguntaDocenteRequest r, Long docenteId) {
        Long id = jdbc.queryForObject("""
                INSERT INTO preguntas (tema, enunciado, dificultad, pista, activa, creada_por)
                VALUES (?, ?, ?, ?, TRUE, ?)
                RETURNING id
                """, Long.class, r.tema(), r.enunciado().trim(), r.dificultad(), pistaLimpia(r.pista()), docenteId);
        for (PreguntaDocenteRequest.OpcionRequest o : r.opciones()) {
            jdbc.update("INSERT INTO opciones (pregunta_id, letra, texto, es_correcta) VALUES (?, ?, ?, ?)",
                    id, o.letra().trim().toUpperCase(), o.texto().trim(), o.esCorrecta());
        }
        return id;
    }

    @Override
    public void actualizar(Long id, PreguntaDocenteRequest r) {
        jdbc.update("""
                UPDATE preguntas
                SET tema = ?, enunciado = ?, dificultad = ?, pista = ?, fecha_actualizacion = NOW()
                WHERE id = ?
                """, r.tema(), r.enunciado().trim(), r.dificultad(), pistaLimpia(r.pista()), id);
        for (PreguntaDocenteRequest.OpcionRequest o : r.opciones()) {
            String letra = o.letra().trim().toUpperCase();
            int filas = jdbc.update("UPDATE opciones SET texto = ?, es_correcta = ? WHERE pregunta_id = ? AND letra = ?",
                    o.texto().trim(), o.esCorrecta(), id, letra);
            if (filas == 0) {
                jdbc.update("INSERT INTO opciones (pregunta_id, letra, texto, es_correcta) VALUES (?, ?, ?, ?)",
                        id, letra, o.texto().trim(), o.esCorrecta());
            }
        }
    }

    @Override
    public boolean cambiarEstado(Long id, boolean activa) {
        return jdbc.update("UPDATE preguntas SET activa = ?, fecha_actualizacion = NOW() WHERE id = ?", activa, id) > 0;
    }

    private static String pistaLimpia(String pista) {
        return pista == null || pista.isBlank() ? null : pista.trim();
    }
}
