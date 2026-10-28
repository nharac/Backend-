package co.edu.ucundinamarca.saesu.quiz.application.dto.response;

import java.time.Instant;
import java.util.List;

/** Resumen de los quices que ha presentado. */
public record QuizHistorialResponse(
        long intentos,
        int promedio,
        int mejor,
        List<Intento> ultimos) {

    public record Intento(Long id, String tema, int correctas, int total, int porcentaje,
                          long duracionSegundos, boolean fueraDeTiempo, Instant fecha) {}
}
