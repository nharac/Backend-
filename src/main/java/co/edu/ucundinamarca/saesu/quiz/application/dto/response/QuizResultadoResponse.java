package co.edu.ucundinamarca.saesu.quiz.application.dto.response;

import java.time.Instant;
import java.util.List;

/** Calificación del quiz con la revisión pregunta por pregunta. */
public record QuizResultadoResponse(
        Long intentoId,
        String tema,
        int correctas,
        int total,
        int porcentaje,
        long duracionSegundos,
        boolean fueraDeTiempo,
        Instant finalizadoEn,
        boolean mejorMarca,
        List<Detalle> detalle) {

    public record Detalle(int orden, Long preguntaId, String tema, String enunciado,
                          List<QuizIniciadoResponse.Opcion> opciones,
                          String letraElegida, String letraCorrecta, boolean correcta, String pista) {}
}
