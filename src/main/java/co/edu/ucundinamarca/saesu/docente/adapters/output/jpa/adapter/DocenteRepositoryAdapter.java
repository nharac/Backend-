package co.edu.ucundinamarca.saesu.docente.adapters.output.jpa.adapter;

import org.springframework.jdbc.core.JdbcTemplate;
import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocenteRepositoryAdapter implements DocenteRepositoryPort {

    private final JdbcTemplate jdbc;

    // Las tres operaciones van con SQL directo para que se apliquen en orden
    // dentro de la misma transacción (y para guardar quién marcó la oficial).

    @Override
    @Transactional
    public void marcarOficial(Long respuestaId, Long docenteId) {
        jdbc.update("""
                UPDATE respuestas
                SET es_oficial = TRUE, marcada_oficial_por = ?, fecha_marcada_oficial = NOW()
                WHERE id = ?
                """, docenteId, respuestaId);
    }

    @Override
    @Transactional
    public void quitarOficial(Long respuestaId) {
        jdbc.update("""
                UPDATE respuestas
                SET es_oficial = FALSE, marcada_oficial_por = NULL, fecha_marcada_oficial = NULL
                WHERE id = ?
                """, respuestaId);
    }

    @Override
    @Transactional
    public void quitarOficialesDePublicacion(Long publicacionId) {
        jdbc.update("""
                UPDATE respuestas
                SET es_oficial = FALSE, marcada_oficial_por = NULL, fecha_marcada_oficial = NULL
                WHERE publicacion_id = ? AND es_oficial = TRUE
                """, publicacionId);
    }

    // ============================================
    // Moderación del foro
    // ============================================

    @Override
    @Transactional
    public boolean ocultarRespuesta(Long respuestaId, Long docenteId) {
        // Una respuesta oculta deja de ser oficial
        return jdbc.update("""
                UPDATE respuestas
                SET oculta = TRUE, oculta_por = ?, fecha_oculta = NOW(),
                    es_oficial = FALSE, marcada_oficial_por = NULL, fecha_marcada_oficial = NULL
                WHERE id = ?
                """, docenteId, respuestaId) > 0;
    }

    @Override
    @Transactional
    public boolean restaurarRespuesta(Long respuestaId) {
        return jdbc.update("UPDATE respuestas SET oculta = FALSE, oculta_por = NULL, fecha_oculta = NULL WHERE id = ?",
                respuestaId) > 0;
    }

    @Override
    @Transactional
    public boolean ocultarPublicacion(Long publicacionId, Long docenteId) {
        return jdbc.update("UPDATE publicaciones SET activa = FALSE, oculta_por = ?, fecha_oculta = NOW() WHERE id = ?",
                docenteId, publicacionId) > 0;
    }

    @Override
    @Transactional
    public boolean restaurarPublicacion(Long publicacionId) {
        return jdbc.update("UPDATE publicaciones SET activa = TRUE, oculta_por = NULL, fecha_oculta = NULL WHERE id = ?",
                publicacionId) > 0;
    }

    @Override
    public List<ContenidoOcultoResponse> listarContenidoOculto() {
        // Solo lo que ocultó un docente (oculta_por no nulo); lo que borró el propio autor no se muestra
        List<ContenidoOcultoResponse> lista = new ArrayList<>(jdbc.query("""
                SELECT 'PUBLICACION' AS tipo, p.id, p.id AS publicacion_id, p.titulo,
                       p.contenido, a.nombre AS autor, p.fecha_creacion, d.nombre AS docente, p.fecha_oculta
                FROM publicaciones p
                JOIN usuarios a ON a.id = p.usuario_id
                LEFT JOIN usuarios d ON d.id = p.oculta_por
                WHERE p.activa = FALSE AND p.oculta_por IS NOT NULL
                UNION ALL
                SELECT 'RESPUESTA', r.id, r.publicacion_id, p.titulo,
                       r.contenido, a.nombre, r.fecha_creacion, d.nombre, r.fecha_oculta
                FROM respuestas r
                JOIN publicaciones p ON p.id = r.publicacion_id
                JOIN usuarios a ON a.id = r.usuario_id
                LEFT JOIN usuarios d ON d.id = r.oculta_por
                WHERE r.oculta = TRUE
                ORDER BY fecha_oculta DESC NULLS LAST
                """, (rs, i) -> new ContenidoOcultoResponse(
                        rs.getString("tipo"), rs.getLong("id"), rs.getLong("publicacion_id"),
                        rs.getString("titulo"), rs.getString("contenido"), rs.getString("autor"),
                        fecha(rs.getTimestamp("fecha_creacion")), rs.getString("docente"),
                        fecha(rs.getTimestamp("fecha_oculta")))));
        return lista;
    }

    private static java.time.LocalDateTime fecha(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
