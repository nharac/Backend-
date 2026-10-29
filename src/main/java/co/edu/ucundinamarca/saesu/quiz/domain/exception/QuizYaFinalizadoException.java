package co.edu.ucundinamarca.saesu.quiz.domain.exception;

/** Se intentó entregar dos veces el mismo quiz (409). */
public class QuizYaFinalizadoException extends RuntimeException {
    public QuizYaFinalizadoException(String mensaje) {
        super(mensaje);
    }
}
