package co.edu.ucundinamarca.saesu.tarea.adapters.input.rest;

import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;
import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaDocenteResponse;
import co.edu.ucundinamarca.saesu.tarea.application.service.TareasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/docente/tareas")
@Tag(name = "Docente - Tareas", description = "Asignar práctica a un estudiante o a todo el grupo")
@PreAuthorize("hasRole('DOCENTE')")
public class DocenteTareasController {

    private final TareasService tareas;

    public DocenteTareasController(TareasService tareas) {
        this.tareas = tareas;
    }

    @Operation(summary = "Tareas creadas por el docente, con cuántos estudiantes ya la completaron")
    @GetMapping
    public List<TareaDocenteResponse> listar(Authentication auth) {
        return tareas.delDocente(auth.getName());
    }

    @Operation(summary = "Asignar una tarea", description = "Sin estudianteId se asigna a todo el grupo.")
    @PostMapping
    public ResponseEntity<TareaDocenteResponse> crear(@Valid @RequestBody TareaRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareas.crear(request, auth.getName()));
    }

    @Operation(summary = "Eliminar una tarea propia")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        return tareas.eliminar(id, auth.getName()) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
