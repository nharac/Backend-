package co.edu.ucundinamarca.saesu.tarea.application.service;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;
import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaDocenteResponse;
import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaEstudianteResponse;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaDocente;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaEstudiante;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

/**
 * Tareas que el docente asigna (a un estudiante o a todo el grupo).
 * Una tarea se completa al resolver bien {@code metaPreguntas} preguntas distintas del tema
 * después de que se creó; no hay que "entregar" nada.
 */
@Service
public class TareasService {

    private final TareaPort port;
    private final Clock clock;

    public TareasService(TareaPort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    private Long idDe(String correo) {
        return port.idPorCorreo(correo).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    public TareaDocenteResponse crear(TareaRequest tarea, String correoDocente) {
        if (tarea.estudianteId() != null && !port.esEstudiante(tarea.estudianteId())) {
            throw new IllegalArgumentException("El estudiante elegido no existe");
        }
        Long docenteId = idDe(correoDocente);
        // Misma hora que usa la práctica al guardar cada intento, para comparar las fechas sin desfase.
        Long id = port.crear(tarea, docenteId, LocalDateTime.now());
        return port.delDocente(docenteId).stream().filter(t -> t.id().equals(id)).findFirst()
                .map(this::aRespuesta)
                .orElseThrow(() -> new IllegalStateException("No se encontró la tarea recién creada"));
    }

    public List<TareaDocenteResponse> delDocente(String correoDocente) {
        return port.delDocente(idDe(correoDocente)).stream().map(this::aRespuesta).toList();
    }

    public boolean eliminar(Long tareaId, String correoDocente) {
        return port.eliminar(tareaId, idDe(correoDocente));
    }

    public List<TareaEstudianteResponse> misTareas(String correoEstudiante) {
        LocalDate hoy = LocalDate.now(clock);
        return port.delEstudiante(idDe(correoEstudiante)).stream()
                .map(t -> aRespuesta(t, hoy))
                .sorted(Comparator.comparingInt(TareasService::orden).thenComparing(TareaEstudianteResponse::fechaLimite))
                .toList();
    }

    /** Primero las pendientes, luego las vencidas y al final las completadas. */
    private static int orden(TareaEstudianteResponse t) {
        if (t.completada()) return 2;
        return t.vencida() ? 1 : 0;
    }

    private TareaDocenteResponse aRespuesta(FilaTareaDocente t) {
        boolean vencida = t.fechaLimite().isBefore(LocalDate.now(clock));
        return new TareaDocenteResponse(t.id(), t.titulo(), t.instrucciones(), t.tema(), Tema.nombreDe(t.tema()),
                t.metaPreguntas(), t.fechaLimite(), t.estudianteId(),
                t.estudianteId() == null ? "Todo el grupo" : t.estudiante(),
                t.asignados(), t.completadas(), vencida, t.creadaEn());
    }

    private static TareaEstudianteResponse aRespuesta(FilaTareaEstudiante t, LocalDate hoy) {
        boolean completada = t.resueltas() >= t.metaPreguntas();
        return new TareaEstudianteResponse(t.id(), t.titulo(), t.instrucciones(), t.tema(), Tema.nombreDe(t.tema()),
                t.metaPreguntas(), t.fechaLimite(), t.docente(), Math.min(t.resueltas(), t.metaPreguntas()),
                completada, !completada && t.fechaLimite().isBefore(hoy), ChronoUnit.DAYS.between(hoy, t.fechaLimite()));
    }
}
