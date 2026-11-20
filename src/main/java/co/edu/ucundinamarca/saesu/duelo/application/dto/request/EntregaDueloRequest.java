package co.edu.ucundinamarca.saesu.duelo.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Respuestas del reto: la letra elegida por cada pregunta y el tiempo que tardó. */
public record EntregaDueloRequest(
        @NotNull @Size(max = 20) List<@Valid Eleccion> respuestas,
        @Min(0) @Max(3600) int segundos
) {
    public record Eleccion(@NotNull Long preguntaId, @Size(max = 2) String letra) {}
}
