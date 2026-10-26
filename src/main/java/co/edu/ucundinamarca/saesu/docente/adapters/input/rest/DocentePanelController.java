package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.application.service.EstadisticasDocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/docente")
@Tag(name = "Docente - Estadísticas", description = "Progreso del grupo y de cada estudiante")
@PreAuthorize("hasRole('DOCENTE')")
public class DocentePanelController {

    private final EstadisticasDocenteService estadisticasService;

    public DocentePanelController(EstadisticasDocenteService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @Operation(summary = "Estadísticas del grupo (aciertos por tema, preguntas más falladas, actividad)")
    @GetMapping("/estadisticas")
    public EstadisticasGrupoResponse estadisticas() {
        return estadisticasService.estadisticasGrupo();
    }

    @Operation(summary = "Lista de estudiantes con su progreso y si están en riesgo")
    @GetMapping("/estudiantes")
    public List<EstudianteResumenResponse> estudiantes() {
        return estadisticasService.listarEstudiantes();
    }

    @Operation(summary = "Exportar el progreso de los estudiantes a CSV (Excel)")
    @GetMapping(value = "/estudiantes/exportar", produces = "text/csv")
    public ResponseEntity<byte[]> exportar() {
        byte[] csv = estadisticasService.exportarCsv().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"progreso-estudiantes-" + LocalDate.now() + ".csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv);
    }

    @Operation(summary = "Detalle de un estudiante")
    @GetMapping("/estudiantes/{id}")
    public EstudianteDetalleResponse estudiante(@PathVariable Long id) {
        return estadisticasService.detalleEstudiante(id);
    }
}
