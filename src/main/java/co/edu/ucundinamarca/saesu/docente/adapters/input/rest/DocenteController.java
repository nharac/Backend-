package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;
import co.edu.ucundinamarca.saesu.docente.application.service.ModeracionForoService;
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
    private final ModeracionForoService moderacionService;

    public DocenteController(GestionDocenteUseCase gestionDocenteUseCase, ModeracionForoService moderacionService) {
        this.gestionDocenteUseCase = gestionDocenteUseCase;
        this.moderacionService = moderacionService;
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

    // ============================================
    // Moderación del foro
    // ============================================

    @Operation(summary = "Contenido del foro ocultado por docentes")
    @GetMapping("/moderacion")
    public List<ContenidoOcultoResponse> contenidoOculto() {
        return moderacionService.listarOculto();
    }

    @Operation(summary = "Ocultar una respuesta del foro")
    @PostMapping("/respuestas/{id}/ocultar")
    public ResponseEntity<Void> ocultarRespuesta(@PathVariable Long id, Authentication auth) {
        moderacionService.ocultarRespuesta(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restaurar una respuesta oculta")
    @DeleteMapping("/respuestas/{id}/ocultar")
    public ResponseEntity<Void> restaurarRespuesta(@PathVariable Long id) {
        moderacionService.restaurarRespuesta(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ocultar una publicación del foro")
    @PostMapping("/publicaciones/{id}/ocultar")
    public ResponseEntity<Void> ocultarPublicacion(@PathVariable Long id, Authentication auth) {
        moderacionService.ocultarPublicacion(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restaurar una publicación oculta")
    @DeleteMapping("/publicaciones/{id}/ocultar")
    public ResponseEntity<Void> restaurarPublicacion(@PathVariable Long id) {
        moderacionService.restaurarPublicacion(id);
        return ResponseEntity.noContent().build();
    }
}
