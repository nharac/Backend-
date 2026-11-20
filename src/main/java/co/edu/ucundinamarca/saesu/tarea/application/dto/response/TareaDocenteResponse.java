package co.edu.ucundinamarca.saesu.tarea.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Tarea vista por el docente, con cuántos de los asignados ya la completaron. */
public record TareaDocenteResponse(
        Long id,
        String titulo,
        String instrucciones,
        String tema,
        String temaNombre,
        int metaPreguntas,
        LocalDate fechaLimite,
        Long estudianteId,
        String asignadaA,
        long asignados,
        long completadas,
        boolean vencida,
        LocalDateTime creadaEn
) {}
