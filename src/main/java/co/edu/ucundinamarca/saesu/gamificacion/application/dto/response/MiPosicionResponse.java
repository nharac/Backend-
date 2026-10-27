package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Posición del estudiante que consulta (GET /api/ranking/yo), aunque no esté en el top 10.
 *
 * @param posicion          null si todavía no tiene puntos en el periodo
 * @param puntosParaSubir   puntos que le faltan para pasar al de arriba (null si va primero o no tiene puesto)
 * @param siguienteNombre   nombre del estudiante que va justo encima
 */
public record MiPosicionResponse(
        String periodo,
        Integer posicion,
        long puntos,
        long participantes,
        Long puntosParaSubir,
        String siguienteNombre,
        int rachaActual,
        long puntosHoy,
        long puntosSemana,
        long puntosTotales,
        List<Movimiento> ultimosMovimientos
) {
    public record Movimiento(int cantidad, String motivo, LocalDateTime fecha) {}
}
