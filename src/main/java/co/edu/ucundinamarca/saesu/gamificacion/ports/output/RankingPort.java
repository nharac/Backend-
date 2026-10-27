package co.edu.ucundinamarca.saesu.gamificacion.ports.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Consultas del ranking sobre puntos_historial.
 * "desde" filtra los puntos ganados a partir de esa fecha (para el ranking semanal o mensual).
 */
public interface RankingPort {

    /** Una fila del ranking ya ordenada (posición 1 = más puntos). */
    record FilaRanking(int posicion, Long usuarioId, String nombre, String fotoPerfilUrl, long puntos, int racha) {}

    /** Posición de un estudiante y quién va justo encima de él. */
    record PosicionUsuario(int posicion, long puntos, long participantes,
                           Long puntosDelDeArriba, String nombreDelDeArriba) {}

    /** Un movimiento del historial de puntos. */
    record Movimiento(int cantidad, String motivo, LocalDateTime fecha) {}

    List<FilaRanking> top(LocalDateTime desde, int limite);

    /** Vacío si el estudiante no tiene puntos en el periodo. */
    Optional<PosicionUsuario> posicionDe(Long usuarioId, LocalDateTime desde);

    /** Estudiantes con más de 0 puntos en el periodo. */
    long participantes(LocalDateTime desde);

    long puntosDesde(Long usuarioId, LocalDateTime desde);

    /** Racha vigente: 0 si no practicó ni hoy ni ayer. */
    int rachaVigente(Long usuarioId);

    List<Movimiento> ultimosMovimientos(Long usuarioId, int limite);
}
