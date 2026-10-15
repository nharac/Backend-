package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.RespuestaEstudianteMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.RespuestaEstudianteJpaRepository;
import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;
import co.edu.ucundinamarca.saesu.practica.ports.output.RespuestaEstudianteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador que implementa RespuestaEstudianteRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class RespuestaEstudianteRepositoryAdapter implements RespuestaEstudianteRepositoryPort {

    private final RespuestaEstudianteJpaRepository jpaRepository;
    private final RespuestaEstudianteMapper mapper;

    @Override
    public RespuestaEstudiante guardar(RespuestaEstudiante respuesta) {
        var entity = mapper.toEntity(respuesta);
        var guardada = jpaRepository.save(entity);
        return mapper.toDominio(guardada);
    }

    @Override
    public List<RespuestaEstudiante> listarPorUsuario(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDominio)
                .toList();
    }

    @Override
    public long contarPreguntasRespondidas(Long usuarioId) {
        return jpaRepository.contarPreguntasRespondidas(usuarioId);
    }

    @Override
    public long contarPreguntasCorrectas(Long usuarioId) {
        return jpaRepository.contarPreguntasCorrectas(usuarioId);
    }

    @Override
    public List<Object[]> contarPorTema(Long usuarioId) {
        return jpaRepository.contarPorTema(usuarioId);
    }
}