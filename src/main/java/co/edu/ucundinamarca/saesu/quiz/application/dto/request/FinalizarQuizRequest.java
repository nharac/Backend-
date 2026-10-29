package co.edu.ucundinamarca.saesu.quiz.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Lo que eligió en cada pregunta; las que no aparezcan cuentan como sin responder. */
public record FinalizarQuizRequest(@NotNull(message = "Faltan las respuestas") @Valid List<Respuesta> respuestas) {

    public record Respuesta(@NotNull Long preguntaId, String letra) {}
}
