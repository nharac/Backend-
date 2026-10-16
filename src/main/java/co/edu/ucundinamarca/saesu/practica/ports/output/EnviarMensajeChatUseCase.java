package co.edu.ucundinamarca.saesu.practica.ports.output;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.EnviarMensajeRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ChatMensajeResponse;

/**
 * Caso de uso: enviar un mensaje al chat de IA.
 *
 * Maneja tanto el chat contextual (con preguntaId) como el chat libre (sin preguntaId).
 */
public interface EnviarMensajeChatUseCase {

    /**
     * Envía un mensaje del estudiante a la IA y devuelve la respuesta.
     *
     * @param request          contiene el mensaje y opcionalmente el preguntaId.
     * @param correoEstudiante correo del estudiante autenticado.
     * @return la respuesta de la IA.
     */
    ChatMensajeResponse enviarMensaje(EnviarMensajeRequest request, String correoEstudiante);
}