package co.edu.ucundinamarca.saesu.foro.domain.exception;

/**
 * Excepción lanzada cuando se intenta responder a una respuesta que:
 * - No existe.
 * - No pertenece a la misma publicación.
 * - Es hija de otra respuesta (más de 1 nivel de anidamiento).
 */
public class RespuestaPadreInvalidaException extends RuntimeException {
    public RespuestaPadreInvalidaException(String mensaje) {
        super(mensaje);
    }
}