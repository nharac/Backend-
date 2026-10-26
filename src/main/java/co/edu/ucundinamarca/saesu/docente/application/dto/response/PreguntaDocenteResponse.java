package co.edu.ucundinamarca.saesu.docente.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Pregunta del banco vista por el docente: incluye la opción correcta y cómo le ha ido al grupo.
 */
public record PreguntaDocenteResponse(
        Long id,
        String tema,
        String enunciado,
        String dificultad,
        String pista,
        boolean activa,
        long intentos,
        double porcentajeAcierto,
        LocalDateTime fechaActualizacion,
        List<OpcionDocente> opciones
) {

    public record OpcionDocente(Long id, String letra, String texto, boolean esCorrecta) {}
}
