package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.ExplicarEjercicioRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ExplicacionResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.ExplicarEjercicioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada de la calculadora paso a paso del tutor IA. */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat IA", description = "Endpoints del chat con el tutor IA (Gemini)")
public class ExplicarEjercicioController {

    private final ExplicarEjercicioUseCase explicarEjercicio;

    public ExplicarEjercicioController(ExplicarEjercicioUseCase explicarEjercicio) {
        this.explicarEjercicio = explicarEjercicio;
    }

    @Operation(summary = "Resolver un ejercicio paso a paso",
            description = "El tutor resuelve el ejercicio del estudiante explicando el porqué de cada paso, en lenguaje sencillo. "
                    + "Solo temas de matemáticas; cuenta en el límite diario del chat. Requiere JWT.")
    @PostMapping("/explicar")
    public ResponseEntity<ExplicacionResponse> explicar(@Valid @RequestBody ExplicarEjercicioRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(explicarEjercicio.explicar(request.ejercicio(), authentication.getName()));
    }
}
