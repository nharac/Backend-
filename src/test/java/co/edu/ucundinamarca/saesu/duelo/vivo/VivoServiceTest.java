package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.duelo.domain.exception.DueloNoEncontradoException;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.EstadoSala;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.MiRespuesta;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoPort.Respuesta;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoPort.Sala;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz.Opcion;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VivoServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 20, 10, 0, 0);
    private static final String ANA = "ana@u.edu.co";

    @Mock private VivoPort port;
    @Mock private QuizRepositoryPort banco;
    @Mock private NotificarUseCase avisos;

    private VivoService en(LocalDateTime momento) {
        VivoService s = new VivoService(port, banco, Clock.fixed(momento.atZone(BOGOTA).toInstant(), BOGOTA));
        s.setAvisos(avisos);
        return s;
    }

    private static PreguntaQuiz pregunta(long id) {
        return new PreguntaQuiz(id, "algebra", "basico", "P" + id, null, List.of(new Opcion("A", "1", true), new Opcion("B", "2", false)));
    }

    private static Sala jugando() {
        return new Sala(9L, "ABC123", 1L, "Ana", 2L, "Beto", "algebra", "JUGANDO", 20, INICIO, 3);
    }

    @Test
    void crearInvitaAlCompaneroYQuedaEsperando() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.esEstudiante(2L)).thenReturn(true);
        when(banco.preguntasAlAzar("algebra", 5)).thenReturn(List.of(pregunta(1), pregunta(2), pregunta(3)));
        when(port.codigoExiste(anyString())).thenReturn(false);
        when(port.buscar(anyString())).thenReturn(Optional.of(new Sala(9L, "ABC123", 1L, "Ana", 2L, "Beto", "algebra", "ESPERANDO", 20, null, 3)));

        EstadoSala e = en(INICIO).crear(ANA, 2L, "algebra");

        assertThat(e.estado()).isEqualTo("ESPERANDO");
        assertThat(e.soyAnfitrion()).isTrue();
        assertThat(e.rival()).isEqualTo("Beto");
        verify(port).crear(anyString(), eq(1L), eq(2L), eq("algebra"), eq(20), eq(List.of(1L, 2L, 3L)), eq(INICIO));
        verify(avisos).notificar(eq(2L), eq("EN_VIVO"), anyString(), anyString(), anyString());
    }

    @Test
    void cuentaRegresivaYLuegoLaPreguntaQueVa() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar("ABC123")).thenReturn(Optional.of(jugando()));
        lenient().when(port.respuestas(9L)).thenReturn(List.of(new Respuesta(2L, 1, "A", true, 130)));
        lenient().when(port.preguntas(9L)).thenReturn(List.of(pregunta(1), pregunta(2), pregunta(3)));

        assertThat(en(INICIO.minusSeconds(3)).ver(ANA, "abc123").cuentaRegresiva()).isEqualTo(3);

        EstadoSala e = en(INICIO.plusSeconds(25)).ver(ANA, "ABC123");
        assertThat(e.indice()).isEqualTo(1);
        assertThat(e.segundosRestantes()).isEqualTo(15);
        assertThat(e.pregunta().id()).isEqualTo(2L);
        assertThat(e.jugadores()).extracting(VivoDtos.Jugador::puntos).containsExactly(0, 130);
        assertThat(e.jugadores().get(1).respondioActual()).isTrue();
    }

    @Test
    void responderDaBonoPorRapidezYNoDejaResponderOtraPregunta() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar("ABC123")).thenReturn(Optional.of(jugando()));
        when(port.preguntas(9L)).thenReturn(List.of(pregunta(1), pregunta(2), pregunta(3)));
        when(port.responder(eq(9L), eq(1L), eq(0), eq("A"), eq(true), anyInt(), any())).thenReturn(true);

        MiRespuesta r = en(INICIO.plusSeconds(5)).responder(ANA, "ABC123", 0, "a");
        assertThat(r.correcta()).isTrue();
        assertThat(r.puntos()).isEqualTo(100 + 38); // quedaban 15 de 20 s: 50 * 0.75 = 37.5 → 38

        assertThatThrownBy(() -> en(INICIO.plusSeconds(25)).responder(ANA, "ABC123", 0, "A"))
                .hasMessageContaining("ya pasó");
    }

    @Test
    void alAcabarElTiempoTerminaYDiceQuienGano() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar("ABC123")).thenReturn(Optional.of(jugando()));
        when(port.respuestas(9L)).thenReturn(List.of(new Respuesta(1L, 0, "A", true, 140), new Respuesta(2L, 0, "A", true, 120)));

        EstadoSala e = en(INICIO.plusSeconds(61)).ver(ANA, "ABC123");

        assertThat(e.estado()).isEqualTo("TERMINADA");
        assertThat(e.resultado()).isEqualTo("GANASTE");
        verify(port).cambiarEstado(9L, "TERMINADA");
    }

    @Test
    void unaSalaAjenaNoSePuedeVer() {
        when(port.idPorCorreo("otro")).thenReturn(Optional.of(7L));
        when(port.buscar("ABC123")).thenReturn(Optional.of(jugando()));
        assertThatThrownBy(() -> en(INICIO).ver("otro", "ABC123")).isInstanceOf(DueloNoEncontradoException.class);
    }

    @Test
    void unirseArrancaLaCuentaRegresiva() {
        when(port.idPorCorreo("beto")).thenReturn(Optional.of(2L));
        Sala esperando = new Sala(9L, "ABC123", 1L, "Ana", null, null, "algebra", "ESPERANDO", 20, null, 3);
        when(port.buscar("ABC123")).thenReturn(Optional.of(esperando), Optional.of(jugando()));
        when(port.unir(9L, 2L, INICIO)).thenReturn(true); // ahora + 5 s de cuenta regresiva
        when(port.respuestas(anyLong())).thenReturn(List.of());

        EstadoSala e = en(INICIO.plusSeconds(-5)).unirse("beto", "ABC123");
        assertThat(e.estado()).isEqualTo("JUGANDO");
    }
}
