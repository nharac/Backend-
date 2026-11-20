package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.RepasoEspaciadoPort.Resumen;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class RepasoEspaciadoJdbcAdapterTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 20);
    private final JdbcFalso jdbc = new JdbcFalso();
    private final RepasoEspaciadoJdbcAdapter adapter = new RepasoEspaciadoJdbcAdapter(jdbc);

    @Test
    void unErrorVuelveMananaALaCaja1() {
        adapter.fallo(1L, 7L, HOY);
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("ON CONFLICT").contains("caja = 1");
        assertThat(jdbc.actualizaciones.get(0).args()).containsExactly(1L, 7L, Date.valueOf(HOY.plusDays(1)));
    }

    @Test
    void acertarCuandoTocabaSubeDeCaja() {
        adapter.acierto(1L, 7L, HOY);
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("caja = caja + 1").contains("proxima <= ?");
        assertThat(jdbc.actualizaciones.get(0).args())
                .containsExactly(Date.valueOf(HOY.plusDays(3)), Date.valueOf(HOY.plusDays(7)), 1L, 7L, Date.valueOf(HOY));
    }

    @Test
    void resumen() {
        jdbc.filas("FROM repaso_espaciado", List.of(fila("hoy", 3L, "manana", 2L, "semana", 4L, "dominadas", 5L, "en_repaso", 9L)));
        Resumen r = adapter.resumen(1L, HOY);
        assertThat(r.paraHoy()).isEqualTo(3);
        assertThat(r.dominadas()).isEqualTo(5);
    }
}
