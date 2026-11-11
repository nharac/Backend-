package co.edu.ucundinamarca.saesu.practica.application.dto.request;

import jakarta.validation.constraints.NotNull;

/** La única respuesta del reto del día. */
public record RetoRespuestaRequest(@NotNull Long opcionId) {
}
