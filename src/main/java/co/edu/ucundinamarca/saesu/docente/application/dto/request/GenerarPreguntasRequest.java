package co.edu.ucundinamarca.saesu.docente.application.dto.request;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Lo que el docente le pide a la IA: tema, dificultad, cuántas y (opcional) un enfoque. */
public record GenerarPreguntasRequest(
        @NotBlank @Pattern(regexp = Tema.PATRON, message = "Tema no válido") String tema,
        @NotBlank @Pattern(regexp = "basico|intermedio|avanzado", message = "La dificultad debe ser: basico, intermedio o avanzado") String dificultad,
        @Min(value = 1, message = "Mínimo 1 pregunta") @Max(value = 5, message = "Máximo 5 preguntas por vez") int cantidad,
        @Size(max = 200, message = "El enfoque admite máximo 200 caracteres") String enfoque
) {}
