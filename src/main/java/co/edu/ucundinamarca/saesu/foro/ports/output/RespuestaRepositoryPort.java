package co.edu.ucundinamarca.saesu.foro.ports.output;

import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de respuestas.
 */
public interface RespuestaRepositoryPort {

    /**
     * Lista todas las respuestas de una publicación (sin anidar).
     * El servicio se encarga de armar la estructura anidada.
     */
    List<Respuesta> listarPorPublicacion(Long publicacionId);

    /**
     * Busca una respuesta por ID.
     */
    Optional<Respuesta> buscarPorId(Long id);

    /**
     * Cuenta las respuestas de una publicación.
     */
    long contarPorPublicacion(Long publicacionId);

    /**
     * Guarda una respuesta.
     */
    Respuesta guardar(Respuesta respuesta);
}