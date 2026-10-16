package co.edu.ucundinamarca.saesu.practica.ports.output;

/**
 * Puerto de salida hacia el servicio de IA.
 *
 * El dominio no sabe si es Gemini, OpenAI, Groq, etc.
 * Solo sabe que puede pedirle una respuesta dada una conversación.
 */
public interface IaServicePort {

    /**
     * Genera una respuesta del modelo de IA dada una conversación.
     *
     * @param systemPrompt  instrucciones del sistema (personalidad, reglas).
     * @param historial     los mensajes previos en formato texto (user/assistant).
     * @param mensajeNuevo  el nuevo mensaje del usuario.
     * @return la respuesta generada por la IA.
     * @throws co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException
     *         si la IA no responde.
     */
    String generarRespuesta(String systemPrompt, String historial, String mensajeNuevo);
}