package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.PublicacionEntity;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper.PublicacionMapper;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.PublicacionJpaRepository;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa PublicacionRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class PublicacionRepositoryAdapter implements PublicacionRepositoryPort {

    private final PublicacionJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PublicacionMapper mapper;

    @Override
    public List<Publicacion> listar(String tema, String busqueda, String orden, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, construirSort(orden));
        Page<PublicacionEntity> pagina = jpaRepository.buscarConFiltros(tema, busqueda, pageable);
        return pagina.getContent().stream().map(mapper::toDominio).toList();
    }

    @Override
    public long contar(String tema, String busqueda) {
        return jpaRepository.contarConFiltros(tema, busqueda);
    }

    @Override
    public Optional<Publicacion> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDominio);
    }

    @Override
    public Publicacion guardar(Publicacion publicacion) {
        PublicacionEntity entity = mapper.toEntity(publicacion);

        // Cargar el autor como UsuarioEntity
        if (publicacion.getAutorId() != null) {
            UsuarioEntity autor = usuarioJpaRepository.findById(publicacion.getAutorId())
                    .orElseThrow(() -> new RuntimeException(
                            "Autor no encontrado con id: " + publicacion.getAutorId()));
            entity.setAutor(autor);
        }

        PublicacionEntity guardada = jpaRepository.save(entity);
        return mapper.toDominio(guardada);
    }

    /**
     * Construye el Sort según el orden solicitado.
     * - "reciente" → fecha_creacion DESC
     * - "antiguo"  → fecha_creacion ASC
     * - "respuestas" → por fecha DESC (el orden por respuestas se hace en el servicio)
     */
    private Sort construirSort(String orden) {
        if ("antiguo".equalsIgnoreCase(orden)) {
            return Sort.by(Sort.Direction.ASC, "fechaCreacion");
        }
        // "reciente" y "respuestas" usan fecha DESC como base
        return Sort.by(Sort.Direction.DESC, "fechaCreacion");
    }
}