package co.edu.ucundinamarca.saesu.docente.adapters.output.jpa.adapter;

import org.springframework.jdbc.core.JdbcTemplate;
import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.PublicacionEntity;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper.PublicacionMapper;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.PublicacionJpaRepository;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.RespuestaJpaRepository;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocenteRepositoryAdapter implements DocenteRepositoryPort {

    private final PublicacionJpaRepository publicacionJpa;
    private final RespuestaJpaRepository respuestaJpa;
    private final PublicacionMapper publicacionMapper;
    private final JdbcTemplate jdbc;

    @Override
    public List<Publicacion> listarSinRespuestaOficial() {
        // Usamos una query personalizada o filtramos en memoria (para mantenerlo simple)
        // Nota: Lo ideal es hacer el JOIN en la query, pero para velocidad usaremos esto.
        return publicacionJpa.findAll().stream()
                .filter(p -> respuestaJpa.listarPorPublicacion(p.getId()).stream()
                        .noneMatch(r -> Boolean.TRUE.equals(r.getEsOficial())))
                .map(publicacionMapper::toDominio)
                .toList();
    }

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
}