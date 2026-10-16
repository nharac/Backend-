package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que agrupa el mensaje del usuario y la respuesta de la IA
 * en una sola respuesta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaChatResponse {

    private ChatMensajeResponse mensajeUsuario;
    private ChatMensajeResponse respuestaIa;
    private long mensajesRestantesHoy;
}