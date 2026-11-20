package co.edu.ucundinamarca.saesu.tarea.adapters.input.rest;

import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaEstudianteResponse;
import co.edu.ucundinamarca.saesu.tarea.application.service.TareasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@Tag(name = "Tareas", description = "Tareas asignadas por el docente")
public class TareasEstudianteController {

    private final TareasService tareas;

    public TareasEstudianteController(TareasService tareas) {
        this.tareas = tareas;
    }

    @Operation(summary = "Mis tareas con su avance", description = "Pendientes primero, luego vencidas y al final completadas.")
    @GetMapping
    public List<TareaEstudianteResponse> misTareas(Authentication auth) {
        return tareas.misTareas(auth.getName());
    }
}
