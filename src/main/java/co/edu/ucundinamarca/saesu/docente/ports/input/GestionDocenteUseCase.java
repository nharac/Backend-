package co.edu.ucundinamarca.saesu.docente.ports.input;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import java.util.List;

public interface GestionDocenteUseCase {
    List<PublicacionResumenResponse> listarPublicacionesSinOficial();
    void marcarRespuestaOficial(Long respuestaId, String correoDocente);
    void quitarRespuestaOficial(Long respuestaId);
}