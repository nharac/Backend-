package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * Calendario de actividad (GET /api/practica/actividad): un elemento por día, también los días sin práctica.
 * @param dias          del más antiguo a hoy
 * @param diasActivos   días con al menos una respuesta
 * @param mejorDia      el día con más respuestas (null si no hay ninguno)
 */
public record ActividadResponse(List<DiaActividad> dias, long diasActivos, long totalRespondidas,
                                long totalAciertos, DiaActividad mejorDia) {

    public record DiaActividad(LocalDate fecha, long respondidas, long aciertos) { }
}
