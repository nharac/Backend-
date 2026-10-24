package co.edu.ucundinamarca.saesu.foro.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear una publicación en el foro.
 * El tema es obligatorio y debe ser uno de los valores permitidos.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearPublicacionRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    @NotBlank(message = "El tema es obligatorio")
    @Pattern(
            regexp = "limites|derivadas|integrales",
            message = "El tema debe ser: limites, derivadas o integrales"
    )
    private String tema;
    private String imagenUrl;
}