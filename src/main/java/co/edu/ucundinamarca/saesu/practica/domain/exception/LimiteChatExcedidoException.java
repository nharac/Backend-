package co.edu.ucundinamarca.saesu.practica.domain.exception;

/**
 * Excepción lanzada cuando el estudiante excede el límite
 * diario de mensajes al chat de IA.
 */
public class LimiteChatExcedidoException extends RuntimeException {
    public LimiteChatExcedidoException(String mensaje) {
        super(mensaje);
    }
}
