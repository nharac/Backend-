package co.edu.ucundinamarca.saesu.gamificacion.ports.input;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MiPosicionResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;

import java.util.List;

/** Puerto de entrada del ranking: lo usa RankingController sin conocer la implementación. */
public interface RankingUseCase {

    /** Top 10 del periodo: "total" (o vacío), "semana" o "mes". */
    List<RankingResponse> top(String periodo);

    /** Posición del estudiante autenticado (identificado por su correo del JWT). */
    MiPosicionResponse miPosicion(String correo, String periodo);
}
