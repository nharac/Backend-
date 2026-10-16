package co.edu.ucundinamarca.saesu.foro.ports.input;

import co.edu.ucundinamarca.saesu.foro.application.dto.request.CrearPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;

/**
 * Caso de uso: crear una nueva publicación en el foro.
 */
public interface CrearPublicacionUseCase {

    /**
     * Crea una publicación.
     *
     * @param request          datos de la publicación (título, contenido, tema).
     * @param correoAutor      correo del usuario autenticado.
     * @return la publicación creada.
     */
    PublicacionDetalleResponse crear(CrearPublicacionRequest request, String correoAutor);
}
