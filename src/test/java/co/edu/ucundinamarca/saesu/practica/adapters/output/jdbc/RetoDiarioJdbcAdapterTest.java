package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort.Intento;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

/** Adaptador JDBC del reto diario. */
class RetoDiarioJdbcAdapterTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 8);

    @Test
    void preguntaDelDiaDaLaVueltaAlBanco() {
        JdbcFalso jdbc = new JdbcFalso()
                .filas("COUNT(*)", List.of(fila("total", 200L)))
                .filas("SELECT id FROM preguntas", List.of(fila("id", 57L)));
        RetoDiarioJdbcAdapter adapter = new RetoDiarioJdbcAdapter(jdbc);

        assertThat(adapter.preguntaDelDia(20_765L)).contains(57L);
        assertThat(jdbc.consultas.get(1).args()).containsExactly(165L); // 20765 % 200
    }

    @Test
    void sinPreguntasNoHayReto() {
        assertThat(new RetoDiarioJdbcAdapter(new JdbcFalso().filas("COUNT(*)", List.of(fila("total", 0L))))
                .preguntaDelDia(5)).isEmpty();
        assertThat(new RetoDiarioJdbcAdapter(new JdbcFalso()).preguntaDelDia(5)).isEmpty();
    }

    @Test
    void intentoYRegistroUnico() {
        JdbcFalso jdbc = new JdbcFalso()
                .filas("FROM reto_diario_intentos", List.of(fila("pregunta_id", 57L, "opcion_id", 300L, "correcta", true)));
        RetoDiarioJdbcAdapter adapter = new RetoDiarioJdbcAdapter(jdbc);

        assertThat(adapter.intento(4L, HOY)).contains(new Intento(57L, 300L, true));

        assertThat(adapter.registrar(4L, HOY, new Intento(57L, 301L, false))).isTrue();
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("ON CONFLICT (usuario_id, fecha) DO NOTHING");
        assertThat(jdbc.actualizaciones.get(0).args()).containsExactly(4L, java.sql.Date.valueOf(HOY), 57L, 301L, false);

        jdbc.filasAfectadas = 0;
        assertThat(adapter.registrar(4L, HOY, new Intento(57L, 300L, true))).isFalse();
    }
}
