package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de resumen de publicación para el listado del foro.
 * NO incluye las respuestas (solo el contador).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionResumenResponse {

    private Long id;
    private String titulo;
    private String contenidoPreview;   // primeros 150 caracteres del contenido
    private String tema;
    private AutorResponse autor;
    private LocalDateTime fechaCreacion;
    private long respuestasCount;
}