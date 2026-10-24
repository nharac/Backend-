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
                entity.getFechaRegistro(),
                entity.getFotoPerfilUrl()
                
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        
     
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setCorreo(domain.getCorreo());
        entity.setPassword(domain.getPassword());
        entity.setRol(domain.getRol());
        entity.setFechaRegistro(domain.getFechaRegistro());
        entity.setFotoPerfilUrl(domain.getFotoPerfilUrl());
        
        entity.setRachaActual(0);
        entity.setUltimaFechaPractica(null);
        
        return entity;
    }
}