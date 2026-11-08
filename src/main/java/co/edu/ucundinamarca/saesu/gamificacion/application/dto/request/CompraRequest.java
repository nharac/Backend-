package co.edu.ucundinamarca.saesu.gamificacion.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Prenda que se quiere comprar en el vestidor. */
public record CompraRequest(@NotBlank @Size(max = 20) String categoria, @NotBlank @Size(max = 40) String prenda) {
}
