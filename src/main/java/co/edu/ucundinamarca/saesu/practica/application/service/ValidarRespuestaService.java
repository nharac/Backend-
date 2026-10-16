package co.edu.ucundinamarca.saesu.practica.application.service;

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

        // 5. Registrar el intento
        RespuestaEstudiante intento = new RespuestaEstudiante(
                null,
                usuarioId,
                preguntaId,
                opcionId,
                esCorrecta,
                LocalDateTime.now()
        );
        respuestaRepository.guardar(intento);

        // 6. Construir respuesta según resultado
        if (esCorrecta) {
            return new ValidacionResponse(
                    true,
                    opcionId,
                    null,
                    "¡Correcto!",
                    true,
                    false
            );
        } else {
            return new ValidacionResponse(
                    false,
                    null,
                    pregunta.getPista(),
                    "Incorrecto",
                    false,
                    true
            );
        }
    }
}