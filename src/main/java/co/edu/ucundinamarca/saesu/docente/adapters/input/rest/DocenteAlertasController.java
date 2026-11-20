package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.AlertaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.application.service.AlertasDocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/docente")
@Tag(name = "Docente - Alertas", description = "Alertas automáticas sobre estudiantes y temas")
@PreAuthorize("hasRole('DOCENTE')")
public class DocenteAlertasController {

    private final AlertasDocenteService alertas;

    public DocenteAlertasController(AlertasDocenteService alertas) {
        this.alertas = alertas;
    }

    @Operation(summary = "Alertas automáticas",
            description = "Inactivos, estudiantes que no empezaron, bajo acierto por tema, quices bajos y temas difíciles para el grupo.")
    @GetMapping("/alertas")
    public List<AlertaDocenteResponse> alertas() {
        return alertas.alertas();
    }
}
