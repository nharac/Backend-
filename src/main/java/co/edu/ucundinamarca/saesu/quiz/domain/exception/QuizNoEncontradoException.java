package co.edu.ucundinamarca.saesu.quiz.domain.exception;

/** El quiz no existe o es de otra persona (en ambos casos se responde 404). */
public class QuizNoEncontradoException extends RuntimeException {
    public QuizNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
