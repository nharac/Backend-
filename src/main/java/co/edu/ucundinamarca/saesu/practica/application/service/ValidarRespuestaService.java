package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.service.LogrosService;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService; // <-- NUEVO IMPORT
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ValidacionResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.OpcionInvalidaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;
import co.edu.ucundinamarca.saesu.practica.ports.input.ValidarRespuestaUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RespuestaEstudianteRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso: validar la respuesta seleccionada.
 * Registra el intento del estudiante y devuelve el resultado.
 */
@Service
@RequiredArgsConstructor
public class ValidarRespuestaService implements ValidarRespuestaUseCase {

    private final PreguntaRepositoryPort preguntaRepository;
    private final OpcionRepositoryPort opcionRepository;
    private final RespuestaEstudianteRepositoryPort respuestaRepository;
    private final UsuarioConsultaPort usuarioConsultaPort;
    private final PuntosService puntosService; // <-- NUEVO
    private final LogrosService logrosService;

    /** Puntaje de un acierto: empieza en 20 y baja 5 por cada fallo, mínimo 5. */
    static final int PUNTOS_MAXIMOS = 20;
    static final int PENALIZACION_POR_FALLO = 5;
    static final int PUNTOS_MINIMOS = 5;

    static int puntosSegunFallos(long fallos) {
        return (int) Math.max(PUNTOS_MAXIMOS - PENALIZACION_POR_FALLO * fallos, PUNTOS_MINIMOS);
    }

    @Override
    @Transactional
    public ValidacionResponse validar(Long preguntaId, Long opcionId, String correoEstudiante) {
        // 1. Validar que la pregunta existe
        Pregunta pregunta = preguntaRepository.buscarPorId(preguntaId)
                .orElseThrow(() -> new PreguntaNoEncontradaException(
                        "Pregunta no encontrada con id: " + preguntaId));

        // 2. Validar que la opción existe y pertenece a la pregunta
        Opcion opcionSeleccionada = opcionRepository.buscarPorId(opcionId)
                .orElseThrow(() -> new OpcionInvalidaException(
                        "Opción no encontrada con id: " + opcionId));

        if (!opcionSeleccionada.getPreguntaId().equals(preguntaId)) {
            throw new OpcionInvalidaException(
                    "La opción no pertenece a la pregunta indicada");
        }

        // 3. Determinar si es correcta
        boolean esCorrecta = Boolean.TRUE.equals(opcionSeleccionada.getEsCorrecta());

        // 4. Obtener el ID del usuario mediante el puerto propio
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(correoEstudiante);

        // 5. Historial de esta pregunta ANTES de registrar el intento actual
        List<RespuestaEstudiante> previos = respuestaRepository.listarPorUsuario(usuarioId).stream()
                .filter(r -> preguntaId.equals(r.getPreguntaId()))
                .toList();
        boolean yaResuelta = previos.stream().anyMatch(r -> Boolean.TRUE.equals(r.getEsCorrecta()));
        long fallosPrevios = previos.stream().filter(r -> !Boolean.TRUE.equals(r.getEsCorrecta())).count();

        // 6. Registrar el intento
        RespuestaEstudiante intento = new RespuestaEstudiante(
                null,
                usuarioId,
                preguntaId,
                opcionId,
                esCorrecta,
                LocalDateTime.now()
        );
        respuestaRepository.guardar(intento);

        // 7. Puntos: solo la primera vez que acierta la pregunta,
        //    y valen menos por cada fallo anterior (20, 15, 10, 5...)
        ValidacionResponse respuesta;
        if (esCorrecta) {
            if (yaResuelta) {
                // Sin puntos, pero el día sí cuenta para la racha
                puntosService.registrarActividad(usuarioId);
                respuesta = new ValidacionResponse(
                        true,
                        opcionId,
                        null,
                        "¡Correcto! Ya habías resuelto esta pregunta (sin puntos extra)",
                        true,
                        false,
                        0,
                        0
                );
            } else {
                int puntos = puntosSegunFallos(fallosPrevios);
                puntosService.otorgarPuntos(usuarioId, puntos, "Acierto en práctica: " + pregunta.getTema().getValor());
                respuesta = new ValidacionResponse(
                        true,
                        opcionId,
                        null,
                        "¡Correcto! (+" + puntos + " puntos)",
                        true,
                        false,
                        puntos,
                        0
                );
            }
            // 8. ¿Desbloqueó algún logro con este acierto?
            respuesta.setNuevosLogros(logrosService.evaluar(usuarioId));
        } else {
            respuesta = new ValidacionResponse(
                    false,
                    null,
                    pregunta.getPista(),
                    "Incorrecto",
                    false,
                    true,
                    0,
                    yaResuelta ? 0 : puntosSegunFallos(fallosPrevios + 1)
            );
        }
        return respuesta;
    }
}