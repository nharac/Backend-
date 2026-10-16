package co.edu.ucundinamarca.saesu.foro.domain.exception;

/**
 * Excepción lanzada cuando se intenta crear una publicación con un tema inválido.
 */
public class TemaInvalidoException extends RuntimeException {
    public TemaInvalidoException(String mensaje) {
        super(mensaje);
    }
}