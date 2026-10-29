package co.edu.ucundinamarca.saesu.quiz.application.dto.response;

import java.time.Instant;
import java.util.List;

/** Quiz listo para responder. Las opciones van SIN marcar cuál es la correcta. */
public record QuizIniciadoResponse(
        Long intentoId,
        String tema,
        int cantidad,
        int segundosLimite,
        Instant limiteEn,
        List<Pregunta> preguntas) {

    public record Pregunta(int orden, Long preguntaId, String tema, String dificultad, String enunciado,
                           List<Opcion> opciones) {}

    public record Opcion(String letra, String texto) {}
}
