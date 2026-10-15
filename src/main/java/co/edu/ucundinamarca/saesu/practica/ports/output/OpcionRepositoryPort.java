package co.edu.ucundinamarca.saesu.practica.ports.output;

import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de opciones.
 */
public interface OpcionRepositoryPort {

    /**
     * Lista todas las opciones de una pregunta.
     */
    List<Opcion> listarPorPreguntaId(Long preguntaId);

    /**
     * Busca una opción por su ID.
     */
    Optional<Opcion> buscarPorId(Long id);

    /**
     * Busca la opción correcta de una pregunta.
     */
    Optional<Opcion> buscarCorrectaPorPreguntaId(Long preguntaId);
}