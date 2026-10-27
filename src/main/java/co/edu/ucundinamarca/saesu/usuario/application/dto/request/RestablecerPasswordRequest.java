package co.edu.ucundinamarca.saesu.usuario.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Paso 2: el usuario envía el código que recibió y su nueva contraseña (mismas reglas del registro). */
public record RestablecerPasswordRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debe ser un correo válido")
        String correo,

        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "^\\d{6}$", message = "El código tiene 6 dígitos")
        String codigo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial (@#$%^&+=!)"
        )
        String passwordNueva
) {}
