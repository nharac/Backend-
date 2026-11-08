package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Un logro con su estado para el usuario actual (GET /api/logros).
 * Un logro secreto que todavía no se tiene llega con nombre "???" y su pista como descripción.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogroResponse {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String icono;
    /** PRACTICA, TEMAS, RACHAS, QUIZ, COMUNIDAD, PUNTOS o RETOS (insignias especiales). */
    private String categoria;
    /** COMUN, RARO, EPICO o LEGENDARIO. */
    private String rareza;
    private boolean secreto;
    private boolean obtenido;
    /** null si todavía no lo tiene. */
    private OffsetDateTime fechaObtenido;
    /** Avance actual hacia la meta (nunca mayor que la meta). */
    private long progreso;
    private long meta;
}
