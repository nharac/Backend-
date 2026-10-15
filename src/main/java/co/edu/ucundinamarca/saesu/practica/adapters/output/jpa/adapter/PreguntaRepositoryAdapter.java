package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.PreguntaEntity;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.PreguntaMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.PreguntaJpaRepository;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa PreguntaRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class PreguntaRepositoryAdapter implements PreguntaRepositoryPort {

    private final PreguntaJpaRepository jpaRepository;
    private final PreguntaMapper mapper;

    @Override
    public List<Pregunta> listar(String tema, String dificultad, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PreguntaEntity> pagina = jpaRepository.buscarConFiltros(tema, dificultad, pageable);

        return pagina.getContent().stream()
                .map(mapper::toDominio)
                .toList();
    }

    @Override
    public long contar(String tema, String dificultad) {
        return jpaRepository.buscarConFiltros(tema, dificultad, Pageable.unpaged()).getTotalElements();
    }

    @Override
    public Optional<Pregunta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDominio);
    }

    @Override
    public Pregunta guardar(Pregunta pregunta) {
        PreguntaEntity entity = mapper.toEntity(pregunta);
        PreguntaEntity guardada = jpaRepository.save(entity);
        return mapper.toDominio(guardada);
    }
}