package co.edu.ucundinamarca.saesu.duelo.adapters.input.rest;

import co.edu.ucundinamarca.saesu.duelo.application.dto.request.EntregaDueloRequest;
import co.edu.ucundinamarca.saesu.duelo.application.dto.request.RetarRequest;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.CompaneroResponse;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.DueloResponse;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.PreguntaDueloResponse;
import co.edu.ucundinamarca.saesu.duelo.application.service.DuelosService;
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

import java.util.List;

@RestController
@RequestMapping("/api/duelos")
@Tag(name = "Retos entre compañeros", description = "Dos estudiantes responden las mismas preguntas; gana quien acierte más")
public class DuelosController {

    private final DuelosService duelos;

    public DuelosController(DuelosService duelos) {
        this.duelos = duelos;
    }

    @Operation(summary = "Compañeros a los que puedo retar")
    @GetMapping("/companeros")
    public List<CompaneroResponse> companeros(Authentication auth) {
        return duelos.companeros(auth.getName());
    }

    @Operation(summary = "Mis retos (enviados y recibidos)")
    @GetMapping
    public List<DueloResponse> misDuelos(Authentication auth) {
        return duelos.misDuelos(auth.getName());
    }

    @Operation(summary = "Retar a un compañero",
            description = "Modos: CLASICO (5 preguntas), MUERTE_SUBITA (racha hasta el primer error, 10), PUNTOS (según dificultad, 8) y RELAMPAGO (10 en 60 s).")
    @PostMapping
    public ResponseEntity<DueloResponse> retar(@Valid @RequestBody RetarRequest r, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(duelos.retar(auth.getName(), r.rivalId(), r.tema(), r.modo()));
    }

    @Operation(summary = "Preguntas del reto (sin la respuesta)", description = "400 si ya lo respondí, 404 si no participo.")
    @GetMapping("/{id}/preguntas")
    public List<PreguntaDueloResponse> preguntas(@PathVariable Long id, Authentication auth) {
        return duelos.preguntas(auth.getName(), id);
    }

    @Operation(summary = "Entregar mis respuestas del reto")
    @PostMapping("/{id}/entregar")
    public DueloResponse entregar(@PathVariable Long id, @Valid @RequestBody EntregaDueloRequest r, Authentication auth) {
        return duelos.entregar(auth.getName(), id, r);
    }
}
