package co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MiPosicionResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.RankingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Adaptador de entrada: solo traduce HTTP al puerto RankingUseCase (no conoce JPA ni SQL). */
@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Gamificación", description = "Ranking de estudiantes")
public class RankingController {

    private final RankingUseCase ranking;

    public RankingController(RankingUseCase ranking) {
        this.ranking = ranking;
    }

    @Operation(summary = "Top 10 del ranking",
            description = "Los 10 estudiantes con más puntos en el periodo (total, semana o mes). La racha es la vigente: 0 si no practicó ni hoy ni ayer. Requiere JWT.")
    @GetMapping
    public List<RankingResponse> obtenerRanking(
            @Parameter(description = "total (por defecto), semana o mes")
            @RequestParam(required = false) String periodo) {
        return ranking.top(periodo);
    }

    @Operation(summary = "Mi posición en el ranking",
            description = "Puesto del estudiante que consulta aunque no esté en el top 10, puntos que le faltan para subir, racha, puntos de hoy y de la semana y sus últimos movimientos de puntos. Requiere JWT.")
    @GetMapping("/yo")
    public MiPosicionResponse miPosicion(
            Authentication authentication,
            @Parameter(description = "total (por defecto), semana o mes")
            @RequestParam(required = false) String periodo) {
        return ranking.miPosicion(authentication.getName(), periodo);
    }
}
