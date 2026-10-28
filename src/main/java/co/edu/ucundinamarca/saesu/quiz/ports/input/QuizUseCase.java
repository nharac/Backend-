package co.edu.ucundinamarca.saesu.quiz.ports.input;

import co.edu.ucundinamarca.saesu.quiz.application.dto.request.FinalizarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.request.IniciarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizHistorialResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizIniciadoResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizResultadoResponse;

public interface QuizUseCase {

    QuizIniciadoResponse iniciar(String correo, IniciarQuizRequest request);

    QuizResultadoResponse finalizar(String correo, Long intentoId, FinalizarQuizRequest request);

    QuizResultadoResponse resultado(String correo, Long intentoId);

    QuizHistorialResponse historial(String correo);
}
