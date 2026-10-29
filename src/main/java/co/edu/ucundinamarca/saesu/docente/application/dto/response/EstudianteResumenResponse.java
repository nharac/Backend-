package co.edu.ucundinamarca.saesu.docente.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fila de la lista de estudiantes del panel docente.
 * GET /api/docente/estudiantes
 */
public record EstudianteResumenResponse(
        Long id,
        String nombre,
        String correo,
        String fotoPerfilUrl,
        long puntos,
        int racha,
        long intentos,
        long correctas,
        double porcentaje,
        LocalDateTime ultimaActividad,
        boolean enRiesgo,
        List<String> motivosRiesgo,
        List<TemaProgreso> porTema,
        /** "nuevo" (recién registrado, aún en su tiempo de gracia), "al-dia" o "en-riesgo". */
        String estado,
        /** Si es nuevo: días que le quedan antes de que su inactividad cuente como riesgo. */
        Integer diasDeGracia,
        long quices,
        /** Promedio de sus quices (0-100); null si no ha hecho ninguno. */
        Double promedioQuiz
) {

    /** Progreso del estudiante en un tema. */
    public record TemaProgreso(
            String tema,
            long intentos,
            long correctas,
            double porcentaje,
            long resueltas,
            long totalPreguntas
    ) {}
}
