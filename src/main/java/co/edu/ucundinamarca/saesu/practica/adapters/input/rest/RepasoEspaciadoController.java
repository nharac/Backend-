package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.ports.output.RepasoEspaciadoPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RepasoEspaciadoPort.Resumen;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/practica/repaso")
@Tag(name = "Práctica", description = "Repaso espaciado")
public class RepasoEspaciadoController {

    private final RepasoEspaciadoPort repaso;
    private final UsuarioRepositoryPort usuarios;
    private final ZoneId zona;

    public RepasoEspaciadoController(RepasoEspaciadoPort repaso, UsuarioRepositoryPort usuarios, ZoneId zona) {
        this.repaso = repaso;
        this.usuarios = usuarios;
        this.zona = zona;
    }

    @Operation(summary = "Resumen del repaso espaciado",
            description = "Cuántas preguntas tocan hoy, mañana y esta semana, y cuántas ya están dominadas (1 → 3 → 7 días).")
    @GetMapping("/resumen")
    public Resumen resumen(Authentication auth) {
        Long id = usuarios.buscarPorCorreo(auth.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado")).getId();
        return repaso.resumen(id, LocalDate.now(zona));
    }
}
