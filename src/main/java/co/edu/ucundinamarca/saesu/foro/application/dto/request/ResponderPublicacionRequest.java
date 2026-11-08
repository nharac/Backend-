package co.edu.ucundinamarca.saesu.foro.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para responder en el foro.
 * Si respuestaPadreId es null → respuesta directa a la publicación.
 * Si respuestaPadreId tiene valor → respuesta anidada (a otra respuesta).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponderPublicacionRequest {

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    /**
     * ID de la respuesta padre (opcional).
     * Null → respuesta directa a la publicación.
     */
    private Long respuestaPadreId;

    /** URL de una imagen subida antes a POST /api/foro/imagenes (opcional). */
    private String imagenUrl;

    public ResponderPublicacionRequest(String contenido, Long respuestaPadreId) {
        this(contenido, respuestaPadreId, null);
    }
}