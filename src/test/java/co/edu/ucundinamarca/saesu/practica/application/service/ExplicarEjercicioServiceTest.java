package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ExplicacionResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.LimiteChatExcedidoException;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExplicarEjercicioServiceTest {

    private static final String CORREO = "ana@ucundinamarca.edu.co";

    @Mock private ChatMensajeRepositoryPort chat;
    @Mock private UsuarioConsultaPort usuarios;
    @Mock private IaServicePort ia;
    @InjectMocks private ExplicarEjercicioService servicio;

    @Test
    @DisplayName("Resuelve el ejercicio con el prompt paso a paso y guarda la pregunta y la respuesta")
    void explica() {
        when(usuarios.obtenerIdPorCorreo(CORREO)).thenReturn(4L);
        when(chat.contarMensajesHoy(eq(4L), any())).thenReturn(10L);
        when(ia.generarRespuesta(anyString(), eq(""), eq("2x + 3 = 7"))).thenReturn("Paso 1: …\nRespuesta: x = 2");

        ExplicacionResponse r = servicio.explicar("  2x + 3 = 7 ", CORREO);

        assertThat(r.explicacion()).contains("Respuesta: x = 2");
        assertThat(r.mensajesRestantes()).isEqualTo(39);
        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(ia).generarRespuesta(prompt.capture(), eq(""), eq("2x + 3 = 7"));
        assertThat(prompt.getValue()).contains("RESOLVER COMPLETO", "Por qué:", "Solo respondes preguntas de matemáticas",
                "español sencillo");
        ArgumentCaptor<ChatMensaje> guardados = ArgumentCaptor.forClass(ChatMensaje.class);
        verify(chat, times(2)).guardar(guardados.capture());
        assertThat(guardados.getAllValues().get(0).getRolEmisor()).isEqualTo(RolEmisor.USUARIO);
        assertThat(guardados.getAllValues().get(0).getMensaje()).isEqualTo("Explícame paso a paso: 2x + 3 = 7");
        assertThat(guardados.getAllValues().get(1).getRolEmisor()).isEqualTo(RolEmisor.IA);
    }

    @Test
    @DisplayName("Respeta el límite diario del chat")
    void limite() {
        when(usuarios.obtenerIdPorCorreo(CORREO)).thenReturn(4L);
        when(chat.contarMensajesHoy(eq(4L), any())).thenReturn(50L);

        assertThatThrownBy(() -> servicio.explicar("x² = 4", CORREO)).isInstanceOf(LimiteChatExcedidoException.class);
        verify(ia, never()).generarRespuesta(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Si la IA falla devuelve un error claro y no guarda respuesta")
    void iaCaida() {
        when(usuarios.obtenerIdPorCorreo(CORREO)).thenReturn(4L);
        when(chat.contarMensajesHoy(eq(4L), any())).thenReturn(0L);
        when(ia.generarRespuesta(anyString(), anyString(), anyString())).thenThrow(new IllegalStateException("red"));

        assertThatThrownBy(() -> servicio.explicar("x² = 4", CORREO)).isInstanceOf(IaNoDisponibleException.class);
        verify(chat, times(1)).guardar(any());

        doThrow(new IaNoDisponibleException("saturado")).when(ia).generarRespuesta(anyString(), anyString(), anyString());
        assertThatThrownBy(() -> servicio.explicar("x² = 4", CORREO)).hasMessage("saturado");
    }
}
