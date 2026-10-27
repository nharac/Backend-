package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import java.util.List;

public interface DocenteRepositoryPort {
    List<Publicacion> listarSinRespuestaOficial();
    /** Marca la respuesta como oficial y guarda qué docente lo hizo y cuándo (SCRUM-91). */
    void marcarOficial(Long respuestaId, Long docenteId);
    void quitarOficial(Long respuestaId);
    void quitarOficialesDePublicacion(Long publicacionId);

    // Moderación del foro (V14). Devuelven false si el id no existe.
    boolean ocultarRespuesta(Long respuestaId, Long docenteId);
    boolean restaurarRespuesta(Long respuestaId);
    boolean ocultarPublicacion(Long publicacionId, Long docenteId);
    boolean restaurarPublicacion(Long publicacionId);
    List<ContenidoOcultoResponse> listarContenidoOculto();
}
