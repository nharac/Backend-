package co.edu.ucundinamarca.saesu.duelo.ports.output;

import co.edu.ucundinamarca.saesu.duelo.application.dto.response.CompaneroResponse;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DueloPort {

    /** Un reto con las entregas de los dos (null si aún no responde). */
    record FilaDuelo(Long id, String tema, String modo, int puntajeMaximo, Long retadorId, String retador, Long rivalId, String rival,
                     LocalDateTime creadoEn, int totalPreguntas,
                     Integer correctasRetador, Integer segundosRetador,
                     Integer correctasRival, Integer segundosRival) {}

    Optional<Long> idPorCorreo(String correo);

    boolean esEstudiante(Long usuarioId);

    List<CompaneroResponse> companeros(Long usuarioId);

    Long crear(Long retadorId, Long rivalId, String tema, String modo, int puntajeMaximo, List<Long> preguntaIds, LocalDateTime creadoEn);

    List<FilaDuelo> duelosDe(Long usuarioId, int limite);

    /** El reto si el usuario participa en él. */
    Optional<FilaDuelo> buscar(Long dueloId, Long usuarioId);

    List<PreguntaQuiz> preguntas(Long dueloId);

    /** Guarda la entrega; false si ya había entregado. */
    boolean entregar(Long dueloId, Long usuarioId, int correctas, int segundos, LocalDateTime entregadoEn);
}
