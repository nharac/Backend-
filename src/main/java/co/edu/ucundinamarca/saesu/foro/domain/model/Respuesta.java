package co.edu.ucundinamarca.saesu.foro.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Respuesta a una publicación del foro.
 * Puede anidarse (respuesta a una respuesta) mediante respuestaPadreId.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Respuesta {

    private Long id;
    private Long publicacionId;
    private Long autorId;
    private Long respuestaPadreId;  // null si es respuesta directa
    private String contenido;
    private Boolean esOficial;
    private LocalDateTime fechaCreacion;
    private boolean oculta;          // ocultada por un docente (moderación)

    /** Constructor sin el estado de moderación (respuestas nuevas: nunca están ocultas). */
    public Respuesta(Long id, Long publicacionId, Long autorId, Long respuestaPadreId,
                     String contenido, Boolean esOficial, LocalDateTime fechaCreacion) {
        this(id, publicacionId, autorId, respuestaPadreId, contenido, esOficial, fechaCreacion, false);
    }

    /**
     * Verifica si esta respuesta es hija de otra respuesta.
     */
    public boolean esAnidada() {
        return this.respuestaPadreId != null;
    }
}