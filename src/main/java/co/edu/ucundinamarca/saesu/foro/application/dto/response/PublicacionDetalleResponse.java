package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionDetalleResponse {
    private Long id;
    private String titulo;
    private String contenido;
    private String tema;
    private AutorResponse autor;
    private LocalDateTime fechaCreacion;
    private List<RespuestaResponse> respuestas;
    private String imagenUrl; // <-- imagenes
}