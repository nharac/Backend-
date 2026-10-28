package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MiPosicionResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.RankingResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.RankingService.Periodo;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    /** Jueves 29 de octubre de 2026, 02:00 UTC = miércoles 28, 21:00 en Colombia. Servidor en UTC. */
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-10-29T02:00:00Z"), ZoneOffset.UTC);

    @Mock
    private RankingPort port;

    private RankingService service;

    @BeforeEach
    void setUp() {
        service = new RankingService(port, RELOJ);
        lenient().when(port.resumenPuntos(any(), any(), any())).thenReturn(new RankingPort.ResumenPuntos(0, 0, 0, 0));
    }

    @Test
    @DisplayName("La semana empieza el lunes a medianoche de Colombia (05:00 en un servidor UTC)")
    void inicioDeSemana() {
        assertThat(service.desde(Periodo.SEMANA)).isEqualTo(LocalDateTime.of(2026, 10, 26, 5, 0));
    }

    @Test
    @DisplayName("El mes empieza el día 1 en Colombia; el total no filtra por fecha")
    void inicioDeMesYTotal() {
        assertThat(service.desde(Periodo.MES)).isEqualTo(LocalDateTime.of(2026, 10, 1, 5, 0));
        assertThat(service.desde(Periodo.TOTAL)).isNull();
    }

    @Test
    @DisplayName("El periodo se lee sin importar mayúsculas y por defecto es total")
    void leerPeriodo() {
        assertThat(Periodo.de("Semana")).isEqualTo(Periodo.SEMANA);
        assertThat(Periodo.de(null)).isEqualTo(Periodo.TOTAL);
        assertThat(Periodo.de(" ")).isEqualTo(Periodo.TOTAL);
        assertThatThrownBy(() -> Periodo.de("anio"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("total, semana o mes");
    }

    @Test
    @DisplayName("El top convierte las filas del puerto y pide 10")
    void top() {
        when(port.top(isNull(), eq(10))).thenReturn(List.of(
                new RankingPort.FilaRanking(1, 4L, "Camila Torres", null, 310, 5)));

        List<RankingResponse> top = service.top(Periodo.TOTAL);

        assertThat(top).hasSize(1);
        assertThat(top.get(0).getNombre()).isEqualTo("Camila Torres");
        assertThat(top.get(0).getPuntosTotales()).isEqualTo(310L);
        assertThat(top.get(0).getRachaActual()).isEqualTo(5);
    }

    @Test
    @DisplayName("Mi posición: cuántos puntos faltan para superar al de arriba")
    void puntosParaSubir() {
        when(port.posicionDe(eq(1L), any())).thenReturn(Optional.of(
                new RankingPort.PosicionUsuario(2, 195, 4, 310L, "Camila Torres")));
        when(port.ultimosMovimientos(1L, 6)).thenReturn(List.of());

        MiPosicionResponse yo = service.miPosicion(1L, Periodo.SEMANA);

        assertThat(yo.periodo()).isEqualTo("semana");
        assertThat(yo.posicion()).isEqualTo(2);
        assertThat(yo.puntosParaSubir()).isEqualTo(116L);
        assertThat(yo.siguienteNombre()).isEqualTo("Camila Torres");
        assertThat(yo.participantes()).isEqualTo(4);
        verify(port, never()).participantes(any());
    }

    @Test
    @DisplayName("Mi posición: el primero no tiene a quién pasar")
    void primerLugar() {
        when(port.posicionDe(eq(4L), isNull())).thenReturn(Optional.of(
                new RankingPort.PosicionUsuario(1, 310, 4, null, null)));

        MiPosicionResponse yo = service.miPosicion(4L, Periodo.TOTAL);

        assertThat(yo.posicion()).isEqualTo(1);
        assertThat(yo.puntosParaSubir()).isNull();
        assertThat(yo.siguienteNombre()).isNull();
    }

    @Test
    @DisplayName("Mi posición: sin puntos en el periodo no hay puesto, pero sí el total de participantes")
    void sinPuntos() {
        when(port.posicionDe(eq(8L), any())).thenReturn(Optional.empty());
        when(port.participantes(any())).thenReturn(5L);

        MiPosicionResponse yo = service.miPosicion(8L, Periodo.MES);

        assertThat(yo.posicion()).isNull();
        assertThat(yo.puntos()).isZero();
        assertThat(yo.participantes()).isEqualTo(5);
        assertThat(yo.puntosParaSubir()).isNull();
    }

    @Test
    @DisplayName("Mi posición: puntos de hoy desde la medianoche de Colombia y de la semana desde el lunes")
    void puntosHoyYSemana() {
        when(port.posicionDe(eq(1L), any())).thenReturn(Optional.empty());
        when(port.resumenPuntos(1L, LocalDateTime.of(2026, 10, 28, 5, 0), LocalDateTime.of(2026, 10, 26, 5, 0)))
                .thenReturn(new RankingPort.ResumenPuntos(3, 20, 45, 195));
        when(port.ultimosMovimientos(eq(1L), anyInt())).thenReturn(List.of(
                new RankingPort.Movimiento(20, "Acierto en práctica: limites", LocalDateTime.of(2026, 10, 28, 20, 0))));

        MiPosicionResponse yo = service.miPosicion(1L, Periodo.TOTAL);

        assertThat(yo.rachaActual()).isEqualTo(3);
        assertThat(yo.puntosHoy()).isEqualTo(20);
        assertThat(yo.puntosSemana()).isEqualTo(45);
        assertThat(yo.puntosTotales()).isEqualTo(195);
        assertThat(yo.ultimosMovimientos()).singleElement()
                .satisfies(m -> assertThat(m.motivo()).isEqualTo("Acierto en práctica: limites"));
    }
}
