package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.CrearSalaRequest;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.EstadoSala;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.MiRespuesta;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.ResponderRequest;
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

@RestController
@RequestMapping("/api/vivo/salas")
@Tag(name = "Retos en vivo", description = "Dos estudiantes responden las mismas preguntas al mismo tiempo (el cliente consulta cada segundo)")
public class VivoController {

    private final VivoService vivo;

    public VivoController(VivoService vivo) {
        this.vivo = vivo;
    }

    @Operation(summary = "Crear sala", description = "Con invitadoId se invita a un compañero; sin él, entra quien tenga el código.")
    @PostMapping
    public ResponseEntity<EstadoSala> crear(@Valid @RequestBody CrearSalaRequest r, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vivo.crear(auth.getName(), r.invitadoId(), r.tema()));
    }

    @Operation(summary = "Estado de la sala en este momento (consultar cada segundo)")
    @GetMapping("/{codigo}")
    public EstadoSala ver(@PathVariable String codigo, Authentication auth) {
        return vivo.ver(auth.getName(), codigo);
    }

    @Operation(summary = "Unirse a la sala (arranca la cuenta regresiva)")
    @PostMapping("/{codigo}/unirse")
    public EstadoSala unirse(@PathVariable String codigo, Authentication auth) {
        return vivo.unirse(auth.getName(), codigo);
    }

    @Operation(summary = "Responder la pregunta actual")
    @PostMapping("/{codigo}/responder")
    public MiRespuesta responder(@PathVariable String codigo, @Valid @RequestBody ResponderRequest r, Authentication auth) {
        return vivo.responder(auth.getName(), codigo, r.orden(), r.letra());
    }

    @Operation(summary = "Cancelar la sala (solo el anfitrión, mientras espera)")
    @PostMapping("/{codigo}/cancelar")
    public EstadoSala cancelar(@PathVariable String codigo, Authentication auth) {
        return vivo.cancelar(auth.getName(), codigo);
    }
}
