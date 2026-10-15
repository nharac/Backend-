package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.PreguntaEntity;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Convierte entre PreguntaEntity (JPA) y Pregunta (dominio).
 */
@Component
public class PreguntaMapper {

    public Pregunta toDominio(PreguntaEntity entity) {
        return new Pregunta(
                entity.getId(),
                Tema.desdeValor(entity.getTema()),
                entity.getEnunciado(),
                Dificultad.desdeValor(entity.getDificultad()),
                entity.getPista(),
                entity.getFechaCreacion(),
                null // Las opciones se cargan aparte
        );
    }

    public PreguntaEntity toEntity(Pregunta dominio) {
        return new PreguntaEntity(
                dominio.getId(),
                dominio.getTema().getValor(),
                dominio.getEnunciado(),
                dominio.getDificultad().getValor(),
                dominio.getPista(),
                dominio.getFechaCreacion() != null ? dominio.getFechaCreacion() : LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}