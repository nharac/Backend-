package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.PublicacionEntity;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import org.springframework.stereotype.Component;

/**
 * Convierte entre PublicacionEntity (JPA) y Publicacion (dominio).
 */
@Component
public class PublicacionMapper {

    public Publicacion toDominio(PublicacionEntity entity) {
        return new Publicacion(
                entity.getId(),
                entity.getAutor() != null ? entity.getAutor().getId() : null,
                entity.getTitulo(),
                entity.getContenido(),
                entity.getTema(),
                entity.getFechaCreacion(),
                entity.getFechaActualizacion()
        );
    }

    public PublicacionEntity toEntity(Publicacion dominio) {
        // El autor se carga aparte en el adapter (porque es una relación)
        PublicacionEntity entity = new PublicacionEntity();
        entity.setId(dominio.getId());
        entity.setTitulo(dominio.getTitulo());
        entity.setContenido(dominio.getContenido());
        entity.setTema(dominio.getTema());
        entity.setFechaCreacion(dominio.getFechaCreacion());
        entity.setFechaActualizacion(dominio.getFechaActualizacion());
        return entity;
    }
}