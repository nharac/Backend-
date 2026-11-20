package co.edu.ucundinamarca.saesu.notificacion;

import co.edu.ucundinamarca.saesu.notificacion.application.dto.NotificacionResponse;
import co.edu.ucundinamarca.saesu.notificacion.application.service.NotificacionesService;
import co.edu.ucundinamarca.saesu.notificacion.ports.output.NotificacionPort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionesServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 10, 0);

    @Mock private NotificacionPort port;
    private NotificacionesService servicio;

    @BeforeEach
    void preparar() {
        servicio = new NotificacionesService(port, Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA));
    }

    @Test
    void notificarRecortaTextosLargosYNoFallaSiLaBdFalla() {
        String largo = "x".repeat(400);
        servicio.notificar(1L, "RETO", "Te retaron", largo, "/retos");
        verify(port).crear(eq(1L), eq("RETO"), eq("Te retaron"), any(String.class), eq("/retos"), eq(AHORA));

        doThrow(new RuntimeException("caída")).when(port).crearParaEstudiantes(anyString(), anyString(), any(), any(), any());
        assertThatCode(() -> servicio.notificarEstudiantes("TAREA", "Nueva tarea", null, "/dashboard")).doesNotThrowAnyException();
        servicio.notificar(null, "RETO", "x", null, null); // sin usuario no hace nada
    }

    @Test
    void bandejaYMarcarLeidas() {
        when(port.idPorCorreo("a@u.edu.co")).thenReturn(Optional.of(5L));
        when(port.noLeidas(5L)).thenReturn(2L);
        when(port.ultimas(5L, 30)).thenReturn(List.of(new NotificacionResponse(1L, "RETO", "t", null, null, false, AHORA)));
        assertThat(servicio.bandeja("a@u.edu.co").noLeidas()).isEqualTo(2);
        when(port.marcarTodas(5L)).thenReturn(2);
        assertThat(servicio.marcarTodas("a@u.edu.co")).isEqualTo(2);
        when(port.idPorCorreo("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.noLeidas("x")).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
