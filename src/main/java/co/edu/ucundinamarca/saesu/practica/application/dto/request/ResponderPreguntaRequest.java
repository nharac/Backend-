package co.edu.ucundinamarca.saesu.practica.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de request para responder una pregunta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponderPreguntaRequest {

    @NotNull(message = "El ID de la opción es obligatorio")
    private Long opcionId;
}