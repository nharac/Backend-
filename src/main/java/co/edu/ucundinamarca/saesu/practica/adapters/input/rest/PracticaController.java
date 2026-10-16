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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Práctica", description = "Endpoints de preguntas tipo ICFES, validación, pistas y progreso")
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

    @Operation(
            summary = "Listar preguntas",
            description = "Devuelve preguntas paginadas con filtros opcionales por tema y dificultad. Requiere JWT."
    )
    @GetMapping("/preguntas")
    public ResponseEntity<List<PreguntaResponse>> listarPreguntas(
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String dificultad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(listarPreguntasUseCase.listar(tema, dificultad, page, size));
    }

    @Operation(
            summary = "Responder pregunta",
            description = "Valida la opción seleccionada. Si acierta, habilita 'Siguiente'. Si falla, devuelve pista y activa chat IA. Requiere JWT."
    )
    @PostMapping("/preguntas/{id}/responder")
    public ResponseEntity<ValidacionResponse> responder(
            @PathVariable Long id,
            @RequestBody ResponderPreguntaRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(validarRespuestaUseCase.validar(
                id, request.getOpcionId(), authentication.getName()));
    }

    @Operation(
            summary = "Obtener pista",
            description = "Devuelve la pista de una pregunta sin revelar la respuesta correcta. Requiere JWT."
    )
    @GetMapping("/preguntas/{id}/pista")
    public ResponseEntity<PistaResponse> obtenerPista(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerPistaUseCase.obtenerPista(id));
    }

    @Operation(
            summary = "Ver progreso",
            description = "Devuelve el progreso del estudiante autenticado agrupado por tema. Requiere JWT."
    )
    @GetMapping("/progreso")
    public ResponseEntity<ProgresoResponse> obtenerProgreso(Authentication authentication) {
        return ResponseEntity.ok(obtenerProgresoUseCase.obtenerProgreso(authentication.getName()));
    }
}