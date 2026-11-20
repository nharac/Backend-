package co.edu.ucundinamarca.saesu.docente.application.dto.response;

/**
 * Alerta automática para el docente.
 *
 * @param tipo        INACTIVO, SIN_EMPEZAR, TEMA_DIFICIL, QUIZ_BAJO o GRUPO_TEMA
 * @param gravedad    "alta" o "media"
 * @param estudianteId estudiante afectado (null en alertas del grupo)
 * @param tema        tema relacionado (puede ser null)
 */
public record AlertaDocenteResponse(
        String tipo,
        String gravedad,
        Long estudianteId,
        String estudiante,
        String tema,
        String mensaje,
        String sugerencia
) {}
