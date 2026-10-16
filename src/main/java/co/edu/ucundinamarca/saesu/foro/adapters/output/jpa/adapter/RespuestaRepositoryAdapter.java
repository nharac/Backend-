package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.RespuestaEntity;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper.RespuestaMapper;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.RespuestaJpaRepository;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa RespuestaRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class RespuestaRepositoryAdapter implements RespuestaRepositoryPort {

    private final RespuestaJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final RespuestaMapper mapper;

    @Override
    public List<Respuesta> listarPorPublicacion(Long publicacionId) {
        return jpaRepository.listarPorPublicacion(publicacionId).stream()
                .map(mapper::toDominio)
                .toList();
    }

    @Override
    public Optional<Respuesta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDominio);
    }

    @Override
    public long contarPorPublicacion(Long publicacionId) {
        return jpaRepository.countByPublicacionId(publicacionId);
    }

    @Override
    public Respuesta guardar(Respuesta respuesta) {
        RespuestaEntity entity = mapper.toEntity(respuesta);

        // Cargar el autor
        if (respuesta.getAutorId() != null) {
            UsuarioEntity autor = usuarioJpaRepository.findById(respuesta.getAutorId())
                    .orElseThrow(() -> new RuntimeException(
                            "Autor no encontrado con id: " + respuesta.getAutorId()));
            entity.setAutor(autor);
        }

        RespuestaEntity guardada = jpaRepository.save(entity);
        return mapper.toDominio(guardada);
    }
}