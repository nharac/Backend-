package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.EnviarMensajeRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RespuestaChatResponse;

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
     * @return la respuesta del usuario + la respuesta de la IA + mensajes restantes.
     */
    RespuestaChatResponse enviarMensaje(EnviarMensajeRequest request, String correoEstudiante);
}