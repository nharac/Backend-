package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.RespuestaEstudianteEntity;
import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;
import org.springframework.stereotype.Component;

/**
 * Convierte entre RespuestaEstudianteEntity (JPA) y RespuestaEstudiante (dominio).
 */
@Component
public class RespuestaEstudianteMapper {

    public RespuestaEstudiante toDominio(RespuestaEstudianteEntity entity) {
        return new RespuestaEstudiante(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getPreguntaId(),
                entity.getOpcionId(),
                entity.getEsCorrecta(),
                entity.getFechaIntento()
        );
    }

    public RespuestaEstudianteEntity toEntity(RespuestaEstudiante dominio) {
        return new RespuestaEstudianteEntity(
                dominio.getId(),
                dominio.getUsuarioId(),
                dominio.getPreguntaId(),
                dominio.getOpcionId(),
                dominio.getEsCorrecta(),
                dominio.getFechaIntento()
        );
    }
}