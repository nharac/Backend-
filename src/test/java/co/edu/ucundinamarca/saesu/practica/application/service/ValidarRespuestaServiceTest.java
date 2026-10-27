package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.LogrosService;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ValidacionResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.OpcionInvalidaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RespuestaEstudianteRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Validación de respuestas de práctica: puntos que bajan con cada fallo (20/15/10/5),
 * puntos solo la primera vez que se resuelve y registro de cada intento.
 */
@ExtendWith(MockitoExtension.class)
class ValidarRespuestaServiceTest {

    private static final Long PREGUNTA = 1L;
    private static final Long CORRECTA = 11L;
    private static final Long INCORRECTA = 12L;
    private static final Long USUARIO = 5L;
    private static final String CORREO = "juan@ucundinamarca.edu.co";

    @Mock private PreguntaRepositoryPort preguntaRepository;
    @Mock private OpcionRepositoryPort opcionRepository;
    @Mock private RespuestaEstudianteRepositoryPort respuestaRepository;
    @Mock private UsuarioConsultaPort usuarioConsultaPort;
    @Mock private PuntosService puntosService;
    @Mock private LogrosService logrosService;

    @InjectMocks private ValidarRespuestaService servicio;

    private final List<RespuestaEstudiante> historial = new ArrayList<>();

    @BeforeEach
    void preparar() {
        Pregunta pregunta = new Pregunta(PREGUNTA, Tema.DERIVADAS, "¿Derivada de x³?", Dificultad.BASICO,
                "Usa la regla de la potencia", LocalDateTime.now(), List.of());
        lenient().when(preguntaRepository.buscarPorId(PREGUNTA)).thenReturn(Optional.of(pregunta));
        lenient().when(opcionRepository.buscarPorId(CORRECTA))
                .thenReturn(Optional.of(new Opcion(CORRECTA, PREGUNTA, "A", "3x²", true)));
        lenient().when(opcionRepository.buscarPorId(INCORRECTA))
                .thenReturn(Optional.of(new Opcion(INCORRECTA, PREGUNTA, "B", "x²", false)));
        lenient().when(usuarioConsultaPort.obtenerIdPorCorreo(CORREO)).thenReturn(USUARIO);
        lenient().when(respuestaRepository.listarPorUsuario(USUARIO)).thenReturn(historial);
        lenient().when(logrosService.evaluar(USUARIO)).thenReturn(List.of());
    }

    private void fallosPrevios(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            historial.add(new RespuestaEstudiante((long) i, USUARIO, PREGUNTA, INCORRECTA, false, LocalDateTime.now()));
        }
    }

    @ParameterizedTest(name = "con {0} fallos previos vale {1} puntos")
    @CsvSource({"0, 20", "1, 15", "2, 10", "3, 5", "4, 5", "10, 5"})
    @DisplayName("La regla de puntos baja 5 por fallo y nunca de 5")
    void reglaDePuntos(long fallos, int esperado) {
        assertThat(ValidarRespuestaService.puntosSegunFallos(fallos)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Acertar al primer intento da 20 puntos")
    void aciertoAlPrimerIntento() {
        ValidacionResponse r = servicio.validar(PREGUNTA, CORRECTA, CORREO);

        assertThat(r.getEsCorrecta()).isTrue();
        assertThat(r.getPuntosGanados()).isEqualTo(20);
        assertThat(r.getMensaje()).contains("+20 puntos");
        assertThat(r.getOpcionCorrectaId()).isEqualTo(CORRECTA);
        verify(puntosService).otorgarPuntos(eq(USUARIO), eq(20), anyString());
    }

    @Test
    @DisplayName("Acertar después de 2 fallos da 10 puntos")
    void aciertoDespuesDeDosFallos() {
        fallosPrevios(2);

        ValidacionResponse r = servicio.validar(PREGUNTA, CORRECTA, CORREO);

        assertThat(r.getPuntosGanados()).isEqualTo(10);
        verify(puntosService).otorgarPuntos(eq(USUARIO), eq(10), anyString());
    }

    @Test
    @DisplayName("Una pregunta ya resuelta no vuelve a dar puntos, pero cuenta para la racha")
    void preguntaYaResuelta() {
        historial.add(new RespuestaEstudiante(1L, USUARIO, PREGUNTA, CORRECTA, true, LocalDateTime.now()));

        ValidacionResponse r = servicio.validar(PREGUNTA, CORRECTA, CORREO);

        assertThat(r.getEsCorrecta()).isTrue();
        assertThat(r.getPuntosGanados()).isZero();
        assertThat(r.getMensaje()).contains("Ya habías resuelto");
        verify(puntosService, never()).otorgarPuntos(anyLong(), anyInt(), anyString());
        verify(puntosService).registrarActividad(USUARIO);
    }

    @Test
    @DisplayName("Fallar devuelve la pista, habilita el tutor y dice cuánto vale el siguiente intento")
    void fallo() {
        fallosPrevios(1);

        ValidacionResponse r = servicio.validar(PREGUNTA, INCORRECTA, CORREO);

        assertThat(r.getEsCorrecta()).isFalse();
        assertThat(r.getPista()).isEqualTo("Usa la regla de la potencia");
        assertThat(r.getChatIaHabilitado()).isTrue();
        assertThat(r.getOpcionCorrectaId()).isNull();
        assertThat(r.getPuntosEnJuego()).isEqualTo(10); // ya iba 1 fallo + este = 2
        verify(puntosService, never()).otorgarPuntos(anyLong(), anyInt(), anyString());
        verify(logrosService, never()).evaluar(anyLong());
    }

    @Test
    @DisplayName("Cada intento queda registrado con el usuario y si fue correcto")
    void registraElIntento() {
        servicio.validar(PREGUNTA, INCORRECTA, CORREO);

        ArgumentCaptor<RespuestaEstudiante> captor = ArgumentCaptor.forClass(RespuestaEstudiante.class);
        verify(respuestaRepository).guardar(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo(USUARIO);
        assertThat(captor.getValue().getPreguntaId()).isEqualTo(PREGUNTA);
        assertThat(captor.getValue().getEsCorrecta()).isFalse();
    }

    @Test
    @DisplayName("Al acertar se devuelven los logros recién desbloqueados")
    void devuelveLogrosNuevos() {
        LogroResponse logro = new LogroResponse("PRIMER_ACIERTO", "Primer paso", "Primera respuesta correcta",
                "rocket_launch", true, null);
        when(logrosService.evaluar(USUARIO)).thenReturn(List.of(logro));

        ValidacionResponse r = servicio.validar(PREGUNTA, CORRECTA, CORREO);

        assertThat(r.getNuevosLogros()).extracting(LogroResponse::getCodigo).containsExactly("PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Una opción de otra pregunta se rechaza")
    void opcionDeOtraPregunta() {
        when(opcionRepository.buscarPorId(99L)).thenReturn(Optional.of(new Opcion(99L, 2L, "A", "x", true)));

        assertThatThrownBy(() -> servicio.validar(PREGUNTA, 99L, CORREO))
                .isInstanceOf(OpcionInvalidaException.class);
        verify(respuestaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Una pregunta que no existe se rechaza")
    void preguntaInexistente() {
        when(preguntaRepository.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.validar(404L, CORRECTA, CORREO))
                .isInstanceOf(PreguntaNoEncontradaException.class);
    }
}
