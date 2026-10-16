package co.edu.ucundinamarca.saesu.foro.ports.input;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;

/**
 * Caso de uso: ver el detalle de una publicación con sus respuestas anidadas.
 */
public interface VerDetallePublicacionUseCase {

    /**
     * Devuelve el detalle de una publicación.
     *
     * @param publicacionId ID de la publicación.
     * @return publicación con sus respuestas (anidadas).
     */
    PublicacionDetalleResponse verDetalle(Long publicacionId);
}