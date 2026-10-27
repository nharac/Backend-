package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.entity.PuntoHistorialEntity;
import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.repository.PuntoHistorialJpaRepository;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Puntos, racha diaria y reglas anti-abuso del foro. */
@ExtendWith(MockitoExtension.class)
class PuntosServiceTest {

    private static final Long USUARIO = 5L;

    @Mock private PuntoHistorialJpaRepository puntosRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;

    @InjectMocks private PuntosService servicio;

    private UsuarioEntity usuario;

    @BeforeEach
    void preparar() {
        usuario = new UsuarioEntity();
        usuario.setId(USUARIO);
        usuario.setNombre("Juan");
        usuario.setRachaActual(0);
        lenient().when(usuarioRepository.findById(USUARIO)).thenReturn(Optional.of(usuario));
    }

    private List<PuntoHistorialEntity> historialGuardado(int veces) {
        ArgumentCaptor<PuntoHistorialEntity> captor = ArgumentCaptor.forClass(PuntoHistorialEntity.class);
        verify(puntosRepository, times(veces)).save(captor.capture());
        return captor.getAllValues();
    }

    // ---------- Racha ----------

    @Test
    @DisplayName("La primera práctica empieza la racha en 1")
    void primeraPractica() {
        servicio.otorgarPuntos(USUARIO, 20, "Acierto");

        assertThat(usuario.getRachaActual()).isEqualTo(1);
        assertThat(usuario.getUltimaFechaPractica()).isEqualTo(LocalDate.now());
        assertThat(historialGuardado(1)).extracting(PuntoHistorialEntity::getCantidad).containsExactly(20);
    }

    @Test
    @DisplayName("Practicar al día siguiente sube la racha y da +5 de bonus")
    void rachaConsecutiva() {
        usuario.setRachaActual(3);
        usuario.setUltimaFechaPractica(LocalDate.now().minusDays(1));

        servicio.otorgarPuntos(USUARIO, 20, "Acierto");

        assertThat(usuario.getRachaActual()).isEqualTo(4);
        assertThat(historialGuardado(2)).extracting(PuntoHistorialEntity::getCantidad).containsExactly(20, 5);
    }

    @Test
    @DisplayName("Si pasó más de un día, la racha vuelve a 1")
    void rachaRota() {
        usuario.setRachaActual(6);
        usuario.setUltimaFechaPractica(LocalDate.now().minusDays(3));

        servicio.otorgarPuntos(USUARIO, 20, "Acierto");

        assertThat(usuario.getRachaActual()).isEqualTo(1);
    }

    @Test
    @DisplayName("Practicar dos veces el mismo día no cambia la racha")
    void mismoDia() {
        usuario.setRachaActual(2);
        usuario.setUltimaFechaPractica(LocalDate.now());

        servicio.otorgarPuntos(USUARIO, 20, "Acierto");

        assertThat(usuario.getRachaActual()).isEqualTo(2);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarActividad cuenta para la racha sin dar puntos")
    void actividadSinPuntos() {
        usuario.setRachaActual(1);
        usuario.setUltimaFechaPractica(LocalDate.now().minusDays(1));

        servicio.registrarActividad(USUARIO);

        assertThat(usuario.getRachaActual()).isEqualTo(2);
        // solo el bonus de racha, ningún punto "base"
        assertThat(historialGuardado(1)).extracting(PuntoHistorialEntity::getCantidad).containsExactly(5);
    }

    // ---------- Foro ----------

    @Test
    @DisplayName("Responder en el foro da +10")
    void foroDaPuntos() {
        when(puntosRepository.contarPorMotivoDesde(eq(USUARIO), eq(PuntosService.MOTIVO_FORO), any(LocalDateTime.class)))
                .thenReturn(0L);

        boolean dio = servicio.otorgarPuntosForo(USUARIO, 9L);

        assertThat(dio).isTrue();
        assertThat(historialGuardado(1).get(0).getCantidad()).isEqualTo(10);
    }

    @Test
    @DisplayName("Responder en tu propia publicación no da puntos")
    void foroPropio() {
        boolean dio = servicio.otorgarPuntosForo(USUARIO, USUARIO);

        assertThat(dio).isFalse();
        verify(puntosRepository, never()).save(any());
    }

    @Test
    @DisplayName("Después de 3 respuestas con puntos en el día, ya no da más")
    void foroTopeDiario() {
        when(puntosRepository.contarPorMotivoDesde(eq(USUARIO), eq(PuntosService.MOTIVO_FORO), any(LocalDateTime.class)))
                .thenReturn((long) PuntosService.MAX_RESPUESTAS_FORO_CON_PUNTOS_POR_DIA);

        boolean dio = servicio.otorgarPuntosForo(USUARIO, 9L);

        assertThat(dio).isFalse();
        verify(puntosRepository, never()).save(any());
    }

    // ---------- Premios únicos ----------

    @Test
    @DisplayName("Un premio único no se repite (ej. respuesta oficial marcada dos veces)")
    void premioUnico() {
        when(puntosRepository.existePorMotivo(USUARIO, "Respuesta oficial #7")).thenReturn(false, true);

        servicio.otorgarPuntosUnaVez(USUARIO, 15, "Respuesta oficial #7");
        servicio.otorgarPuntosUnaVez(USUARIO, 15, "Respuesta oficial #7");

        assertThat(historialGuardado(1).get(0).getCantidad()).isEqualTo(15);
        verify(puntosRepository, times(2)).existePorMotivo(anyLong(), anyString());
    }
}
