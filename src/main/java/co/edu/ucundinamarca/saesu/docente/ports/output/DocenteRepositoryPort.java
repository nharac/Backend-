package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import java.util.List;

public interface DocenteRepositoryPort {
    List<Publicacion> listarSinRespuestaOficial();
    /** Marca la respuesta como oficial y guarda qué docente lo hizo y cuándo (SCRUM-91). */
    void marcarOficial(Long respuestaId, Long docenteId);
    void quitarOficial(Long respuestaId);
    void quitarOficialesDePublicacion(Long publicacionId);
}
