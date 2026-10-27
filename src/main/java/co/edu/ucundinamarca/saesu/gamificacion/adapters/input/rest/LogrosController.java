package co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.LogrosService;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
@Tag(name = "Gamificación", description = "Logros del estudiante")
public class LogrosController {

    private final LogrosService logrosService;
    private final UsuarioJpaRepository usuarioRepository;

    public LogrosController(LogrosService logrosService, UsuarioJpaRepository usuarioRepository) {
        this.logrosService = logrosService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Mis logros",
            description = "Devuelve los 11 logros con su estado (obtenido y fecha). Evalúa primero si se desbloqueó alguno nuevo. Requiere JWT.")
    @GetMapping
    public ResponseEntity<List<LogroResponse>> misLogros(Authentication authentication) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(logrosService.listar(usuario.getId()));
    }
}
