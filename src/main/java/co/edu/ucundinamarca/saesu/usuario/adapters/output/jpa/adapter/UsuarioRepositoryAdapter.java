package co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.mapper.UsuarioMapper;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository repository, UsuarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        // La racha no está en el modelo de dominio: al actualizar un usuario existente
        // (nombre, contraseña, foto) se conserva la que ya tenía en vez de reiniciarla a 0.
        if (usuario.getId() != null) {
            repository.findById(usuario.getId()).ifPresent(actual -> {
                entity.setRachaActual(actual.getRachaActual());
                entity.setUltimaFechaPractica(actual.getUltimaFechaPractica());
            });
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return repository.existsByCorreo(correo);
    }
}