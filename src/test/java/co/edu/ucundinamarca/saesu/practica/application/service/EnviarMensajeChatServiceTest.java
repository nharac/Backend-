package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.request.EnviarMensajeRequest;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RespuestaChatResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarMensajeChatServiceTest {

    private static final String CORREO = "laura@ucundinamarca.edu.co";

    @Mock private ChatMensajeRepositoryPort chat;
    @Mock private PreguntaRepositoryPort preguntas;
    @Mock private UsuarioConsultaPort usuarios;
    @Mock private IaServicePort ia;

    private EnviarMensajeChatService service;

    @BeforeEach
    void setUp() {
        service = new EnviarMensajeChatService(chat, preguntas, usuarios, ia);
        ReflectionTestUtils.setField(service, "limiteDiario", 50);
        ReflectionTestUtils.setField(service, "historialMax", 20);
        lenient().when(usuarios.obtenerIdPorCorreo(CORREO)).thenReturn(1L);
        AtomicLong ids = new AtomicLong(100);
        lenient().when(chat.guardar(any(ChatMensaje.class))).thenAnswer(inv -> {
            ChatMensaje m = inv.getArgument(0);
            m.setId(ids.incrementAndGet());
            return m;
        });
    }

    @Test
    @DisplayName("Chat libre: guarda el mensaje y la respuesta de la IA y descuenta del límite diario")
    void chatLibre() {
        when(chat.contarMensajesHoy(eq(1L), any())).thenReturn(10L);
        when(chat.listarHistorialLibre(1L, 20)).thenReturn(List.of(
                new ChatMensaje(1L, 1L, null, RolEmisor.USUARIO, "Hola", LocalDateTime.now()),
                new ChatMensaje(2L, 1L, null, RolEmisor.IA, "¿En qué tema?", LocalDateTime.now())));
        when(ia.generarRespuesta(anyString(), anyString(), eq("¿Cómo resuelvo un límite 0/0?")))
                .thenReturn("¿Puedes factorizar el numerador?");

        RespuestaChatResponse r = service.enviarMensaje(new EnviarMensajeRequest(null, "¿Cómo resuelvo un límite 0/0?"), CORREO);

        assertThat(r.getMensajeUsuario().getRolEmisor()).isEqualTo("usuario");
        assertThat(r.getRespuestaIa().getRolEmisor()).isEqualTo("ia");
        assertThat(r.getRespuestaIa().getMensaje()).isEqualTo("¿Puedes factorizar el numerador?");
        assertThat(r.getMensajesRestantesHoy()).isEqualTo(39);
        verify(chat, times(2)).guardar(any(ChatMensaje.class));

        ArgumentCaptor<String> historial = ArgumentCaptor.forClass(String.class);
        verify(ia).generarRespuesta(anyString(), historial.capture(), anyString());
        assertThat(historial.getValue()).isEqualTo("Usuario: Hola\nTutor: ¿En qué tema?\n");
    }

    @Test
    @DisplayName("Las reglas del tutor prohíben dar la respuesta final y, con pregunta, incluyen su contexto")
    void promptConContexto() {
        Pregunta p = new Pregunta(7L, Tema.DERIVADAS, "Derive f(x) = x³", Dificultad.INTERMEDIO, "Usa la regla de la potencia", null, null);
        when(chat.contarMensajesHoy(eq(1L), any())).thenReturn(0L);
        when(preguntas.buscarPorId(7L)).thenReturn(Optional.of(p));
        when(chat.listarHistorialPorPregunta(1L, 7L, 20)).thenReturn(List.of());
        when(ia.generarRespuesta(anyString(), anyString(), anyString())).thenReturn("¿Qué dice la regla de la potencia?");

        service.enviarMensaje(new EnviarMensajeRequest(7L, "No sé cómo empezar"), CORREO);

        ArgumentCaptor<String> sistema = ArgumentCaptor.forClass(String.class);
        verify(ia).generarRespuesta(sistema.capture(), eq(""), eq("No sé cómo empezar"));
        assertThat(sistema.getValue())
                .contains("NUNCA des la respuesta final")
                .contains("- Tema: derivadas")
                .contains("- Enunciado: Derive f(x) = x³")
                .contains("- Pista disponible: Usa la regla de la potencia");
        verify(chat, never()).listarHistorialLibre(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Límite diario alcanzado: no llama a la IA ni guarda nada")
    void limiteDiario() {
        when(chat.contarMensajesHoy(eq(1L), any())).thenReturn(50L);

        assertThatThrownBy(() -> service.enviarMensaje(new EnviarMensajeRequest(null, "Hola"), CORREO))
                .isInstanceOf(LimiteChatExcedidoException.class)
                .hasMessageContaining("50 mensajes");
        verify(ia, never()).generarRespuesta(anyString(), anyString(), anyString());
        verify(chat, never()).guardar(any());
    }

    @Test
    @DisplayName("Pregunta de contexto inexistente: error antes de guardar")
    void preguntaInexistente() {
        when(chat.contarMensajesHoy(eq(1L), any())).thenReturn(0L);
        when(preguntas.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enviarMensaje(new EnviarMensajeRequest(99L, "Hola"), CORREO))
                .isInstanceOf(PreguntaNoEncontradaException.class);
        verify(chat, never()).guardar(any());
    }

    @Test
    @DisplayName("Si la IA falla, responde IaNoDisponibleException con un mensaje claro")
    void iaNoDisponible() {
        when(chat.contarMensajesHoy(eq(1L), any())).thenReturn(0L);
        when(chat.listarHistorialLibre(1L, 20)).thenReturn(List.of());
        when(ia.generarRespuesta(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("503"));

        assertThatThrownBy(() -> service.enviarMensaje(new EnviarMensajeRequest(null, "Hola"), CORREO))
                .isInstanceOf(IaNoDisponibleException.class)
                .hasMessageContaining("tutor IA");
    }
}
