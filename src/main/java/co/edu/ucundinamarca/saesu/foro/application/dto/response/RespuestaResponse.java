package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de una respuesta del foro.
 * Puede contener respuestas hijas (anidamiento de 1 nivel).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaResponse {

    private Long id;
    private AutorResponse autor;
    private String contenido;
    private boolean esOficial;
    private LocalDateTime fechaCreacion;
    private List<RespuestaResponse> respuestasHijas;   // lista vacía si no tiene hijas
}