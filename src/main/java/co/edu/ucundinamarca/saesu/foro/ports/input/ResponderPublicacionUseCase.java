package co.edu.ucundinamarca.saesu.foro.ports.input;

import co.edu.ucundinamarca.saesu.foro.application.dto.request.ResponderPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;

/**
 * Caso de uso: responder a una publicación o a otra respuesta.
 */
public interface ResponderPublicacionUseCase {

    /**
     * Crea una respuesta.
     *
     * @param publicacionId    ID de la publicación.
     * @param request          contenido y opcionalmente respuestaPadreId.
     * @param correoAutor      correo del usuario autenticado.
     * @return la respuesta creada.
     */
    RespuestaResponse responder(Long publicacionId, ResponderPublicacionRequest request, String correoAutor);
}