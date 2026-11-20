package co.edu.ucundinamarca.saesu.duelo.application.service;

import co.edu.ucundinamarca.saesu.duelo.application.dto.response.TorneoResponse;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort.FilaDuelo;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TorneoServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    // Viernes 20 de noviembre de 2026: la semana va del lunes 16 al domingo 22
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 10, 0);

    @Mock private DueloPort port;
    @Mock private NotificarUseCase avisos;
    private TorneoService servicio;

    @BeforeEach
    void preparar() {
        servicio = new TorneoService(port, Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA));
    }

    private static FilaDuelo reto(long ana, int cA, int sA, long beto, int cB, int sB) {
        return new FilaDuelo(1L, "algebra", "CLASICO", 5, ana, "U" + ana, beto, "U" + beto, AHORA, 5, cA, sA, cB, sB);
    }

    @Test
    void calculaLaTablaDeLaSemana() {
        when(port.idPorCorreo("a@u.edu.co")).thenReturn(Optional.of(1L));
        when(port.terminadosEntre(LocalDate.of(2026, 11, 16).atStartOfDay(), LocalDate.of(2026, 11, 23).atStartOfDay())).thenReturn(List.of(
                reto(1, 4, 30, 2, 3, 20),   // gana 1
                reto(2, 3, 30, 3, 3, 30),   // empate 2-3
                reto(3, 2, 10, 1, 2, 50)));  // gana 3 (más rápido)
        when(port.terminadosEntre(LocalDate.of(2026, 11, 9).atStartOfDay(), LocalDate.of(2026, 11, 16).atStartOfDay())).thenReturn(List.of());

        TorneoResponse t = servicio.semanaActual("a@u.edu.co");

        assertThat(t.inicio()).isEqualTo(LocalDate.of(2026, 11, 16));
        assertThat(t.diasRestantes()).isEqualTo(2);
        assertThat(t.tabla()).extracting(TorneoResponse.Fila::usuarioId).containsExactly(3L, 1L, 2L);
        assertThat(t.tabla().get(0).puntos()).isEqualTo(4);
        assertThat(t.tabla().get(1).soyYo()).isTrue();
        assertThat(t.tabla().get(2).empates()).isEqualTo(1);
        assertThat(t.campeonAnterior()).isNull();
    }

    @Test
    void elLunesAvisaAlPodio() {
        servicio.setAvisos(avisos);
        when(port.terminadosEntre(LocalDate.of(2026, 11, 9).atStartOfDay(), LocalDate.of(2026, 11, 16).atStartOfDay()))
                .thenReturn(List.of(reto(1, 4, 30, 2, 3, 20)));
        servicio.premiarSemanaAnterior();
        verify(avisos).notificar(eq(1L), eq("TORNEO"), anyString(), anyString(), eq("/retos"));
        verify(avisos).notificar(eq(2L), eq("TORNEO"), anyString(), anyString(), eq("/retos"));
    }

    @Test
    void compararPorPuntajeYTiempo() {
        assertThat(TorneoService.comparar(3, 10, 2, 5)).isEqualTo(1);
        assertThat(TorneoService.comparar(2, 10, 2, 5)).isEqualTo(-1);
        assertThat(TorneoService.comparar(2, 5, 2, 5)).isZero();
    }
}
