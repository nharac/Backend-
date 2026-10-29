package co.edu.ucundinamarca.saesu.docente.adapters.input.rest;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.application.service.EstadisticasDocenteService;
import co.edu.ucundinamarca.saesu.docente.application.service.ExportarEstudiantesService;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizResultadoResponse;
import co.edu.ucundinamarca.saesu.quiz.ports.input.QuizUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final ExportarEstudiantesService exportarService;
    private final QuizUseCase quiz;

    public DocentePanelController(EstadisticasDocenteService estadisticasService,
                                  ExportarEstudiantesService exportarService,
                                  QuizUseCase quiz) {
        this.estadisticasService = estadisticasService;
        this.exportarService = exportarService;
        this.quiz = quiz;
    }

    @Operation(summary = "Revisión de un quiz de un estudiante",
            description = "Pregunta por pregunta: qué respondió, cuál era la correcta y la pista. 404 si el quiz no es de ese estudiante.")
    @GetMapping("/estudiantes/{id}/quices/{intentoId}")
    public QuizResultadoResponse quizDeEstudiante(@PathVariable Long id, @PathVariable Long intentoId) {
        return quiz.resultadoDeEstudiante(id, intentoId);
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

    @Operation(summary = "Descargar el progreso de los estudiantes",
            description = "Por defecto un Excel (.xlsx) con formato: hoja Estudiantes y hoja Resumen del grupo. "
                    + "Con ?formato=csv devuelve el mismo listado en CSV.")
    @GetMapping("/estudiantes/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(defaultValue = "xlsx") String formato) {
        String nombre = "progreso-estudiantes-" + LocalDate.now();
        if ("csv".equalsIgnoreCase(formato)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + ".csv\"")
                    .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                    .body(exportarService.csv().getBytes(StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportarService.excel());
    }

    @Operation(summary = "Detalle de un estudiante")
    @GetMapping("/estudiantes/{id}")
    public EstudianteDetalleResponse estudiante(@PathVariable Long id) {
        return estadisticasService.detalleEstudiante(id);
    }
}
