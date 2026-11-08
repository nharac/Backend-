package co.edu.ucundinamarca.saesu.gamificacion.domain.exception;

/** Compra en el vestidor que no se puede hacer (ya la tiene o no le alcanzan las monedas). */
public class CompraNoValidaException extends RuntimeException {
    public CompraNoValidaException(String mensaje) {
        super(mensaje);
    }
}
