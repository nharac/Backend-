package co.edu.ucundinamarca.saesu.foro.domain.exception;

public class RespuestaNoEncontradaException extends RuntimeException {
    public RespuestaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}