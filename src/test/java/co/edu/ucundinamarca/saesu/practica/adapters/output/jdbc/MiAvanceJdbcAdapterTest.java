package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class MiAvanceJdbcAdapterTest {

    @Test
    void actividadAgrupaPorDiaEnHoraDeColombia() {
        LocalDate dia = LocalDate.of(2026, 11, 3);
        JdbcFalso jdbc = new JdbcFalso().filas("AS dia", List.of(fila("dia", Date.valueOf(dia), "respondidas", 7L, "aciertos", 5L)));

        List<MiAvancePort.Dia> dias = new MiAvanceJdbcAdapter(jdbc).actividadDesde(4L, LocalDate.of(2026, 11, 1));

        assertThat(dias).containsExactly(new MiAvancePort.Dia(dia, 7, 5));
        assertThat(jdbc.consultas.get(0).sql()).contains("America/Bogota");
        assertThat(jdbc.consultas.get(0).args()).containsExactly(4L, Date.valueOf(LocalDate.of(2026, 11, 1)));
    }

    @Test
    void rendimientoPorTemaYDificultad() {
        JdbcFalso jdbc = new JdbcFalso().filas("GROUP BY p.tema, p.dificultad",
                List.of(fila("tema", "limites", "dificultad", "basico", "respondidas", 6L, "correctas", 4L)));

        assertThat(new MiAvanceJdbcAdapter(jdbc).rendimiento(4L))
                .containsExactly(new MiAvancePort.Rendimiento("limites", "basico", 6, 4));
    }

    @Test
    void metaSemanalLeerYGuardar() {
        JdbcFalso jdbc = new JdbcFalso();
        MiAvanceJdbcAdapter adapter = new MiAvanceJdbcAdapter(jdbc);
        assertThat(adapter.metaSemanal(99L)).as("usuario que no existe").isZero();

        jdbc.filas("meta_semanal FROM usuarios", List.of(fila("meta_semanal", 30)));
        assertThat(adapter.metaSemanal(4L)).isEqualTo(30);

        adapter.guardarMetaSemanal(4L, 45);
        assertThat(jdbc.actualizaciones.get(0).args()).containsExactly(45, 4L);
    }
}
