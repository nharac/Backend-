package co.edu.ucundinamarca.saesu.quiz.domain.model;

import java.util.List;
import java.util.Optional;

/** Pregunta del banco con sus opciones, tal como se usa en un quiz. */
public record PreguntaQuiz(Long id, String tema, String dificultad, String enunciado, String pista,
                           List<Opcion> opciones) {

    public record Opcion(String letra, String texto, boolean correcta) {}

    public Optional<String> letraCorrecta() {
        return opciones.stream().filter(Opcion::correcta).map(Opcion::letra).findFirst();
    }
}
