package co.edu.ucundinamarca.saesu.practica.adapters.input.rest;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.EnviarMensajeRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.HistorialChatResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RespuestaChatResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.EnviarMensajeChatUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Chat IA", description = "Endpoints del chat con el tutor IA (Gemini)")
public class ChatController {

    private final EnviarMensajeChatUseCase enviarMensajeChatUseCase;
    private final ChatMensajeRepositoryPort chatMensajeRepository;
    private final UsuarioConsultaPort usuarioConsultaPort;

    @Value("${chat.historial.max:20}")
    private int historialMax;

    public ChatController(EnviarMensajeChatUseCase enviarMensajeChatUseCase,
                          ChatMensajeRepositoryPort chatMensajeRepository,
                          UsuarioConsultaPort usuarioConsultaPort) {
        this.enviarMensajeChatUseCase = enviarMensajeChatUseCase;
        this.chatMensajeRepository = chatMensajeRepository;
        this.usuarioConsultaPort = usuarioConsultaPort;
    }

    @Operation(
            summary = "Enviar mensaje al chat IA",
            description = "Envía un mensaje al tutor IA. Si preguntaId es null, es chat libre. " +
                    "Si preguntaId tiene valor, es chat contextual. Requiere JWT."
    )
    @PostMapping("/mensaje")
    public ResponseEntity<RespuestaChatResponse> enviarMensaje(
            @Valid @RequestBody EnviarMensajeRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                enviarMensajeChatUseCase.enviarMensaje(request, authentication.getName())
        );
    }

    @Operation(
            summary = "Ver historial del chat libre",
            description = "Devuelve los últimos N mensajes del chat libre del usuario autenticado. Requiere JWT."
    )
    @GetMapping("/historial")
    public ResponseEntity<HistorialChatResponse> verHistorialLibre(Authentication authentication) {
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(authentication.getName());

        var mensajes = chatMensajeRepository.listarHistorialLibre(usuarioId, historialMax);

        var mensajesDto = mensajes.stream()
                .map(m -> new co.edu.ucundinamarca.saesu.practica.application.dto.response.ChatMensajeResponse(
                        m.getId(),
                        m.getRolEmisor().getValor(),
                        m.getMensaje(),
                        m.getPreguntaId(),
                        m.getFechaHora()
                ))
                .toList();

        return ResponseEntity.ok(new HistorialChatResponse(null, mensajesDto));
    }

    @Operation(
            summary = "Ver historial del chat de una pregunta",
            description = "Devuelve los últimos N mensajes del chat contextual a una pregunta específica. Requiere JWT."
    )
    @GetMapping("/historial/pregunta/{preguntaId}")
    public ResponseEntity<HistorialChatResponse> verHistorialPorPregunta(
            @PathVariable Long preguntaId,
            Authentication authentication
    ) {
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(authentication.getName());

        var mensajes = chatMensajeRepository.listarHistorialPorPregunta(usuarioId, preguntaId, historialMax);

        var mensajesDto = mensajes.stream()
                .map(m -> new co.edu.ucundinamarca.saesu.practica.application.dto.response.ChatMensajeResponse(
                        m.getId(),
                        m.getRolEmisor().getValor(),
                        m.getMensaje(),
                        m.getPreguntaId(),
                        m.getFechaHora()
                ))
                .toList();

        return ResponseEntity.ok(new HistorialChatResponse(preguntaId, mensajesDto));
    }

    @Operation(
            summary = "Limpiar historial del chat libre",
            description = "Elimina todos los mensajes del chat libre del usuario autenticado. Requiere JWT."
    )
    @DeleteMapping("/historial")
    public ResponseEntity<Void> limpiarHistorialLibre(Authentication authentication) {
        Long usuarioId = usuarioConsultaPort.obtenerIdPorCorreo(authentication.getName());
        chatMensajeRepository.eliminarHistorialLibre(usuarioId);
        return ResponseEntity.noContent().build();
    }
}