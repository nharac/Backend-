package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ExplicacionResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.ports.input.ExplicarEjercicioUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Calculadora paso a paso: el estudiante escribe su ejercicio y el tutor lo resuelve completo,
 * explicando el porqué de cada paso en lenguaje sencillo. A diferencia del chat, aquí sí se da
 * la respuesta final, porque el objetivo es aprender el procedimiento.
 * Cuenta dentro del límite diario de mensajes del chat y queda en el historial.
 */
@Service
@RequiredArgsConstructor
public class ExplicarEjercicioService implements ExplicarEjercicioUseCase {

    /** Prefijo con el que el ejercicio queda en el historial del chat. */
    static final String PREFIJO = "Explícame paso a paso: ";

    static final String PROMPT = "Eres Sumi, el tutor de matemáticas de SAESU. Tu tarea es RESOLVER COMPLETO "
            + "el ejercicio que te envía el estudiante y explicarle cada paso.\n\n"
            + EnviarMensajeChatService.REGLAS_DE_TEMA
            + EnviarMensajeChatService.REGLAS_DE_LENGUAJE
            + """
            FORMATO (OBLIGATORIO):
            - Escribe cada paso así: "Paso N: <lo que se hace>" y en la línea siguiente "Por qué: <explicación sencilla>".
            - Usa entre 2 y 8 pasos; no te saltes pasos de álgebra.
            - Al final escribe "Respuesta: <resultado>" y "Idea clave: <qué recordar para ejercicios parecidos>".
            - Si el ejercicio está incompleto o es ambiguo, pide en una frase el dato que falta en lugar de inventarlo.
            """;

    private final ChatMensajeRepositoryPort chatRepository;
    private final UsuarioConsultaPort usuarioConsultaPort;
    private final IaServicePort iaServicePort;

    @Value("${chat.limite.diario:50}")
    private int limiteDiario = 50;

    @Override
    public ExplicacionResponse explicar(String ejercicio, String correoEstudiante) {
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(correoEstudiante);
        long mensajesHoy = chatRepository.contarMensajesHoy(usuarioId, LocalDate.now().atStartOfDay());
        if (mensajesHoy >= limiteDiario) {
            throw new LimiteChatExcedidoException(
                    "Has alcanzado el límite diario de " + limiteDiario + " mensajes. Vuelve mañana.");
        }

        String texto = ejercicio.trim();
        chatRepository.guardar(new ChatMensaje(null, usuarioId, null, RolEmisor.USUARIO, PREFIJO + texto, LocalDateTime.now()));

        String explicacion;
        try {
            explicacion = iaServicePort.generarRespuesta(PROMPT, "", texto);
        } catch (IaNoDisponibleException e) {
            throw e;
        } catch (Exception e) {
            throw new IaNoDisponibleException("No se pudo contactar al tutor IA. Intenta de nuevo en unos segundos.", e);
        }

        chatRepository.guardar(new ChatMensaje(null, usuarioId, null, RolEmisor.IA, explicacion, LocalDateTime.now()));
        return new ExplicacionResponse(explicacion, Math.max(0, limiteDiario - (mensajesHoy + 1)));
    }
}
