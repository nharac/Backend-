package co.edu.ucundinamarca.saesu.docente.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Crear o editar una pregunta del banco (docente).
 * Las reglas que cruzan campos (letras A-D sin repetir, una sola correcta) se validan en BancoPreguntasService.
 */
public record PreguntaDocenteRequest(

        @NotBlank(message = "El tema es obligatorio")
        @Pattern(regexp = "limites|derivadas|integrales", message = "El tema debe ser: limites, derivadas o integrales")
        String tema,

        @NotBlank(message = "El enunciado es obligatorio")
        @Size(max = 2000, message = "El enunciado no puede superar 2000 caracteres")
        String enunciado,

        @NotBlank(message = "La dificultad es obligatoria")
        @Pattern(regexp = "basico|intermedio|avanzado", message = "La dificultad debe ser: basico, intermedio o avanzado")
        String dificultad,

        @Size(max = 1000, message = "La pista no puede superar 1000 caracteres")
        String pista,

        @NotNull(message = "Las opciones son obligatorias")
        @Size(min = 4, max = 4, message = "Debe haber exactamente 4 opciones (A, B, C y D)")
        List<@Valid OpcionRequest> opciones
) {

    public record OpcionRequest(
            @NotBlank(message = "La letra es obligatoria")
            String letra,

            @NotBlank(message = "El texto de la opción es obligatorio")
            @Size(max = 500, message = "El texto de la opción no puede superar 500 caracteres")
            String texto,

            boolean esCorrecta
    ) {}
}
