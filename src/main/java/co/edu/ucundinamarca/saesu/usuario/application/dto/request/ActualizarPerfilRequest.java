package co.edu.ucundinamarca.saesu.usuario.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos para actualizar el perfil del usuario autenticado.
 * - nombre: obligatorio.
 * - passwordActual + passwordNueva: opcionales; solo si se quiere cambiar la contraseña.
 * El correo y el rol no se pueden modificar desde aquí.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarPerfilRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    /** Requerida solo si se envía passwordNueva. */
    private String passwordActual;

    /** Misma regla que en el registro. Null o vacía = no se cambia la contraseña. */
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
        message = "La nueva contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial (@#$%^&+=!)"
    )
    private String passwordNueva;
}
