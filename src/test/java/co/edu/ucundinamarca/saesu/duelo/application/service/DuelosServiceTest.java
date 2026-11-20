package co.edu.ucundinamarca.saesu.duelo.application.service;

import co.edu.ucundinamarca.saesu.duelo.application.dto.request.EntregaDueloRequest;
import co.edu.ucundinamarca.saesu.duelo.application.dto.request.EntregaDueloRequest.Eleccion;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.DueloResponse;
import co.edu.ucundinamarca.saesu.duelo.domain.exception.DueloNoEncontradoException;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort.FilaDuelo;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz.Opcion;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuelosServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 10, 0);
    private static final String ANA = "ana@u.edu.co";

    @Mock private DueloPort port;
    @Mock private QuizRepositoryPort banco;
    private DuelosService servicio;

    @BeforeEach
    void preparar() {
        servicio = new DuelosService(port, banco, Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA));
    }

    private static FilaDuelo duelo(Integer cRetador, Integer sRetador, Integer cRival, Integer sRival) {
        return new FilaDuelo(7L, "algebra", 1L, "Ana", 2L, "Beto", AHORA, 5, cRetador, sRetador, cRival, sRival);
    }

    private static PreguntaQuiz pregunta(long id, String correcta) {
        return new PreguntaQuiz(id, "algebra", "basico", "P" + id, null,
                List.of(new Opcion("A", "x", "A".equals(correcta)), new Opcion("B", "y", "B".equals(correcta))));
    }

    @Test
    @DisplayName("Estados y resultado desde el punto de vista de cada participante")
    void vistas() {
        DueloResponse porJugar = DuelosService.vista(duelo(null, null, 4, 30), 1L);
        assertThat(porJugar.estado()).isEqualTo("POR_JUGAR");
        assertThat(porJugar.suyasCorrectas()).isNull(); // no se revela antes de jugar
        assertThat(porJugar.rival()).isEqualTo("Beto");

        assertThat(DuelosService.vista(duelo(3, 40, null, null), 1L).estado()).isEqualTo("ESPERANDO");
        assertThat(DuelosService.vista(duelo(4, 50, 3, 20), 1L).resultado()).isEqualTo("GANASTE");
        assertThat(DuelosService.vista(duelo(4, 50, 3, 20), 2L).resultado()).isEqualTo("PERDISTE");
        assertThat(DuelosService.vista(duelo(4, 50, 4, 20), 1L).resultado()).isEqualTo("PERDISTE");
        assertThat(DuelosService.vista(duelo(4, 20, 4, 20), 2L).resultado()).isEqualTo("EMPATE");
        assertThat(DuelosService.vista(duelo(4, 20, 4, 20), 2L).rival()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Retar elige preguntas al azar y valida al rival")
    void retar() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        assertThatThrownBy(() -> servicio.retar(ANA, 1L, "algebra")).hasMessageContaining("ti mismo");

        when(port.esEstudiante(2L)).thenReturn(true);
        when(banco.preguntasAlAzar("algebra", 5)).thenReturn(List.of(pregunta(1, "A"), pregunta(2, "B"), pregunta(3, "A")));
        when(port.crear(1L, 2L, "algebra", List.of(1L, 2L, 3L), AHORA)).thenReturn(7L);
        when(port.buscar(7L, 1L)).thenReturn(Optional.of(duelo(null, null, null, null)));

        assertThat(servicio.retar(ANA, 2L, "algebra").estado()).isEqualTo("POR_JUGAR");
    }

    @Test
    @DisplayName("Sin preguntas suficientes no crea el reto")
    void pocasPreguntas() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.esEstudiante(2L)).thenReturn(true);
        when(banco.preguntasAlAzar("algebra", 5)).thenReturn(List.of(pregunta(1, "A")));
        assertThatThrownBy(() -> servicio.retar(ANA, 2L, "algebra")).isInstanceOf(IllegalArgumentException.class);
        verify(port, never()).crear(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Las preguntas no incluyen la respuesta y no se dan si ya respondió o no participa")
    void preguntas() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar(7L, 1L)).thenReturn(Optional.of(duelo(null, null, null, null)));
        when(port.preguntas(7L)).thenReturn(List.of(pregunta(1, "A")));
        assertThat(servicio.preguntas(ANA, 7L).get(0).opciones()).hasSize(2);

        when(port.buscar(7L, 1L)).thenReturn(Optional.of(duelo(3, 10, null, null)));
        assertThatThrownBy(() -> servicio.preguntas(ANA, 7L)).hasMessageContaining("Ya respondiste");

        when(port.buscar(8L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.preguntas(ANA, 8L)).isInstanceOf(DueloNoEncontradoException.class);
    }

    @Test
    @DisplayName("Entregar califica en el servidor y cuenta cada pregunta una vez")
    void entregar() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar(7L, 1L)).thenReturn(Optional.of(duelo(null, null, 4, 30)), Optional.of(duelo(2, 25, 4, 30)));
        when(port.preguntas(7L)).thenReturn(List.of(pregunta(1, "A"), pregunta(2, "B"), pregunta(3, "A")));
        when(port.entregar(eq(7L), eq(1L), anyInt(), eq(25), eq(AHORA))).thenReturn(true);

        DueloResponse r = servicio.entregar(ANA, 7L, new EntregaDueloRequest(List.of(
                new Eleccion(1L, "a"), new Eleccion(1L, "A"), new Eleccion(2L, "A"), new Eleccion(3L, "A"), new Eleccion(99L, "A")), 25));

        verify(port).entregar(7L, 1L, 2, 25, AHORA);
        assertThat(r.resultado()).isEqualTo("PERDISTE");
    }

    @Test
    @DisplayName("Entregar dos veces: 400")
    void entregarDosVeces() {
        when(port.idPorCorreo(ANA)).thenReturn(Optional.of(1L));
        when(port.buscar(7L, 1L)).thenReturn(Optional.of(duelo(2, 25, null, null)));
        when(port.preguntas(7L)).thenReturn(List.of());
        when(port.entregar(7L, 1L, 0, 5, AHORA)).thenReturn(false);
        assertThatThrownBy(() -> servicio.entregar(ANA, 7L, new EntregaDueloRequest(List.of(), 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
