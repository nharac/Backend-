package co.edu.ucundinamarca.saesu.tarea.ports.output;

import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistencia de las tareas y del avance de cada estudiante. */
public interface TareaPort {

    /** Tarea con el conteo de asignados y de quienes ya la completaron. */
    record FilaTareaDocente(Long id, String titulo, String instrucciones, String tema, int metaPreguntas,
                            LocalDate fechaLimite, Long estudianteId, String estudiante, long asignados,
                            long completadas, LocalDateTime creadaEn) {}

    /** Tarea de un estudiante con las preguntas que ya resolvió para ella. */
    record FilaTareaEstudiante(Long id, String titulo, String instrucciones, String tema, int metaPreguntas,
                               LocalDate fechaLimite, String docente, long resueltas) {}

    Optional<Long> idPorCorreo(String correo);

    boolean esEstudiante(Long usuarioId);

    Long crear(TareaRequest tarea, Long docenteId, LocalDateTime creadaEn);

    List<FilaTareaDocente> delDocente(Long docenteId);

    /** Borra la tarea solo si es de ese docente. */
    boolean eliminar(Long tareaId, Long docenteId);

    List<FilaTareaEstudiante> delEstudiante(Long estudianteId);
}
