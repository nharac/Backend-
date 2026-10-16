package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.PistaResponse;

/**
 * Caso de uso: obtener la pista de una pregunta.
 */
public interface ObtenerPistaUseCase {

    /**
     * Devuelve la pista de una pregunta específica.
     *
     * @param preguntaId ID de la pregunta.
     * @return pista asociada.
     */
    PistaResponse obtenerPista(Long preguntaId);
}