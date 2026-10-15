package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.OpcionEntity;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import org.springframework.stereotype.Component;

/**
 * Convierte entre OpcionEntity (JPA) y Opcion (dominio).
 */
@Component
public class OpcionMapper {

    public Opcion toDominio(OpcionEntity entity) {
        return new Opcion(
                entity.getId(),
                entity.getPreguntaId(),
                entity.getLetra(),
                entity.getTexto(),
                entity.getEsCorrecta()
        );
    }

    public OpcionEntity toEntity(Opcion dominio) {
        return new OpcionEntity(
                dominio.getId(),
                dominio.getPreguntaId(),
                dominio.getLetra(),
                dominio.getTexto(),
                dominio.getEsCorrecta()
        );
    }
}