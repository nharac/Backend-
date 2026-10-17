package co.edu.ucundinamarca.saesu.foro.adapters.input.rest;

import co.edu.ucundinamarca.saesu.foro.application.dto.request.CrearPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.request.ResponderPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.application.service.EliminarPublicacionService;
import co.edu.ucundinamarca.saesu.foro.ports.input.CrearPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.ListarPublicacionesUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.ResponderPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.input.VerDetallePublicacionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/foro")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Foro", description = "Endpoints del foro colaborativo de dudas académicas")
public class ForoController {

    private final ListarPublicacionesUseCase listarPublicacionesUseCase;
    private final CrearPublicacionUseCase crearPublicacionUseCase;
    private final VerDetallePublicacionUseCase verDetallePublicacionUseCase;
    private final ResponderPublicacionUseCase responderPublicacionUseCase;
    private final EliminarPublicacionService eliminarPublicacionService;

    public ForoController(ListarPublicacionesUseCase listarPublicacionesUseCase,
                          CrearPublicacionUseCase crearPublicacionUseCase,
                          VerDetallePublicacionUseCase verDetallePublicacionUseCase,
                          ResponderPublicacionUseCase responderPublicacionUseCase,
                          EliminarPublicacionService eliminarPublicacionService) {
        this.listarPublicacionesUseCase = listarPublicacionesUseCase;
        this.crearPublicacionUseCase = crearPublicacionUseCase;
        this.verDetallePublicacionUseCase = verDetallePublicacionUseCase;
        this.responderPublicacionUseCase = responderPublicacionUseCase;
        this.eliminarPublicacionService = eliminarPublicacionService; // <-- LÍNEA AGREGADA
    }

    @Operation(
            summary = "Listar publicaciones",
            description = "Lista publicaciones con filtros opcionales (tema, búsqueda, orden) y paginación. Requiere JWT."
    )
    @GetMapping("/publicaciones")
    public ResponseEntity<List<PublicacionResumenResponse>> listarPublicaciones(
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "reciente") String orden,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                listarPublicacionesUseCase.listar(tema, busqueda, orden, page, size)
        );
    }

    @Operation(
            summary = "Crear publicación",
            description = "Crea una nueva publicación en el foro. Requiere JWT."
    )
    @PostMapping("/publicaciones")
    public ResponseEntity<PublicacionDetalleResponse> crearPublicacion(
            @Valid @RequestBody CrearPublicacionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                crearPublicacionUseCase.crear(request, authentication.getName())
        );
    }

    @Operation(
            summary = "Ver detalle de publicación",
            description = "Devuelve una publicación con todas sus respuestas anidadas. Requiere JWT."
    )
    @GetMapping("/publicaciones/{id}")
    public ResponseEntity<PublicacionDetalleResponse> verDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(verDetallePublicacionUseCase.verDetalle(id));
    }

    @Operation(
            summary = "Responder publicación",
            description = "Crea una respuesta a una publicación. Si respuestaPadreId está presente, es respuesta anidada (1 nivel). Requiere JWT."
    )
    @PostMapping("/publicaciones/{id}/respuestas")
    public ResponseEntity<RespuestaResponse> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderPublicacionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                responderPublicacionUseCase.responder(id, request, authentication.getName())
        );
    }

    // ==========================================
    // NUEVO ENDPOINT PARA ELIMINAR PUBLICACIÓN
    // ==========================================
    @Operation(
            summary = "Eliminar publicación",
            description = "Elimina una pregunta del foro. Solo el autor o un docente pueden hacerlo."
    )
    @DeleteMapping("/publicaciones/{id}")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Long id,
            Authentication authentication
    ) {
        eliminarPublicacionService.ejecutar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}