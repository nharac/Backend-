package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta para el historial de chat.
 * Devuelve los últimos N mensajes de una conversación (libre o por pregunta).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistorialChatResponse {

    private Long preguntaId;    // null si es chat libre
    private List<ChatMensajeResponse> mensajes;
}