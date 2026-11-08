package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.UtilResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.ports.output.UtilesPort;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UtilesServiceTest {

    private final UtilesPort port = mock(UtilesPort.class);
    private final UtilesService servicio = new UtilesService(port);

    @Test
    void resumenDeLaPublicacion() {
        when(port.idUsuario("ana@u.edu.co")).thenReturn(4L);
        when(port.contarPorPublicacion(1L)).thenReturn(Map.of(7L, 2L));
        when(port.marcadasPor(1L, 4L)).thenReturn(Set.of(9L, 7L));

        var r = servicio.dePublicacion(1L, "ana@u.edu.co");
        assertThat(r.conteos()).containsEntry(7L, 2L);
        assertThat(r.mias()).containsExactly(7L, 9L);

        assertThat(servicio.dePublicacion(1L, "nadie@u.edu.co").mias()).isEmpty();
    }

    @Test
    void marcarYQuitar() {
        when(port.idUsuario("ana@u.edu.co")).thenReturn(4L);
        when(port.respuestaVisible(7L)).thenReturn(true);
        when(port.marcar(7L, 4L)).thenReturn(true);
        when(port.contar(7L)).thenReturn(3L);

        assertThat(servicio.alternar(7L, "ana@u.edu.co")).isEqualTo(new UtilResponse(7L, 3L, true));
        verify(port, never()).desmarcar(7L, 4L);

        when(port.marcar(7L, 4L)).thenReturn(false);
        when(port.contar(7L)).thenReturn(2L);
        assertThat(servicio.alternar(7L, "ana@u.edu.co")).isEqualTo(new UtilResponse(7L, 2L, false));
        verify(port).desmarcar(7L, 4L);
    }

    @Test
    void respuestaOcultaOUsuarioInexistente() {
        when(port.idUsuario("ana@u.edu.co")).thenReturn(4L);
        when(port.respuestaVisible(8L)).thenReturn(false);
        assertThatThrownBy(() -> servicio.alternar(8L, "ana@u.edu.co")).isInstanceOf(RespuestaNoEncontradaException.class);
        assertThatThrownBy(() -> servicio.alternar(8L, "nadie@u.edu.co")).isInstanceOf(RespuestaNoEncontradaException.class);
    }
}
