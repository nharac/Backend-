package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionResumenResponse {
    private Long id;
    private String titulo;
    private String contenidoPreview;
    private String tema;
    private AutorResponse autor;
    private LocalDateTime fechaCreacion;
    private long respuestasCount;
    private String imagenUrl; // <-- imagenes
}