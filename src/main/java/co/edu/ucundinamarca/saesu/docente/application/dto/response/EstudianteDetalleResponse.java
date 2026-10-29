package co.edu.ucundinamarca.saesu.docente.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detalle de un estudiante para el docente.
 * GET /api/docente/estudiantes/{id}
 */
public record EstudianteDetalleResponse(
        EstudianteResumenResponse resumen,
        LocalDateTime fechaRegistro,
        long logrosObtenidos,
        long publicaciones,
        long respuestasForo,
        long respuestasOficiales,
        List<IntentoReciente> ultimosIntentos,
        List<PreguntaConFallos> preguntasConMasFallos,
        List<EstadisticasGrupoResponse.ActividadDia> actividad,
        List<QuizRealizado> quices
) {

    /** Quiz entregado por el estudiante; el docente puede abrir la revisión con su id. */
    public record QuizRealizado(
            Long id,
            String tema,
            int correctas,
            int total,
            int porcentaje,
            long duracionSegundos,
            boolean fueraDeTiempo,
            LocalDateTime fecha
    ) {}

    public record IntentoReciente(
            Long preguntaId,
            String enunciado,
            String tema,
            String opcionElegida,
            boolean correcta,
            LocalDateTime fecha
    ) {}

    public record PreguntaConFallos(
            Long preguntaId,
            String enunciado,
            String tema,
            long fallos,
            boolean resuelta
    ) {}
}
