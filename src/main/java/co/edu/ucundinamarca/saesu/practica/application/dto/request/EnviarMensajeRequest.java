package co.edu.ucundinamarca.saesu.practica.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para enviar un mensaje al chat de IA.
 *
 * - Si preguntaId es null → chat libre (sin contexto).
 * - Si preguntaId tiene valor → chat contextual (con esa pregunta).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnviarMensajeRequest {

    /**
     * ID de la pregunta (opcional).
     * Null indica chat libre.
     */
    private Long preguntaId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 2000, message = "El mensaje no puede superar los 2000 caracteres")
    private String mensaje;
}