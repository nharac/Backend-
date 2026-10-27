package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.RespuestaEntity;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import org.springframework.stereotype.Component;

/**
 * Convierte entre RespuestaEntity (JPA) y Respuesta (dominio).
 */
@Component
public class RespuestaMapper {

    public Respuesta toDominio(RespuestaEntity entity) {
        return new Respuesta(
                entity.getId(),
                entity.getPublicacionId(),
                entity.getAutor() != null ? entity.getAutor().getId() : null,
                entity.getRespuestaPadreId(),
                entity.getContenido(),
                entity.getEsOficial(),
                entity.getFechaCreacion(),
                Boolean.TRUE.equals(entity.getOculta())
        );
    }

    public RespuestaEntity toEntity(Respuesta dominio) {
        RespuestaEntity entity = new RespuestaEntity();
        entity.setId(dominio.getId());
        entity.setPublicacionId(dominio.getPublicacionId());
        entity.setRespuestaPadreId(dominio.getRespuestaPadreId());
        entity.setContenido(dominio.getContenido());
        entity.setEsOficial(dominio.getEsOficial());
        entity.setFechaCreacion(dominio.getFechaCreacion());
        return entity;
    }
}