package co.edu.ucundinamarca.saesu.usuario.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Paso 1: el usuario pide un código de recuperación. */
public record RecuperarPasswordRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo válido")
        String correo
) {}
