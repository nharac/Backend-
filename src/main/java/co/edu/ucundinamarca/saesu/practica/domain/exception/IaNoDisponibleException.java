package co.edu.ucundinamarca.saesu.practica.domain.exception;

/**
 * Excepción lanzada cuando la IA no está disponible
 * (timeout, error de red, cuota excedida, etc.).
 */
public class IaNoDisponibleException extends RuntimeException {
    public IaNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public IaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}