package co.edu.ucundinamarca.saesu.tarea.application.dto.request;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Tarea nueva. Sin estudianteId se asigna a todo el grupo. */
public record TareaRequest(
        @NotBlank(message = "El título es obligatorio") @Size(max = 120, message = "El título admite máximo 120 caracteres")
        String titulo,
        @Size(max = 500, message = "Las instrucciones admiten máximo 500 caracteres")
        String instrucciones,
        @NotNull(message = "El tema es obligatorio") @Pattern(regexp = Tema.PATRON, message = "Tema no válido")
        String tema,
        @Min(value = 1, message = "La meta mínima es 1 pregunta") @Max(value = 50, message = "La meta máxima es 50 preguntas")
        int metaPreguntas,
        @NotNull(message = "La fecha límite es obligatoria") @FutureOrPresent(message = "La fecha límite no puede estar en el pasado")
        LocalDate fechaLimite,
        Long estudianteId
) {}
