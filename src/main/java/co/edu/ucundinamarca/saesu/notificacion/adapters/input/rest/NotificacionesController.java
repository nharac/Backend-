package co.edu.ucundinamarca.saesu.notificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.notificacion.application.dto.BandejaResponse;
import co.edu.ucundinamarca.saesu.notificacion.application.service.NotificacionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Campanita: retos, tareas, logros y respuestas del foro")
public class NotificacionesController {

    private final NotificacionesService servicio;

    public NotificacionesController(NotificacionesService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Mis últimas notificaciones y cuántas faltan por leer")
    @GetMapping
    public BandejaResponse bandeja(Authentication auth) {
        return servicio.bandeja(auth.getName());
    }

    @Operation(summary = "Solo el número de no leídas (para la campanita)")
    @GetMapping("/no-leidas")
    public Map<String, Long> noLeidas(Authentication auth) {
        return Map.of("noLeidas", servicio.noLeidas(auth.getName()));
    }

    @Operation(summary = "Marcar una como leída")
    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> leida(@PathVariable Long id, Authentication auth) {
        return servicio.marcarLeida(auth.getName(), id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Marcar todas como leídas")
    @PostMapping("/leer-todas")
    public Map<String, Integer> leerTodas(Authentication auth) {
        return Map.of("marcadas", servicio.marcarTodas(auth.getName()));
    }
}
