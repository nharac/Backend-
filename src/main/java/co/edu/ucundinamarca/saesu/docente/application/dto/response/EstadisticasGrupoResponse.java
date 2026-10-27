package co.edu.ucundinamarca.saesu.docente.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * Panel de estadísticas del grupo para el docente.
 * GET /api/docente/estadisticas
 */
public record EstadisticasGrupoResponse(
        long totalEstudiantes,
        long activosSemana,
        long intentosSemana,
        double porcentajeAciertoGeneral,
        long estudiantesEnRiesgo,
        long preguntasActivas,
        List<TemaEstadistica> porTema,
        List<PreguntaDificil> preguntasDificiles,
        List<ActividadDia> actividad
) {

    /** Aciertos por tema (sobre todos los intentos del grupo). */
    public record TemaEstadistica(
            String tema,
            long intentos,
            long correctas,
            double porcentaje,
            long estudiantes,
            long preguntasActivas
    ) {}

    /** Pregunta con mayor porcentaje de fallo, y la opción incorrecta que más eligen. */
    public record PreguntaDificil(
            Long id,
            String enunciado,
            String tema,
            String dificultad,
            long intentos,
            long fallos,
            double porcentajeFallo,
            String errorFrecuente
    ) {}

    /** Intentos por día (últimos 14 días, hora de Colombia). */
    public record ActividadDia(LocalDate fecha, long intentos, long estudiantes) {}
}
