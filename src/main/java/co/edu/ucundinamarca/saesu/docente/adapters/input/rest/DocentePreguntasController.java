package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.GenerarPreguntasRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.application.service.BancoPreguntasService;
import co.edu.ucundinamarca.saesu.docente.application.service.GenerarPreguntasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docente/preguntas")
@Tag(name = "Docente - Banco de preguntas", description = "Crear, editar y desactivar preguntas de práctica")
@PreAuthorize("hasRole('DOCENTE')")
public class DocentePreguntasController {

    private final BancoPreguntasService bancoService;
    private GenerarPreguntasService generador;

    public DocentePreguntasController(BancoPreguntasService bancoService) {
        this.bancoService = bancoService;
    }

    @Operation(summary = "Listar preguntas (estado: activas | inactivas | todas)")
    @GetMapping
    public List<PreguntaDocenteResponse> listar(
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String dificultad,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "todas") String estado) {
        return bancoService.listar(tema, dificultad, busqueda, estado);
    }

    @Operation(summary = "Ver una pregunta con sus opciones")
    @GetMapping("/{id}")
    public PreguntaDocenteResponse obtener(@PathVariable Long id) {
        return bancoService.obtener(id);
    }

    @Operation(summary = "Crear pregunta (4 opciones A-D, una correcta)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaDocenteResponse crear(@Valid @RequestBody PreguntaDocenteRequest request, Authentication auth) {
        return bancoService.crear(request, auth.getName());
    }

    @Operation(summary = "Editar pregunta")
    @PutMapping("/{id}")
    public PreguntaDocenteResponse actualizar(@PathVariable Long id, @Valid @RequestBody PreguntaDocenteRequest request) {
        return bancoService.actualizar(id, request);
    }

    @Operation(summary = "Activar o desactivar una pregunta. Body: { \"activa\": false }")
    @PatchMapping("/{id}/estado")
    public PreguntaDocenteResponse cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean activa = body.get("activa");
        if (activa == null) {
            throw new IllegalArgumentException("Falta el campo 'activa' (true o false)");
        }
        return bancoService.cambiarEstado(id, activa);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public void setGenerador(GenerarPreguntasService generador) {
        this.generador = generador;
    }

    @Operation(summary = "Borradores de preguntas con IA",
            description = "La IA propone de 1 a 5 preguntas del tema y nivel pedidos. No se guardan: el docente revisa y publica las que quiera.")
    @PostMapping("/generar")
    public List<PreguntaDocenteRequest> generar(
            @Valid @RequestBody GenerarPreguntasRequest request) {
        return generador.generar(request);
    }
}
