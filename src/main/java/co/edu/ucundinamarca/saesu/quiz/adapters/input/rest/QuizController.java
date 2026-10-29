package co.edu.ucundinamarca.saesu.quiz.adapters.input.rest;

import co.edu.ucundinamarca.saesu.quiz.application.dto.request.FinalizarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.request.IniciarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizHistorialResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizIniciadoResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizResultadoResponse;
import co.edu.ucundinamarca.saesu.quiz.ports.input.QuizUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz", description = "Evaluación cronometrada con preguntas del banco de práctica")
public class QuizController {

    private final QuizUseCase quiz;

    public QuizController(QuizUseCase quiz) {
        this.quiz = quiz;
    }

    @Operation(summary = "Empezar un quiz",
            description = "Elige al azar de 5 a 20 preguntas (de un tema o mixto) y arranca el cronómetro: 90 s por pregunta. "
                    + "Las opciones no indican cuál es la correcta. Requiere JWT.")
    @PostMapping("/iniciar")
    public ResponseEntity<QuizIniciadoResponse> iniciar(@Valid @RequestBody IniciarQuizRequest request,
                                                        Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz.iniciar(auth.getName(), request));
    }

    @Operation(summary = "Entregar un quiz",
            description = "Califica las respuestas y devuelve la revisión con la opción correcta y la pista de cada pregunta. "
                    + "Solo se puede entregar una vez (409 si se repite). Requiere JWT.")
    @PostMapping("/{id}/finalizar")
    public QuizResultadoResponse finalizar(@PathVariable Long id, @Valid @RequestBody FinalizarQuizRequest request,
                                           Authentication auth) {
        return quiz.finalizar(auth.getName(), id, request);
    }

    @Operation(summary = "Ver el resultado de un quiz ya entregado", description = "Requiere JWT.")
    @GetMapping("/{id}")
    public QuizResultadoResponse resultado(@PathVariable Long id, Authentication auth) {
        return quiz.resultado(auth.getName(), id);
    }

    @Operation(summary = "Mis quices",
            description = "Cantidad de quices entregados, promedio, mejor resultado y los 10 más recientes. Requiere JWT.")
    @GetMapping("/historial")
    public QuizHistorialResponse historial(Authentication auth) {
        return quiz.historial(auth.getName());
    }
}
