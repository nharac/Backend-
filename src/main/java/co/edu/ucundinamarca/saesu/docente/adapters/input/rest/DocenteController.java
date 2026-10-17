package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.ports.input.GestionDocenteUseCase;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docente")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Docente", description = "Endpoints del panel de docente")
@PreAuthorize("hasRole('DOCENTE')") // Seguridad: Solo docentes
public class DocenteController {

    private final GestionDocenteUseCase gestionDocenteUseCase;

    public DocenteController(GestionDocenteUseCase gestionDocenteUseCase) {
        this.gestionDocenteUseCase = gestionDocenteUseCase;
    }

    @Operation(summary = "Listar publicaciones sin respuesta oficial")
    @GetMapping("/notificaciones")
    public ResponseEntity<List<PublicacionResumenResponse>> notificaciones() {
        return ResponseEntity.ok(gestionDocenteUseCase.listarPublicacionesSinOficial());
    }

    @Operation(summary = "Marcar respuesta como oficial")
    @PostMapping("/respuestas/{id}/oficial")
    public ResponseEntity<Void> marcarOficial(@PathVariable Long id, Authentication auth) {
        gestionDocenteUseCase.marcarRespuestaOficial(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Quitar estado oficial de una respuesta")
    @DeleteMapping("/respuestas/{id}/oficial")
    public ResponseEntity<Void> quitarOficial(@PathVariable Long id) {
        gestionDocenteUseCase.quitarRespuestaOficial(id);
        return ResponseEntity.ok().build();
    }
}