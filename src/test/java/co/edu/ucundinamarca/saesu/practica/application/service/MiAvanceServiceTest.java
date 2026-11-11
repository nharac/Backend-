package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.MetaSemanalResponse;
import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort;
import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort.Dia;
import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort.Rendimiento;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MiAvanceServiceTest {

    private static final String CORREO = "ana@ucundinamarca.edu.co";
    private static final Long ID = 9L;
    /** Miércoles 4 de noviembre de 2026, 11 p. m. en Colombia (ya es jueves en UTC). */
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-05T04:00:00Z"), co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);
    private static final LocalDate HOY = LocalDate.of(2026, 11, 4);

    private final MiAvancePort port = mock(MiAvancePort.class);
    private final UsuarioRepositoryPort usuarios = mock(UsuarioRepositoryPort.class);
    private final MiAvanceService servicio = new MiAvanceService(port, usuarios, RELOJ, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);

    @BeforeEach
    void setUp() {
        Usuario ana = mock(Usuario.class);
        when(ana.getId()).thenReturn(ID);
        when(usuarios.buscarPorCorreo(CORREO)).thenReturn(Optional.of(ana));
    }

    private static Rendimiento r(String tema, String dificultad, long respondidas, long correctas) {
        return new Rendimiento(tema, dificultad, respondidas, correctas);
    }

    @Test
    @DisplayName("Actividad: un día por fecha (también vacíos), totales y mejor día, en hora de Colombia")
    void actividad() {
        when(port.actividadDesde(ID, HOY.minusDays(6))).thenReturn(List.of(
                new Dia(HOY.minusDays(5), 4, 3), new Dia(HOY.minusDays(1), 10, 8), new Dia(HOY, 2, 2)));

        ActividadResponse a = servicio.actividad(CORREO, 7);

        assertThat(a.dias()).hasSize(7);
        assertThat(a.dias().get(0).fecha()).isEqualTo(HOY.minusDays(6));
        assertThat(a.dias().get(6).fecha()).as("hoy en Colombia, no en UTC").isEqualTo(HOY);
        assertThat(a.dias().get(1).respondidas()).isEqualTo(4);
        assertThat(a.dias().get(2).respondidas()).isZero();
        assertThat(a.diasActivos()).isEqualTo(3);
        assertThat(a.totalRespondidas()).isEqualTo(16);
        assertThat(a.totalAciertos()).isEqualTo(13);
        assertThat(a.mejorDia().fecha()).isEqualTo(HOY.minusDays(1));
    }

    @Test
    @DisplayName("Actividad: los días pedidos se limitan entre 7 y 371; sin práctica no hay mejor día")
    void actividadLimites() {
        when(port.actividadDesde(eq(ID), any())).thenReturn(List.of());

        assertThat(servicio.actividad(CORREO, 2).dias()).hasSize(7);
        assertThat(servicio.actividad(CORREO, 5000).dias()).hasSize(371);
        assertThat(servicio.actividad(CORREO, 30).mejorDia()).isNull();
    }

    @Test
    @DisplayName("Análisis: 9 combinaciones, fortalezas ≥ 70 %, debilidades < 60 % y con al menos 3 respuestas")
    void analisis() {
        when(port.rendimiento(ID)).thenReturn(List.of(
                r("limites", "basico", 10, 9),        // 90 % fortaleza
                r("limites", "intermedio", 8, 6),     // 75 % fortaleza
                r("derivadas", "basico", 6, 4),       // 67 % ni una ni otra
                r("derivadas", "avanzado", 5, 1),     // 20 % debilidad
                r("integrales", "basico", 4, 2),      // 50 % debilidad
                r("integrales", "avanzado", 2, 0)));  // muy pocas: no cuenta

        AnalisisResponse a = servicio.analisis(CORREO);

        assertThat(a.detalle()).hasSize(9);
        assertThat(a.detalle().get(0)).isEqualTo(new AnalisisResponse.Rendimiento("limites", "basico", 10, 9, 90));
        assertThat(a.fortalezas()).extracting(AnalisisResponse.Rendimiento::porcentaje).containsExactly(90, 75);
        assertThat(a.debilidades()).extracting(x -> x.tema() + "/" + x.dificultad())
                .containsExactly("derivadas/avanzado", "integrales/basico");
        assertThat(a.recomendacion().tema()).isEqualTo("derivadas");
        assertThat(a.recomendacion().dificultad()).isEqualTo("avanzado");
        assertThat(a.recomendacion().motivo()).contains("Derivadas avanzado").contains("20 %");
    }

    @Test
    @DisplayName("Análisis sin respuestas: sin fortalezas, debilidades ni recomendación")
    void analisisVacio() {
        when(port.rendimiento(ID)).thenReturn(List.of());

        AnalisisResponse a = servicio.analisis(CORREO);

        assertThat(a.detalle()).hasSize(9).allMatch(x -> x.respondidas() == 0 && x.porcentaje() == 0);
        assertThat(a.fortalezas()).isEmpty();
        assertThat(a.debilidades()).isEmpty();
        assertThat(a.recomendacion()).isNull();
    }

    @Test
    @DisplayName("Sin debilidades recomienda el tema menos practicado: primero lo que no ha probado")
    void recomiendaLoQueFalta() {
        when(port.rendimiento(ID)).thenReturn(List.of(
                r("limites", "basico", 10, 9), r("derivadas", "basico", 5, 4), r("integrales", "basico", 1, 1)));

        AnalisisResponse.Recomendacion rec = servicio.analisis(CORREO).recomendacion();

        assertThat(rec.tema()).isEqualTo("integrales");
        assertThat(rec.dificultad()).isEqualTo("basico");
        assertThat(rec.motivo()).contains("pocas preguntas");
    }

    @Test
    @DisplayName("Si domina el nivel básico sube al siguiente; si nunca lo probó lo invita a empezar")
    void subeDeNivel() {
        when(port.rendimiento(ID)).thenReturn(List.of(
                r("limites", "basico", 5, 5), r("limites", "intermedio", 5, 5), r("limites", "avanzado", 5, 5),
                r("derivadas", "basico", 6, 6), r("derivadas", "intermedio", 6, 4),
                r("integrales", "basico", 9, 9), r("integrales", "intermedio", 9, 9)));

        AnalisisResponse.Recomendacion rec = servicio.analisis(CORREO).recomendacion();
        assertThat(rec.tema()).as("Límites es el menos practicado, pero ya lo domina").isEqualTo("derivadas");
        assertThat(rec.dificultad()).isEqualTo("intermedio");
        assertThat(rec.motivo()).contains("Vas bien").contains("67 %");

        when(port.rendimiento(ID)).thenReturn(List.of(r("limites", "basico", 5, 5), r("derivadas", "basico", 5, 5)));
        rec = servicio.analisis(CORREO).recomendacion();
        assertThat(rec.tema()).isEqualTo("integrales");
        assertThat(rec.motivo()).contains("Todavía no has probado Integrales básico");
    }

    @Test
    @DisplayName("Si domina todo, el reto es el avanzado con menor porcentaje")
    void dominaTodo() {
        when(port.rendimiento(ID)).thenReturn(List.of(
                r("limites", "basico", 5, 5), r("limites", "intermedio", 5, 5), r("limites", "avanzado", 5, 4),
                r("derivadas", "basico", 5, 5), r("derivadas", "intermedio", 5, 5), r("derivadas", "avanzado", 5, 5),
                r("integrales", "basico", 5, 5), r("integrales", "intermedio", 5, 5), r("integrales", "avanzado", 10, 9)));

        AnalisisResponse.Recomendacion rec = servicio.analisis(CORREO).recomendacion();

        assertThat(rec.tema()).isEqualTo("limites");
        assertThat(rec.dificultad()).isEqualTo("avanzado");
        assertThat(rec.motivo()).startsWith("¡Dominas todos los temas!");
    }

    @Test
    @DisplayName("Meta semanal: cuenta desde el lunes (hora de Colombia) y dice cuántos días quedan")
    void meta() {
        LocalDate lunes = LocalDate.of(2026, 11, 2);
        when(port.metaSemanal(ID)).thenReturn(20);
        when(port.actividadDesde(ID, lunes)).thenReturn(List.of(new Dia(lunes, 8, 6), new Dia(HOY, 5, 5)));

        MetaSemanalResponse m = servicio.meta(CORREO);

        assertThat(m.meta()).isEqualTo(20);
        assertThat(m.respondidas()).isEqualTo(13);
        assertThat(m.aciertos()).isEqualTo(11);
        assertThat(m.inicioSemana()).isEqualTo(lunes);
        assertThat(m.finSemana()).isEqualTo(LocalDate.of(2026, 11, 8));
        assertThat(m.diasRestantes()).as("miércoles a domingo").isEqualTo(5);
        assertThat(m.cumplida()).isFalse();
    }

    @Test
    @DisplayName("Cambiar la meta la guarda y la evalúa de una vez; fuera de 5 a 300 se rechaza")
    void cambiarMeta() {
        when(port.actividadDesde(eq(ID), any())).thenReturn(List.of(new Dia(HOY, 12, 9)));

        MetaSemanalResponse m = servicio.cambiarMeta(CORREO, 10);

        verify(port).guardarMetaSemanal(ID, 10);
        assertThat(m.cumplida()).isTrue();
        assertThatThrownBy(() -> servicio.cambiarMeta(CORREO, 4)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> servicio.cambiarMeta(CORREO, 301)).isInstanceOf(IllegalArgumentException.class);
        verify(port, never()).guardarMetaSemanal(anyLong(), eq(4));
    }

    @Test
    @DisplayName("Un correo que no existe lanza UsuarioNoEncontradoException")
    void usuarioInexistente() {
        assertThatThrownBy(() -> servicio.meta("nadie@ucundinamarca.edu.co")).isInstanceOf(UsuarioNoEncontradoException.class);
        verify(port, never()).metaSemanal(anyLong());
        verify(port, never()).guardarMetaSemanal(anyLong(), anyInt());
    }
}
