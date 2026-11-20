package co.edu.ucundinamarca.saesu.duelo.domain.exception;

/** El reto no existe o el usuario no participa en él (404). */
public class DueloNoEncontradoException extends RuntimeException {
    public DueloNoEncontradoException() {
        super("Reto no encontrado");
    }
}
