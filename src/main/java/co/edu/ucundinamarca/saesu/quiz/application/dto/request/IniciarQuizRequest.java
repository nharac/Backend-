package co.edu.ucundinamarca.saesu.quiz.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * @param tema     limites, derivadas o integrales; vacío o "mixto" = los tres temas
 * @param cantidad número de preguntas (5 a 20)
 */
public record IniciarQuizRequest(
        String tema,
        @Min(value = 5, message = "El quiz debe tener al menos 5 preguntas")
        @Max(value = 20, message = "El quiz puede tener máximo 20 preguntas")
        int cantidad) {
}
