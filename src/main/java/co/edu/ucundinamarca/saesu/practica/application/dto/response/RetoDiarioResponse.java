package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import java.time.LocalDate;

/**
 * Reto del día de quien consulta.
 * @param estado        "pendiente", "ganado" o "perdido"
 * @param opcionElegida lo que respondió (null si no ha respondido)
 * @param opcionCorrecta solo se envía después de responder
 * @param pista         explicación, solo después de responder
 * @param puntos        Puntos Sumi que da acertarlo
 */
public record RetoDiarioResponse(LocalDate fecha, String estado, PreguntaResponse pregunta,
                                 Long opcionElegida, Long opcionCorrecta, String pista, int puntos) {
}
