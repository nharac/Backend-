package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort.FilaRanking;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort.Movimiento;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort.PosicionUsuario;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.RankingPort.ResumenPuntos;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class RankingJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final RankingJdbcAdapter adapter = new RankingJdbcAdapter(jdbc);

    @Test
    void topDesdeElInicioCuandoNoHayFecha() {
        jdbc.filas("ORDER BY posicion LIMIT", List.of(
                fila("posicion", 1, "id", 3L, "nombre", "Laura", "foto_perfil_url", null, "puntos", 640L, "racha", 5)));

        List<FilaRanking> top = adapter.top(null, 10);

        assertThat(top).containsExactly(new FilaRanking(1, 3L, "Laura", null, 640L, 5));
        assertThat(jdbc.consultas.get(0).args()[0]).isEqualTo(Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 0, 0)));
    }

    @Test
    void posicionDelPrimeroNoTieneANadieArriba() {
        jdbc.filas("WHERE id = ?", List.of(fila("posicion", 1, "puntos", 640L, "participantes", 12L,
                "puntos_arriba", null, "nombre_arriba", null)));

        PosicionUsuario p = adapter.posicionDe(3L, LocalDateTime.of(2026, 10, 27, 0, 0)).orElseThrow();

        assertThat(p.participantes()).isEqualTo(12L);
        assertThat(p.puntosDelDeArriba()).isNull();
    }

    @Test
    void participantesResumenYMovimientos() {
        jdbc.filas("SELECT COUNT(*) FROM ordenado", List.of(fila("count", 12L)))
            .filas("AS semana", List.of(fila("racha", 4, "hoy", 20L, "semana", 140L, "total", 640L)))
            .filas("FROM puntos_historial\n", List.of(fila("cantidad", 20, "motivo", "Respuesta correcta",
                    "fecha", LocalDateTime.of(2026, 10, 29, 19, 0))));

        assertThat(adapter.participantes(null)).isEqualTo(12L);
        assertThat(adapter.resumenPuntos(3L, LocalDateTime.now(), LocalDateTime.now().minusDays(3)))
                .isEqualTo(new ResumenPuntos(4, 20, 140, 640));
        assertThat(adapter.ultimosMovimientos(3L, 5)).singleElement().extracting(Movimiento::motivo).isEqualTo("Respuesta correcta");
    }

    @Test
    void usuarioSinPuntosTieneResumenEnCero() {
        assertThat(adapter.resumenPuntos(99L, null, null)).isEqualTo(new ResumenPuntos(0, 0, 0, 0));
        assertThat(adapter.posicionDe(99L, null)).isEmpty();
    }
}
