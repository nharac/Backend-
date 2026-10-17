package co.edu.ucundinamarca.saesu.docente.adapters.output.jpa.adapter;

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

    @Override
    @Transactional
    public void marcarOficial(Long respuestaId) {
        respuestaJpa.findById(respuestaId).ifPresent(r -> {
            r.setEsOficial(true);
            respuestaJpa.save(r);
        });
    }

    @Override
    @Transactional
    public void quitarOficial(Long respuestaId) {
        respuestaJpa.findById(respuestaId).ifPresent(r -> {
            r.setEsOficial(false);
            respuestaJpa.save(r);
        });
    }

    @Override
    @Transactional
    public void quitarOficialesDePublicacion(Long publicacionId) {
        respuestaJpa.listarPorPublicacion(publicacionId).forEach(r -> {
            if (Boolean.TRUE.equals(r.getEsOficial())) {
                r.setEsOficial(false);
                respuestaJpa.save(r);
            }
        });
    }
}