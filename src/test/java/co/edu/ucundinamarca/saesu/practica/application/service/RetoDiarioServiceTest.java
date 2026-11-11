package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.ports.input.OtorgarPuntosUseCase;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RetoDiarioResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.OpcionInvalidaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.RetoYaRespondidoException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort.Intento;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Reto del día: una sola oportunidad, +30 puntos si acierta. */
@ExtendWith(MockitoExtension.class)
class RetoDiarioServiceTest {

    private static final String CORREO = "ana@ucundinamarca.edu.co";
    private static final Long USUARIO = 4L;
    private static final Long PREGUNTA = 57L;
    private static final Long BIEN = 300L;
    private static final Long MAL = 301L;
    // 2026-11-09 03:00 UTC sigue siendo 8 de noviembre en Colombia (UTC-5)
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-09T03:00:00Z"), co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);
    private static final LocalDate HOY = LocalDate.of(2026, 11, 8);

    @Mock private RetoDiarioPort retos;
    @Mock private PreguntaRepositoryPort preguntas;
    @Mock private OpcionRepositoryPort opciones;
    @Mock private UsuarioConsultaPort usuarios;
    @Mock private OtorgarPuntosUseCase puntos;

    private RetoDiarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new RetoDiarioService(retos, preguntas, opciones, usuarios, puntos, RELOJ, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);
        Opcion bien = new Opcion(BIEN, PREGUNTA, "A", "3x²", true);
        Opcion mal = new Opcion(MAL, PREGUNTA, "B", "x²", false);
        lenient().when(usuarios.obtenerIdPorCorreo(CORREO)).thenReturn(USUARIO);
        lenient().when(retos.preguntaDelDia(HOY.toEpochDay())).thenReturn(Optional.of(PREGUNTA));
        lenient().when(preguntas.buscarPorId(PREGUNTA)).thenReturn(Optional.of(new Pregunta(PREGUNTA, Tema.DERIVADAS,
                "¿Derivada de x³?", Dificultad.BASICO, "Regla de la potencia", LocalDateTime.now(), List.of())));
        lenient().when(opciones.listarPorPreguntaId(PREGUNTA)).thenReturn(List.of(bien, mal));
        lenient().when(opciones.buscarPorId(BIEN)).thenReturn(Optional.of(bien));
        lenient().when(opciones.buscarPorId(MAL)).thenReturn(Optional.of(mal));
        lenient().when(retos.registrar(anyLong(), any(), any())).thenReturn(true);
    }

    @Test
    void pendienteNoDelataLaRespuesta() {
        RetoDiarioResponse r = servicio.hoy(CORREO);
        assertThat(r.fecha()).isEqualTo(HOY);
        assertThat(r.estado()).isEqualTo("pendiente");
        assertThat(r.pregunta().getOpciones()).hasSize(2);
        assertThat(r.opcionCorrecta()).isNull();
        assertThat(r.pista()).isNull();
        assertThat(r.puntos()).isEqualTo(30);
    }

    @Test
    void aciertaYGana30Puntos() {
        RetoDiarioResponse r = servicio.responder(CORREO, BIEN);
        assertThat(r.estado()).isEqualTo("ganado");
        assertThat(r.opcionElegida()).isEqualTo(BIEN);
        assertThat(r.opcionCorrecta()).isEqualTo(BIEN);
        assertThat(r.pista()).isEqualTo("Regla de la potencia");
        verify(retos).registrar(USUARIO, HOY, new Intento(PREGUNTA, BIEN, true));
        verify(puntos).otorgarPuntos(USUARIO, 30, "Reto del día 2026-11-08");
    }

    @Test
    void fallaYLoPierde() {
        RetoDiarioResponse r = servicio.responder(CORREO, MAL);
        assertThat(r.estado()).isEqualTo("perdido");
        assertThat(r.opcionCorrecta()).isEqualTo(BIEN);
        verify(puntos, never()).otorgarPuntos(anyLong(), anyInt(), anyString());
        verify(puntos).registrarActividad(USUARIO);
    }

    @Test
    void soloUnaOportunidad() {
        when(retos.intento(USUARIO, HOY)).thenReturn(Optional.of(new Intento(PREGUNTA, MAL, false)));
        assertThatThrownBy(() -> servicio.responder(CORREO, BIEN)).isInstanceOf(RetoYaRespondidoException.class);
        verify(retos, never()).registrar(anyLong(), any(), any());
        verify(puntos, never()).otorgarPuntos(anyLong(), anyInt(), anyString());

        RetoDiarioResponse r = servicio.hoy(CORREO);
        assertThat(r.estado()).isEqualTo("perdido");
        assertThat(r.opcionElegida()).isEqualTo(MAL);
    }

    @Test
    void dosRespuestasAlMismoTiempoSoloCuentaUna() {
        when(retos.registrar(anyLong(), any(), any())).thenReturn(false);
        assertThatThrownBy(() -> servicio.responder(CORREO, BIEN)).isInstanceOf(RetoYaRespondidoException.class);
        verify(puntos, never()).otorgarPuntos(anyLong(), anyInt(), anyString());
    }

    @Test
    void opcionDeOtraPregunta() {
        when(opciones.buscarPorId(999L)).thenReturn(Optional.of(new Opcion(999L, 8L, "A", "otra", true)));
        assertThatThrownBy(() -> servicio.responder(CORREO, 999L)).isInstanceOf(OpcionInvalidaException.class);
        assertThatThrownBy(() -> servicio.responder(CORREO, 1000L)).isInstanceOf(OpcionInvalidaException.class);
        verify(retos, never()).registrar(anyLong(), any(), any());
    }

    @Test
    void sinPreguntasOPreguntaBorrada() {
        when(retos.preguntaDelDia(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.hoy(CORREO)).isInstanceOf(PreguntaNoEncontradaException.class);
        when(retos.preguntaDelDia(anyLong())).thenReturn(Optional.of(77L));
        assertThatThrownBy(() -> servicio.hoy(CORREO)).isInstanceOf(PreguntaNoEncontradaException.class);
    }

    @Test
    void springLoCreaConUnSoloConstructorYLosBeansDeTiempo() {
        // Un solo constructor: Spring no tiene que adivinar (el error que tumbó el arranque)
        assertThat(RetoDiarioService.class.getDeclaredConstructors()).hasSize(1);

        var ctx = new org.springframework.context.annotation.AnnotationConfigApplicationContext();
        ctx.registerBean(RetoDiarioPort.class, () -> retos);
        ctx.registerBean(PreguntaRepositoryPort.class, () -> preguntas);
        ctx.registerBean(OpcionRepositoryPort.class, () -> opciones);
        ctx.registerBean(UsuarioConsultaPort.class, () -> usuarios);
        ctx.registerBean(co.edu.ucundinamarca.saesu.gamificacion.ports.input.OtorgarPuntosUseCase.class, () -> puntos);
        ctx.register(co.edu.ucundinamarca.saesu.core.config.TiempoConfig.class, RetoDiarioService.class);
        ctx.refresh();
        when(retos.preguntaDelDia(anyLong())).thenReturn(Optional.of(PREGUNTA));
        assertThat(ctx.getBean(RetoDiarioService.class).hoy(CORREO).fecha())
                .isEqualTo(LocalDate.now(co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO));
        ctx.close();
    }

    @Test
    void laZonaDecideQueDiaEs() {
        // 03:00 UTC del 9 de noviembre: en Bogotá todavía es 8, en Madrid ya es 9
        RetoDiarioService madrid = new RetoDiarioService(retos, preguntas, opciones, usuarios, puntos, RELOJ, java.time.ZoneId.of("Europe/Madrid"));
        when(retos.preguntaDelDia(anyLong())).thenReturn(Optional.of(PREGUNTA));
        assertThat(madrid.hoy(CORREO).fecha()).isEqualTo(HOY.plusDays(1));
    }
}
