package co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getCorreo(),
                entity.getPassword(),
                entity.getRol(),
                entity.getFechaRegistro()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        return new UsuarioEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getCorreo(),
                domain.getPassword(),
                domain.getRol(),
                domain.getFechaRegistro()
        );
    }
}