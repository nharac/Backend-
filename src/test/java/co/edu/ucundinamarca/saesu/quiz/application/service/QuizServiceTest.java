package co.edu.ucundinamarca.saesu.quiz.application.service;

import co.edu.ucundinamarca.saesu.quiz.application.dto.request.FinalizarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.request.IniciarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizHistorialResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizIniciadoResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizResultadoResponse;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizNoEncontradoException;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizYaFinalizadoException;
import co.edu.ucundinamarca.saesu.quiz.domain.model.IntentoQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort.Eleccion;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    private static final String CORREO = "laura@ucundinamarca.edu.co";
    private static final Instant AHORA = Instant.parse("2026-10-28T20:00:00Z");

    @Mock private QuizRepositoryPort port;
    @Mock private UsuarioRepositoryPort usuarios;

    private QuizService service;

    @BeforeEach
    void setUp() {
        service = conReloj(AHORA);
        Usuario laura = new Usuario();
        laura.setId(1L);
        lenient().when(usuarios.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        lenient().when(port.totales(1L)).thenReturn(new QuizRepositoryPort.Totales(0, 0, 0));
    }

    private QuizService conReloj(Instant instante) {
        return new QuizService(port, usuarios, Clock.fixed(instante, ZoneOffset.UTC));
    }

    private static PreguntaQuiz pregunta(long id, String correcta) {
        return new PreguntaQuiz(id, "limites", "basico", "Pregunta " + id, "Pista " + id, List.of(
                new PreguntaQuiz.Opcion("A", "1", "A".equals(correcta)),
                new PreguntaQuiz.Opcion("B", "2", "B".equals(correcta)),
                new PreguntaQuiz.Opcion("C", "3", "C".equals(correcta)),
                new PreguntaQuiz.Opcion("D", "4", "D".equals(correcta))));
    }

    private static IntentoQuiz intento(long usuarioId, Instant finalizado) {
        return new IntentoQuiz(7L, usuarioId, "limites", 2, AHORA.minusSeconds(60), AHORA.plusSeconds(120),
                finalizado, null, false);
    }

    @Test
    @DisplayName("Iniciar: 90 s por pregunta y las opciones no dicen cuál es la correcta")
    void iniciar() {
        when(port.preguntasAlAzar("derivadas", 5)).thenReturn(List.of(pregunta(1, "A"), pregunta(2, "C")));
        when(port.crearIntento(eq(1L), eq("derivadas"), eq(AHORA), eq(AHORA.plusSeconds(180)), eq(List.of(1L, 2L))))
                .thenReturn(7L);

        QuizIniciadoResponse r = service.iniciar(CORREO, new IniciarQuizRequest("Derivadas", 5));

        assertThat(r.intentoId()).isEqualTo(7L);
        assertThat(r.cantidad()).isEqualTo(2);
        assertThat(r.segundosLimite()).isEqualTo(180);
        assertThat(r.preguntas()).extracting(QuizIniciadoResponse.Pregunta::orden).containsExactly(1, 2);
        // El record de opción del quiz solo tiene letra y texto: no hay forma de filtrar la respuesta
        assertThat(QuizIniciadoResponse.Opcion.class.getRecordComponents()).hasSize(2);
    }

    @Test
    @DisplayName("Iniciar: 'mixto' o vacío usan los tres temas; un tema desconocido es 400")
    void temas() {
        when(port.preguntasAlAzar(isNull(), anyInt())).thenReturn(List.of(pregunta(1, "A")));
        when(port.crearIntento(any(), isNull(), any(), any(), anyList())).thenReturn(8L);

        assertThat(service.iniciar(CORREO, new IniciarQuizRequest("mixto", 10)).tema()).isNull();
        assertThatThrownBy(() -> service.iniciar(CORREO, new IniciarQuizRequest("algebra", 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Iniciar: sin preguntas en el banco no se crea el intento")
    void sinPreguntas() {
        when(port.preguntasAlAzar("integrales", 10)).thenReturn(List.of());

        assertThatThrownBy(() -> service.iniciar(CORREO, new IniciarQuizRequest("integrales", 10)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(port, never()).crearIntento(any(), any(), any(), any(), anyList());
    }

    @Test
    @DisplayName("Finalizar: califica en el servidor, ignora letras inválidas y las sin responder cuentan como malas")
    void finalizar() {
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(1L, null)));
        when(port.preguntasDelIntento(7L)).thenReturn(List.of(pregunta(1, "A"), pregunta(2, "C"), pregunta(3, "B")));
        when(port.finalizar(eq(7L), eq(AHORA), anyInt(), anyBoolean(), anyList())).thenReturn(true);

        QuizResultadoResponse r = service.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of(
                new FinalizarQuizRequest.Respuesta(1L, "a"),     // correcta (minúscula)
                new FinalizarQuizRequest.Respuesta(2L, "Z"))));  // letra que no existe → sin responder

        assertThat(r.correctas()).isEqualTo(1);
        assertThat(r.total()).isEqualTo(3);
        assertThat(r.porcentaje()).isEqualTo(33);
        assertThat(r.duracionSegundos()).isEqualTo(60);
        assertThat(r.fueraDeTiempo()).isFalse();
        assertThat(r.detalle()).extracting(QuizResultadoResponse.Detalle::letraElegida).containsExactly("A", null, null);
        assertThat(r.detalle()).extracting(QuizResultadoResponse.Detalle::letraCorrecta).containsExactly("A", "C", "B");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Eleccion>> guardadas = ArgumentCaptor.forClass(List.class);
        verify(port).finalizar(eq(7L), eq(AHORA), eq(1), eq(false), guardadas.capture());
        assertThat(guardadas.getValue()).extracting(Eleccion::correcta).containsExactly(true, false, false);
    }

    @Test
    @DisplayName("Finalizar después del tiempo (más 10 s de gracia) queda marcado y el tiempo se corta en el límite")
    void fueraDeTiempo() {
        QuizService tarde = conReloj(AHORA.plusSeconds(200));
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(1L, null)));
        when(port.preguntasDelIntento(7L)).thenReturn(List.of(pregunta(1, "A")));
        when(port.finalizar(eq(7L), any(), anyInt(), eq(true), anyList())).thenReturn(true);

        QuizResultadoResponse r = tarde.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of()));

        assertThat(r.fueraDeTiempo()).isTrue();
        assertThat(r.duracionSegundos()).isEqualTo(180);
    }

    @Test
    @DisplayName("No se puede entregar dos veces")
    void dobleEntrega() {
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(1L, AHORA.minusSeconds(5))));

        assertThatThrownBy(() -> service.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of())))
                .isInstanceOf(QuizYaFinalizadoException.class);
        verify(port, never()).finalizar(any(), any(), anyInt(), anyBoolean(), anyList());
    }

    @Test
    @DisplayName("Si dos entregas llegan a la vez, la segunda pierde (la BD solo deja finalizar una)")
    void carrera() {
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(1L, null)));
        when(port.preguntasDelIntento(7L)).thenReturn(List.of(pregunta(1, "A")));
        when(port.finalizar(any(), any(), anyInt(), anyBoolean(), anyList())).thenReturn(false);

        assertThatThrownBy(() -> service.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of())))
                .isInstanceOf(QuizYaFinalizadoException.class);
    }

    @Test
    @DisplayName("El quiz de otra persona responde 404")
    void ajeno() {
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(99L, null)));

        assertThatThrownBy(() -> service.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of())))
                .isInstanceOf(QuizNoEncontradoException.class);
        assertThatThrownBy(() -> service.resultado(CORREO, 7L)).isInstanceOf(QuizNoEncontradoException.class);
    }

    @Test
    @DisplayName("Mejor marca: solo si ya tenía quices y supera su mejor resultado")
    void mejorMarca() {
        when(port.totales(1L)).thenReturn(new QuizRepositoryPort.Totales(3, 40, 50));
        when(port.buscarIntento(7L)).thenReturn(Optional.of(intento(1L, null)));
        when(port.preguntasDelIntento(7L)).thenReturn(List.of(pregunta(1, "A"), pregunta(2, "B")));
        when(port.finalizar(any(), any(), anyInt(), anyBoolean(), anyList())).thenReturn(true);

        QuizResultadoResponse r = service.finalizar(CORREO, 7L, new FinalizarQuizRequest(List.of(
                new FinalizarQuizRequest.Respuesta(1L, "A"), new FinalizarQuizRequest.Respuesta(2L, "B"))));

        assertThat(r.porcentaje()).isEqualTo(100);
        assertThat(r.mejorMarca()).isTrue();
    }

    @Test
    @DisplayName("Historial: promedio y mejor redondeados")
    void historial() {
        when(port.totales(1L)).thenReturn(new QuizRepositoryPort.Totales(2, 66.6, 80));
        when(port.historial(1L, 10)).thenReturn(List.of(new IntentoQuiz(3L, 1L, null, 10,
                AHORA.minusSeconds(400), AHORA.plusSeconds(500), AHORA, 8, false)));

        QuizHistorialResponse h = service.historial(CORREO);

        assertThat(h.intentos()).isEqualTo(2);
        assertThat(h.promedio()).isEqualTo(67);
        assertThat(h.mejor()).isEqualTo(80);
        assertThat(h.ultimos()).singleElement().satisfies(i -> {
            assertThat(i.porcentaje()).isEqualTo(80);
            assertThat(i.duracionSegundos()).isEqualTo(400);
        });
    }
}
