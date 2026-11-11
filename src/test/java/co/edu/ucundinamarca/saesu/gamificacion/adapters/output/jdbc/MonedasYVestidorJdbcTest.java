package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort.UsuarioBasico;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

/** Adaptadores JDBC de las Sumi Monedas y del vestidor. */
class MonedasYVestidorJdbcTest {

    @Test
    void monedas() {
        LocalDateTime ayer = LocalDateTime.now().minusDays(1);
        JdbcFalso jdbc = new JdbcFalso()
                .filas("SELECT monedas FROM usuarios", List.of(fila("monedas", 42)))
                .filas("FROM monedas_historial", List.of(fila("cantidad", -20, "motivo", "Compra", "fecha", ayer)));
        MonedasJdbcAdapter adapter = new MonedasJdbcAdapter(jdbc);

        adapter.sumar(4L, 3, "Respondió en el foro");
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("monedas = monedas + ?");
        assertThat(jdbc.actualizaciones.get(1).args()).containsExactly(4L, 3, "Respondió en el foro");

        assertThat(adapter.gastar(4L, 20, "x".repeat(200))).isTrue();
        assertThat(jdbc.actualizaciones.get(2).sql()).contains("AND monedas >= ?");
        assertThat(jdbc.actualizaciones.get(3).args()[1]).isEqualTo(-20);
        assertThat((String) jdbc.actualizaciones.get(3).args()[2]).hasSize(150);

        jdbc.filasAfectadas = 0;
        assertThat(adapter.gastar(4L, 999, "Compra")).isFalse();
        assertThat(jdbc.actualizaciones).hasSize(5); // no se escribe historial si no alcanzó

        assertThat(adapter.saldo(4L)).isEqualTo(42);
        assertThat(adapter.historial(4L, 30)).singleElement()
                .satisfies(m -> assertThat(m.cantidad()).isEqualTo(-20));
    }

    @Test
    void monedasSinUsuario() {
        assertThat(new MonedasJdbcAdapter(new JdbcFalso()).saldo(9L)).isZero();
    }

    @Test
    void vestidor() {
        JdbcFalso jdbc = new JdbcFalso()
                .filas("SELECT id, rol FROM usuarios", List.of(fila("id", 4L, "rol", "ESTUDIANTE")))
                .filas("FROM sumi_catalogo", List.of(fila("precio", 40)))
                .filas("FROM sumi_inventario", List.of(fila("categoria", "sombrero", "prenda", "gorra")))
                .filas("FROM sumi_estilo", List.of(fila("sombrero", "gorra", "cara", "ninguno", "ropa", "toga-azul", "objeto", "cafe",
                        "espalda", "ninguno", "bufanda", "saesu", "fondo", "playa")));
        VestidorJdbcAdapter adapter = new VestidorJdbcAdapter(jdbc);

        assertThat(adapter.usuario("Ana@U.edu.co")).isEqualTo(new UsuarioBasico(4L, "ESTUDIANTE"));
        assertThat(adapter.precio("sombrero", "gorra")).isEqualTo(40);
        assertThat(adapter.inventario(4L)).containsExactly("sombrero:gorra");
        assertThat(adapter.estilo(4L)).containsEntry("fondo", "playa").containsEntry("objeto", "cafe").containsEntry("ropa", "toga-azul");

        assertThat(adapter.agregar(4L, "sombrero", "corona")).isTrue();
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("ON CONFLICT DO NOTHING");
        adapter.guardarEstilo(4L, Map.of("sombrero", "gorra", "cara", "sol", "ropa", "smoking-rojo", "objeto", "pc",
                "espalda", "capa", "bufanda", "oro", "fondo", "playa"));
        assertThat(jdbc.actualizaciones.get(1).args()).containsExactly(4L, "gorra", "sol", "smoking-rojo", "pc", "capa", "oro", "playa");
    }

    @Test
    void vestidorVacio() {
        VestidorJdbcAdapter adapter = new VestidorJdbcAdapter(new JdbcFalso());
        assertThat(adapter.usuario("nadie@u.edu.co")).isNull();
        assertThat(adapter.precio("sombrero", "capa")).isNull();
        assertThat(adapter.estilo(4L)).isEmpty();
        assertThat(new UsuarioBasico(1L, "ROLE_DOCENTE").esDocente()).isTrue();
        assertThat(new UsuarioBasico(1L, null).esDocente()).isFalse();
    }
}
