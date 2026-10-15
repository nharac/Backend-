package co.edu.ucundinamarca.saesu.practica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de un intento de respuesta del estudiante.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaEstudiante {
    private Long id;
    private Long usuarioId;
    private Long preguntaId;
    private Long opcionId;
    private Boolean esCorrecta;
    private LocalDateTime fechaIntento;
}