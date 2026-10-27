package co.edu.ucundinamarca.saesu.usuario.domain.exception;

/**
 * Se lanza cuando la contraseña actual no coincide al intentar cambiarla,
 * o cuando falta la contraseña actual.
 */
public class ContrasenaIncorrectaException extends RuntimeException {
    public ContrasenaIncorrectaException(String mensaje) {
        super(mensaje);
    }
}
