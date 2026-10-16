package co.edu.ucundinamarca.saesu.practica.ports.output;

import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida para la persistencia de mensajes del chat.
 */
public interface ChatMensajeRepositoryPort {

    /**
     * Guarda un mensaje (sea del usuario o de la IA).
     */
    ChatMensaje guardar(ChatMensaje mensaje);

    /**
     * Devuelve el historial del chat libre (sin pregunta asociada).
     * Los mensajes vienen ordenados por fecha ascendente.
     *
     * @param usuarioId ID del usuario.
     * @param limite    máximo de mensajes a devolver.
     */
    List<ChatMensaje> listarHistorialLibre(Long usuarioId, int limite);

    /**
     * Devuelve el historial del chat contextual a una pregunta.
     */
    List<ChatMensaje> listarHistorialPorPregunta(Long usuarioId, Long preguntaId, int limite);

    /**
     * Cuenta cuántos mensajes del USUARIO ha enviado hoy.
     * Útil para validar el límite diario.
     */
    long contarMensajesHoy(Long usuarioId, LocalDateTime inicioDelDia);

    /**
     * Elimina todos los mensajes del chat libre de un usuario.
     */
    void eliminarHistorialLibre(Long usuarioId);

    /**
     * Elimina todos los mensajes del chat de una pregunta.
     */
    void eliminarHistorialPorPregunta(Long usuarioId, Long preguntaId);
}