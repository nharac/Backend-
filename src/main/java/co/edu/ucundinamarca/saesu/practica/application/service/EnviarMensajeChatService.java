package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.EnviarMensajeRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ChatMensajeResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RespuestaChatResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.ports.input.EnviarMensajeChatUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Caso de uso: enviar un mensaje al chat con IA.
 *
 * Flujo:
 * 1. Validar que el usuario no excedió su límite diario.
 * 2. Si hay preguntaId, validar que la pregunta existe y cargar su contexto.
 * 3. Guardar el mensaje del usuario en la BD.
 * 4. Cargar el historial (últimos N mensajes).
 * 5. Construir el prompt para la IA (system + historial + mensaje nuevo).
 * 6. Llamar a la IA.
 * 7. Guardar la respuesta de la IA.
 * 8. Devolver ambos mensajes al frontend.
 */
@Service
@RequiredArgsConstructor
public class EnviarMensajeChatService implements EnviarMensajeChatUseCase {

    private final ChatMensajeRepositoryPort chatRepository;
    private final PreguntaRepositoryPort preguntaRepository;
    private final UsuarioConsultaPort usuarioConsultaPort;
    private final IaServicePort iaServicePort;

    @Value("${chat.limite.diario:50}")
    private int limiteDiario;

    @Value("${chat.historial.max:20}")
    private int historialMax;

    @Override
    @Transactional
    public RespuestaChatResponse enviarMensaje(EnviarMensajeRequest request, String correoEstudiante) {
        // 1. Obtener ID del usuario
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(correoEstudiante);

        // 2. Validar límite diario
        LocalDateTime inicioDelDia = LocalDate.now().atStartOfDay();
        long mensajesHoy = chatRepository.contarMensajesHoy(usuarioId, inicioDelDia);

        if (mensajesHoy >= limiteDiario) {
            throw new LimiteChatExcedidoException(
                    "Has alcanzado el límite diario de " + limiteDiario
                            + " mensajes. Vuelve mañana."
            );
        }

        // 3. Validar pregunta (si aplica) y cargar contexto
        Pregunta pregunta = null;
        if (request.getPreguntaId() != null) {
            pregunta = preguntaRepository.buscarPorId(request.getPreguntaId())
                    .orElseThrow(() -> new PreguntaNoEncontradaException(
                            "Pregunta no encontrada con id: " + request.getPreguntaId()
                    ));
        }

        // 4. Guardar mensaje del usuario
        ChatMensaje mensajeUsuario = new ChatMensaje(
                null,
                usuarioId,
                request.getPreguntaId(),
                RolEmisor.USUARIO,
                request.getMensaje(),
                LocalDateTime.now()
        );
        ChatMensaje mensajeUsuarioGuardado = chatRepository.guardar(mensajeUsuario);

        // 5. Cargar historial
        List<ChatMensaje> historial = (pregunta == null)
                ? chatRepository.listarHistorialLibre(usuarioId, historialMax)
                : chatRepository.listarHistorialPorPregunta(usuarioId, pregunta.getId(), historialMax);

        // 6. Construir prompts
        String systemPrompt = construirSystemPrompt(pregunta);
        String historialFormateado = formatearHistorial(historial);

        // 7. Llamar a la IA
        String respuestaTexto;
        try {
            respuestaTexto = iaServicePort.generarRespuesta(
                    systemPrompt,
                    historialFormateado,
                    request.getMensaje()
            );
        } catch (Exception e) {
            throw new IaNoDisponibleException(
                    "No se pudo contactar al tutor IA. Intenta de nuevo en unos segundos.",
                    e
            );
        }

        // 8. Guardar respuesta de la IA
        ChatMensaje respuesta = new ChatMensaje(
                null,
                usuarioId,
                request.getPreguntaId(),
                RolEmisor.IA,
                respuestaTexto,
                LocalDateTime.now()
        );
        ChatMensaje respuestaGuardada = chatRepository.guardar(respuesta);

        // 9. Calcular mensajes restantes
        long mensajesRestantes = Math.max(0, limiteDiario - (mensajesHoy + 1));

        // 10. Convertir a DTOs y devolver
        return new RespuestaChatResponse(
                convertirADto(mensajeUsuarioGuardado),
                convertirADto(respuestaGuardada),
                mensajesRestantes
        );
    }

    /**
     * Construye el system prompt de la IA.
     * Si hay pregunta, incluye el contexto (enunciado, tema, pista).
     */
    private String construirSystemPrompt(Pregunta pregunta) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un tutor de matemáticas para estudiantes de Ingeniería ");
        prompt.append("de Sistemas de la Universidad de Cundinamarca. ");
        prompt.append("Ayudas con temas de límites, derivadas e integrales.\n\n");
        prompt.append("REGLAS ESTRICTAS:\n");
        prompt.append("1. NUNCA des la respuesta final al problema.\n");
        prompt.append("2. Siempre responde con preguntas que guíen al estudiante.\n");
        prompt.append("3. Da pistas progresivas: de lo más general a lo más específico.\n");
        prompt.append("4. Si el estudiante insiste en la respuesta, responde: ");
        prompt.append("\"Revisa el método de solución en tus apuntes y vuelve a intentarlo.\"\n");
        prompt.append("5. Usa un tono amable y motivador.\n");
        prompt.append("6. Máximo 4 frases por respuesta.\n");

        if (pregunta != null) {
            prompt.append("\nCONTEXTO DE LA PREGUNTA ACTUAL:\n");
            prompt.append("- Tema: ").append(pregunta.getTema().getValor()).append("\n");
            prompt.append("- Dificultad: ").append(pregunta.getDificultad().getValor()).append("\n");
            prompt.append("- Enunciado: ").append(pregunta.getEnunciado()).append("\n");
            if (pregunta.getPista() != null) {
                prompt.append("- Pista disponible: ").append(pregunta.getPista()).append("\n");
            }
            prompt.append("\nEl estudiante está resolviendo esta pregunta. ");
            prompt.append("Ayúdalo a razonar sin darle la respuesta.\n");
        }

        return prompt.toString();
    }

    /**
     * Convierte una lista de mensajes a un texto con formato
     * "Usuario: ...\nTutor: ...\n" para alimentar a la IA.
     */
    private String formatearHistorial(List<ChatMensaje> historial) {
        if (historial == null || historial.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ChatMensaje m : historial) {
            if (m.getRolEmisor() == RolEmisor.USUARIO) {
                sb.append("Usuario: ").append(m.getMensaje()).append("\n");
            } else {
                sb.append("Tutor: ").append(m.getMensaje()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Convierte un ChatMensaje de dominio a ChatMensajeResponse (DTO).
     */
    private ChatMensajeResponse convertirADto(ChatMensaje mensaje) {
        return new ChatMensajeResponse(
                mensaje.getId(),
                mensaje.getRolEmisor().getValor(),
                mensaje.getMensaje(),
                mensaje.getPreguntaId(),
                mensaje.getFechaHora()
        );
    }
}
