package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import java.util.List;

public interface DocenteRepositoryPort {
    List<Publicacion> listarSinRespuestaOficial();
    void marcarOficial(Long respuestaId);
    void quitarOficial(Long respuestaId);
    void quitarOficialesDePublicacion(Long publicacionId);
}