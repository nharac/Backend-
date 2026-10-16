package co.edu.ucundinamarca.saesu.practica.domain.exception;

public class OpcionInvalidaException extends RuntimeException {
    public OpcionInvalidaException(String mensaje) {
        super(mensaje);
    }
}