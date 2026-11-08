package co.edu.ucundinamarca.saesu.foro.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class UtilesJdbcAdapterTest {

    @Test
    void consultasDeMeSirvio() {
        JdbcFalso jdbc = new JdbcFalso()
                .filas("FROM usuarios", List.of(fila("id", 4L)))
                .filas("oculta_por IS NULL", List.of(fila("count", 1)))
                .filas("GROUP BY ru.respuesta_id", List.of(fila("respuesta_id", 7L, "total", 3L), fila("respuesta_id", 8L, "total", 1L)))
                .filas("ru.usuario_id = ?", List.of(fila("respuesta_id", 7L)))
                .filas("COUNT(*) FROM respuestas_utiles WHERE", List.of(fila("count", 3L)));
        UtilesJdbcAdapter adapter = new UtilesJdbcAdapter(jdbc);

        assertThat(adapter.idUsuario("Ana@U.edu.co")).isEqualTo(4L);
        assertThat(adapter.respuestaVisible(7L)).isTrue();
        assertThat(adapter.contarPorPublicacion(1L)).isEqualTo(Map.of(7L, 3L, 8L, 1L));
        assertThat(adapter.marcadasPor(1L, 4L)).containsExactly(7L);
        assertThat(adapter.contar(7L)).isEqualTo(3L);

        assertThat(adapter.marcar(7L, 4L)).isTrue();
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("ON CONFLICT DO NOTHING");
        jdbc.filasAfectadas = 0;
        assertThat(adapter.desmarcar(7L, 4L)).isFalse();
    }

    @Test
    void sinDatos() {
        JdbcFalso jdbc = new JdbcFalso().filas("oculta_por IS NULL", List.of(fila("count", 0)));
        UtilesJdbcAdapter adapter = new UtilesJdbcAdapter(jdbc);
        assertThat(adapter.idUsuario("nadie@u.edu.co")).isNull();
        assertThat(adapter.respuestaVisible(9L)).isFalse();
        assertThat(adapter.contarPorPublicacion(1L)).isEmpty();
    }
}
