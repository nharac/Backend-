package co.edu.ucundinamarca.saesu.core.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/** Endpoint público y liviano para probar el balanceo de NGINX entre réplicas. */
@RestController
@Tag(name = "Infraestructura", description = "Prueba del balanceo entre réplicas")
public class InstanciaController {

    private final String instancia;

    public InstanciaController(@Value("${saesu.instancia:local}") String instancia) {
        this.instancia = instancia;
    }

    @Operation(summary = "Qué réplica del backend respondió", description = "No requiere sesión. También va en el encabezado X-Instancia.")
    @GetMapping("/api/instancia")
    public Map<String, String> instancia() {
        return Map.of("instancia", instancia, "hora", Instant.now().toString());
    }
}
