package co.edu.ucundinamarca.saesu.practica.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ejercicio que el estudiante quiere ver resuelto paso a paso. */
public record ExplicarEjercicioRequest(
        @NotBlank(message = "Escribe el ejercicio que quieres resolver")
        @Size(max = 500, message = "El ejercicio no puede superar los 500 caracteres")
        String ejercicio) {
}
