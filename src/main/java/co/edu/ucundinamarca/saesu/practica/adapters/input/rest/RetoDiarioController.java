package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.RetoRespuestaRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RetoDiarioResponse;
import co.edu.ucundinamarca.saesu.practica.application.service.RetoDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada: reto del día (una sola oportunidad). */
@RestController
@RequestMapping("/api/practica/reto")
@Tag(name = "Reto del día", description = "Una pregunta al día con una sola oportunidad")
public class RetoDiarioController {

    private final RetoDiarioService servicio;

    public RetoDiarioController(RetoDiarioService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Reto de hoy", description = "Pregunta del día y si ya se ganó o perdió. Requiere JWT.")
    @GetMapping
    public ResponseEntity<RetoDiarioResponse> hoy(Authentication authentication) {
        return ResponseEntity.ok(servicio.hoy(authentication.getName()));
    }

    @Operation(summary = "Responder el reto", description = "Una sola respuesta por día: +30 puntos si acierta; si falla, lo pierde. 409 si ya respondió. Requiere JWT.")
    @PostMapping("/responder")
    public ResponseEntity<RetoDiarioResponse> responder(@Valid @RequestBody RetoRespuestaRequest respuesta, Authentication authentication) {
        return ResponseEntity.ok(servicio.responder(authentication.getName(), respuesta.opcionId()));
    }
}
