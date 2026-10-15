package co.edu.ucundinamarca.saesu.practica.ports.output;

import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de preguntas.
 * El dominio NO conoce si se implementa con JPA, MongoDB, etc.
 */
public interface PreguntaRepositoryPort {

    /**
     * Busca preguntas con filtros opcionales y paginación.
     */
    List<Pregunta> listar(String tema, String dificultad, int page, int size);

    /**
     * Cuenta el total de preguntas con los filtros aplicados.
     */
    long contar(String tema, String dificultad);

    /**
     * Busca una pregunta por su ID.
     */
    Optional<Pregunta> buscarPorId(Long id);

    /**
     * Guarda una pregunta (con sus opciones).
     */
    Pregunta guardar(Pregunta pregunta);
}