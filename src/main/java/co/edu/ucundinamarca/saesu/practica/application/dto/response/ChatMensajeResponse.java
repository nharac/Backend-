package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO que representa un mensaje individual del chat.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMensajeResponse {

    private Long id;
    private String rolEmisor;      // "usuario" o "ia"
    private String mensaje;
    private Long preguntaId;       // null si es chat libre
    private LocalDateTime fechaHora;
}
