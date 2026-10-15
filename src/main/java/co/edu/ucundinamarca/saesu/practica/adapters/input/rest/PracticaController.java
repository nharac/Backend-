package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.ResponderPreguntaRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PistaResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PreguntaResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ProgresoResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ValidacionResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.ListarPreguntasUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerPistaUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerProgresoUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.input.ValidarRespuestaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/practica")
@CrossOrigin(origins = "http://localhost:4200")
public class PracticaController {

    private final ListarPreguntasUseCase listarPreguntasUseCase;
    private final ValidarRespuestaUseCase validarRespuestaUseCase;
    private final ObtenerPistaUseCase obtenerPistaUseCase;
    private final ObtenerProgresoUseCase obtenerProgresoUseCase;

    public PracticaController(ListarPreguntasUseCase listarPreguntasUseCase,
                              ValidarRespuestaUseCase validarRespuestaUseCase,
                              ObtenerPistaUseCase obtenerPistaUseCase,
                              ObtenerProgresoUseCase obtenerProgresoUseCase) {
        this.listarPreguntasUseCase = listarPreguntasUseCase;
        this.validarRespuestaUseCase = validarRespuestaUseCase;
        this.obtenerPistaUseCase = obtenerPistaUseCase;
        this.obtenerProgresoUseCase = obtenerProgresoUseCase;
    }

    @GetMapping("/preguntas")
    public ResponseEntity<List<PreguntaResponse>> listarPreguntas(
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String dificultad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(listarPreguntasUseCase.listar(tema, dificultad, page, size));
    }

    @PostMapping("/preguntas/{id}/responder")
    public ResponseEntity<ValidacionResponse> responder(
            @PathVariable Long id,
            @RequestBody ResponderPreguntaRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(validarRespuestaUseCase.validar(
                id, request.getOpcionId(), authentication.getName()));
    }

    @GetMapping("/preguntas/{id}/pista")
    public ResponseEntity<PistaResponse> obtenerPista(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerPistaUseCase.obtenerPista(id));
    }

    @GetMapping("/progreso")
    public ResponseEntity<ProgresoResponse> obtenerProgreso(Authentication authentication) {
        return ResponseEntity.ok(obtenerProgresoUseCase.obtenerProgreso(authentication.getName()));
    }
}