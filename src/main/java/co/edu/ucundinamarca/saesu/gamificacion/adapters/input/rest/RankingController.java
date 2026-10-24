package co.edu.ucundinamarca.saesu.gamificacion.adapters.input.rest;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.repository.PuntoHistorialJpaRepository;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Gamificación", description = "Ranking de estudiantes")
public class RankingController {

    private final PuntoHistorialJpaRepository puntosRepository;

    public RankingController(PuntoHistorialJpaRepository puntosRepository) {
        this.puntosRepository = puntosRepository;
    }

    @Operation(summary = "Obtener Top 10 del Ranking", description = "Devuelve los 10 estudiantes con más puntos (Requiere JWT).")
    @GetMapping
    public List<RankingResponse> obtenerRanking() {
        // Pedimos solo los primeros 10 a la base de datos (Optimizado para miles de usuarios)
        Pageable top10 = PageRequest.of(0, 10);
        List<Object[]> resultados = puntosRepository.obtenerTopUsuarios(top10);

        return resultados.stream().map(row -> {
            UsuarioEntity usuario = (UsuarioEntity) row[0];
            Long totalPuntos = (Long) row[1]; // Hibernate devuelve Long en los SUM
            
            return new RankingResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getFotoPerfilUrl(),
                    totalPuntos,
                    usuario.getRachaActual()
            );
        }).toList();
    }
}