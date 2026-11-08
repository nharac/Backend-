package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.MetaSemanalRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.MetaSemanalResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.MiAvanceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Adaptador de entrada: estadísticas personales que se muestran en el perfil del estudiante. */
@RestController
@RequestMapping("/api/practica")
@Tag(name = "Práctica", description = "Endpoints de preguntas tipo ICFES, validación, pistas y progreso")
public class MiAvanceController {

    private final MiAvanceUseCase miAvance;

    public MiAvanceController(MiAvanceUseCase miAvance) {
        this.miAvance = miAvance;
    }

    @Operation(summary = "Calendario de actividad",
            description = "Respuestas y aciertos por día (hora de Colombia) de los últimos N días, entre 7 y 371. Requiere JWT.")
    @GetMapping("/actividad")
    public ResponseEntity<ActividadResponse> actividad(@RequestParam(defaultValue = "126") int dias,
                                                       Authentication authentication) {
        return ResponseEntity.ok(miAvance.actividad(authentication.getName(), dias));
    }

    @Operation(summary = "Fortalezas y debilidades",
            description = "Porcentaje de acierto por tema y dificultad, las fortalezas (≥ 70 %), las debilidades (< 60 %) "
                    + "y qué practicar ahora. Requiere JWT.")
    @GetMapping("/analisis")
    public ResponseEntity<AnalisisResponse> analisis(Authentication authentication) {
        return ResponseEntity.ok(miAvance.analisis(authentication.getName()));
    }

    @Operation(summary = "Mi meta semanal", description = "Meta de preguntas de la semana (lunes a domingo) y el avance. Requiere JWT.")
    @GetMapping("/meta")
    public ResponseEntity<MetaSemanalResponse> meta(Authentication authentication) {
        return ResponseEntity.ok(miAvance.meta(authentication.getName()));
    }

    @Operation(summary = "Cambiar mi meta semanal", description = "Entre 5 y 300 preguntas por semana. Requiere JWT.")
    @PutMapping("/meta")
    public ResponseEntity<MetaSemanalResponse> cambiarMeta(@Valid @RequestBody MetaSemanalRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.ok(miAvance.cambiarMeta(authentication.getName(), request.meta()));
    }
}
