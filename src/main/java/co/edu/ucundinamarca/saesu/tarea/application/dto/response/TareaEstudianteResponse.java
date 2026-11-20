package co.edu.ucundinamarca.saesu.tarea.application.dto.response;

import java.time.LocalDate;

/** Tarea vista por el estudiante, con su avance. */
public record TareaEstudianteResponse(
        Long id,
        String titulo,
        String instrucciones,
        String tema,
        String temaNombre,
        int metaPreguntas,
        LocalDate fechaLimite,
        String docente,
        long resueltas,
        boolean completada,
        boolean vencida,
        long diasRestantes
) {}
