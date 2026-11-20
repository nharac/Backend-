package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.AlertaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaEstudiante;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaTemaEstudiante;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertasDocenteServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 10, 0);

    @Mock private EstadisticasDocentePort port;
    private AlertasDocenteService servicio;

    @BeforeEach
    void preparar() {
        servicio = new AlertasDocenteService(port, Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA), BOGOTA);
    }

    private static FilaEstudiante est(long id, String nombre, LocalDateTime ultima, LocalDateTime registro,
                                      long quices, Double promedio) {
        return new FilaEstudiante(id, nombre, id + "@u.edu.co", null, 0, 0, 10, 5, ultima, registro, quices, promedio, null);
    }

    @Test
    @DisplayName("Genera alertas de inactividad, sin empezar, tema difícil, quiz bajo y del grupo")
    void todasLasAlertas() {
        when(port.listarEstudiantes()).thenReturn(List.of(
                est(1, "Ana", AHORA.minusDays(10), AHORA.minusMonths(1), 0, null),
                est(2, "Beto", null, AHORA.minusDays(9), 0, null),
                est(3, "Caro", AHORA.minusDays(1), AHORA.minusMonths(1), 3, 40.0),
                est(4, "Dani", null, AHORA.minusDays(2), 0, null)));
        when(port.progresoPorTema(isNull())).thenReturn(List.of(
                new FilaTemaEstudiante(3L, "algebra", 12, 3, 3),
                new FilaTemaEstudiante(3L, "limites", 12, 10, 8)));
        when(port.estadisticasPorTema()).thenReturn(List.of(
                new TemaEstadistica("trigonometria", 30, 9, 30.0, 4, 20),
                new TemaEstadistica("derivadas", 5, 0, 0.0, 1, 20)));

        List<AlertaDocenteResponse> a = servicio.alertas();

        assertThat(a).extracting(AlertaDocenteResponse::tipo)
                .containsExactly("INACTIVO", "SIN_EMPEZAR", "GRUPO_TEMA", "TEMA_DIFICIL", "QUIZ_BAJO");
        assertThat(a.get(0).mensaje()).contains("10 días");
        assertThat(a.get(3).tema()).isEqualTo("algebra");
        assertThat(a.get(3).mensaje()).contains("25.0 %").contains("Álgebra");
    }

    @Test
    @DisplayName("Sin problemas no hay alertas")
    void sinAlertas() {
        when(port.listarEstudiantes()).thenReturn(List.of(est(1, "Ana", AHORA.minusDays(1), AHORA.minusMonths(1), 1, 90.0)));
        when(port.progresoPorTema(isNull())).thenReturn(List.of());
        when(port.estadisticasPorTema()).thenReturn(List.of());
        assertThat(servicio.alertas()).isEmpty();
    }
}
