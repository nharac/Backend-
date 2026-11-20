package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistencia de las salas de retos en vivo. */
public interface VivoPort {

    record Sala(Long id, String codigo, Long anfitrionId, String anfitrion, Long invitadoId, String invitado, String tema,
                String estado, int segundosPorPregunta, LocalDateTime iniciaEn, int total) {}

    record Respuesta(Long usuarioId, int orden, String letra, boolean correcta, int puntos) {}

    Optional<Long> idPorCorreo(String correo);

    boolean esEstudiante(Long usuarioId);

    boolean codigoExiste(String codigo);

    Long crear(String codigo, Long anfitrionId, Long invitadoId, String tema, int segundosPorPregunta,
               List<Long> preguntaIds, LocalDateTime creadaEn);

    Optional<Sala> buscar(String codigo);

    /** Une al invitado y arranca el juego; false si la sala ya no está esperando o es para otra persona. */
    boolean unir(Long salaId, Long invitadoId, LocalDateTime iniciaEn);

    void cambiarEstado(Long salaId, String estado);

    List<PreguntaQuiz> preguntas(Long salaId);

    boolean responder(Long salaId, Long usuarioId, int orden, String letra, boolean correcta, int puntos, LocalDateTime cuando);

    List<Respuesta> respuestas(Long salaId);
}
