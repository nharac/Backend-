package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;
import java.util.List;

public interface DocenteRepositoryPort {
    /** Marca la respuesta como oficial y guarda qué docente lo hizo y cuándo (SCRUM-91). */
    void marcarOficial(Long respuestaId, Long docenteId);
    void quitarOficial(Long respuestaId);
    void quitarOficialesDePublicacion(Long publicacionId);

    // Moderación del foro (V14). Devuelven false si el id no existe.
    boolean ocultarRespuesta(Long respuestaId, Long docenteId);
    boolean restaurarRespuesta(Long respuestaId);
    boolean ocultarPublicacion(Long publicacionId, Long docenteId);
    boolean restaurarPublicacion(Long publicacionId);
    /** Borra la publicación de verdad (con sus respuestas, en cascada). */
    boolean eliminarPublicacion(Long publicacionId);
    /** Borra la respuesta de verdad (con sus respuestas hijas, en cascada). */
    boolean eliminarRespuesta(Long respuestaId);
    List<ContenidoOcultoResponse> listarContenidoOculto();
}
