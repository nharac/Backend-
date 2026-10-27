package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;

import java.util.List;
import java.util.Optional;

/** Persistencia del banco de preguntas administrado por el docente. */
public interface BancoPreguntasPort {

    /**
     * Lista preguntas con filtros opcionales (null = sin filtro).
     * @param activa true = solo activas, false = solo inactivas, null = todas
     */
    List<PreguntaDocenteResponse> listar(String tema, String dificultad, String busqueda, Boolean activa);

    Optional<PreguntaDocenteResponse> buscarPorId(Long id);

    /** ¿Existe otra pregunta con el mismo enunciado? (sin contar la de id excluirId). */
    boolean existeEnunciado(String enunciado, Long excluirId);

    Long crear(PreguntaDocenteRequest request, Long docenteId);

    void actualizar(Long id, PreguntaDocenteRequest request);

    /** @return false si la pregunta no existe */
    boolean cambiarEstado(Long id, boolean activa);
}
