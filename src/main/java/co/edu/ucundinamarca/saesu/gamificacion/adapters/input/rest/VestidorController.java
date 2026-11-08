package co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.request.CompraRequest;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.VestidorResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.VestidorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Adaptador de entrada: Sumi Monedas y vestidor de Sumi. */
@RestController
@RequestMapping("/api/sumi")
@Tag(name = "Vestidor de Sumi", description = "Sumi Monedas, compras y estilo de la mascota")
public class VestidorController {

    private final VestidorService servicio;

    public VestidorController(VestidorService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Estado del vestidor", description = "Saldo de Sumi Monedas, prendas compradas y lo que Sumi lleva puesto. Requiere JWT.")
    @GetMapping("/vestidor")
    public ResponseEntity<VestidorResponse> estado(Authentication authentication) {
        return ResponseEntity.ok(servicio.estado(authentication.getName()));
    }

    @Operation(summary = "Comprar una prenda", description = "Descuenta las Sumi Monedas y agrega la prenda a la colección. 409 si ya la tiene o no le alcanza. Requiere JWT.")
    @PostMapping("/vestidor/comprar")
    public ResponseEntity<VestidorResponse> comprar(@Valid @RequestBody CompraRequest compra, Authentication authentication) {
        return ResponseEntity.ok(servicio.comprar(authentication.getName(), compra));
    }

    @Operation(summary = "Vestir a Sumi", description = "Guarda lo que lleva puesto (categoría → prenda). Solo prendas gratis o compradas. Requiere JWT.")
    @PutMapping("/vestidor/estilo")
    public ResponseEntity<VestidorResponse> vestir(@RequestBody Map<String, String> estilo, Authentication authentication) {
        return ResponseEntity.ok(servicio.vestir(authentication.getName(), estilo));
    }

    @Operation(summary = "Historial de Sumi Monedas", description = "Últimos movimientos: positivos si las ganó, negativos si las gastó. Requiere JWT.")
    @GetMapping("/monedas/historial")
    public ResponseEntity<List<MovimientoMonedaResponse>> historial(Authentication authentication) {
        return ResponseEntity.ok(servicio.historial(authentication.getName()));
    }
}
