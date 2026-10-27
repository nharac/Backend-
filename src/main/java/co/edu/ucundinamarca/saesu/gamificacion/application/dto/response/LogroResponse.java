package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Un logro con su estado para el usuario actual (GET /api/logros).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogroResponse {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String icono;
    private boolean obtenido;
    /** null si todavía no lo tiene. */
    private OffsetDateTime fechaObtenido;
}
