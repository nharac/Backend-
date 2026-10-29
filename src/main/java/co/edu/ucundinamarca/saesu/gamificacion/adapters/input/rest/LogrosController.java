package co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.LogrosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Adaptador de entrada: solo traduce HTTP al puerto LogrosUseCase (no conoce JPA ni SQL). */
@RestController
@RequestMapping("/api/logros")
@Tag(name = "Gamificación", description = "Logros del estudiante")
public class LogrosController {

    private final LogrosUseCase logros;

    public LogrosController(LogrosUseCase logros) {
        this.logros = logros;
    }

    @Operation(summary = "Mis logros",
            description = "Devuelve los 11 logros con su estado (obtenido y fecha). Evalúa primero si se desbloqueó alguno nuevo. Requiere JWT.")
    @GetMapping
    public ResponseEntity<List<LogroResponse>> misLogros(Authentication authentication) {
        return ResponseEntity.ok(logros.listar(authentication.getName()));
    }
}
