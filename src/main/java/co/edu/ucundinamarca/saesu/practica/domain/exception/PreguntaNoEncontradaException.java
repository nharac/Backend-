package co.edu.ucundinamarca.saesu.practica.domain.exception;

public class PreguntaNoEncontradaException extends RuntimeException {
    public PreguntaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}