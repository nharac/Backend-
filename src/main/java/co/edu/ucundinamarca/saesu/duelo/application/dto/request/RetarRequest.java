package co.edu.ucundinamarca.saesu.duelo.application.dto.request;

import co.edu.ucundinamarca.saesu.duelo.domain.ModoDuelo;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RetarRequest(
        @NotNull(message = "Elige a quién retar") Long rivalId,
        @NotNull(message = "El tema es obligatorio") @Pattern(regexp = Tema.PATRON, message = "Tema no válido") String tema,
        @Pattern(regexp = ModoDuelo.PATRON, message = "Modo de reto no válido") String modo
) {}
