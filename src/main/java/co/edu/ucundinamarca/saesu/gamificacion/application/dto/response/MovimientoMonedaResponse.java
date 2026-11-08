package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import java.time.LocalDateTime;

/** Un movimiento de Sumi Monedas: positivo si las ganó, negativo si las gastó. */
public record MovimientoMonedaResponse(int cantidad, String motivo, LocalDateTime fecha) {
}
