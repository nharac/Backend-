package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.OpcionMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.OpcionJpaRepository;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa OpcionRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class OpcionRepositoryAdapter implements OpcionRepositoryPort {

    private final OpcionJpaRepository jpaRepository;
    private final OpcionMapper mapper;

    @Override
    public List<Opcion> listarPorPreguntaId(Long preguntaId) {
        return jpaRepository.findByPreguntaIdOrderByLetra(preguntaId).stream()
                .map(mapper::toDominio)
                .toList();
    }

    @Override
    public Optional<Opcion> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDominio);
    }

    @Override
    public Optional<Opcion> buscarCorrectaPorPreguntaId(Long preguntaId) {
        return jpaRepository.findByPreguntaIdAndEsCorrectaTrue(preguntaId)
                .map(mapper::toDominio);
    }
}