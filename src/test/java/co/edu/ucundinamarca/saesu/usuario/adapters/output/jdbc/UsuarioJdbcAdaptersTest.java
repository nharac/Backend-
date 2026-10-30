package co.edu.ucundinamarca.saesu.usuario.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort.CodigoActivo;
import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort.Pendiente;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioJdbcAdaptersTest {

    @Test
    void codigoDeRecuperacion() {
        JdbcFalso jdbc = new JdbcFalso();
        CodigoRecuperacionJdbcAdapter adapter = new CodigoRecuperacionJdbcAdapter(jdbc);

        assertThat(adapter.buscarActivo(3L, 5)).isEmpty();
        assertThat(adapter.fechaUltimoCodigo(3L)).isEmpty();

        LocalDateTime hace = LocalDateTime.of(2026, 10, 29, 20, 0);
        jdbc.filas("codigo_hash, intentos", List.of(fila("id", 8L, "codigo_hash", "$2a$hash", "intentos", 2)))
            .filas("MAX(creado_en)", List.of(fila("f", hace)));
        assertThat(adapter.buscarActivo(3L, 5)).contains(new CodigoActivo(8L, "$2a$hash", 2));
        assertThat(adapter.fechaUltimoCodigo(3L)).contains(hace);

        adapter.invalidarActivos(3L);
        adapter.guardar(3L, "$2a$otro", 15);
        adapter.sumarIntento(8L);
        adapter.marcarUsado(8L);
        adapter.actualizarPassword(3L, "$2a$nueva");
        assertThat(jdbc.actualizaciones).hasSize(5);
        assertThat(jdbc.actualizaciones.get(1).args()).containsExactly(3L, "$2a$otro", 15);
        assertThat(jdbc.actualizaciones.get(4).args()).containsExactly("$2a$nueva", 3L);
    }

    @Test
    void codigoSinFechaSeIgnora() {
        JdbcFalso jdbc = new JdbcFalso().filas("MAX(creado_en)", List.of(fila("f", null)));
        assertThat(new CodigoRecuperacionJdbcAdapter(jdbc).fechaUltimoCodigo(3L)).isEmpty();
    }

    @Test
    void registroPendiente() {
        JdbcFalso jdbc = new JdbcFalso();
        RegistroPendienteJdbcAdapter adapter = new RegistroPendienteJdbcAdapter(jdbc);
        assertThat(adapter.buscar("x@ucundinamarca.edu.co")).isEmpty();

        jdbc.filas("FROM registros_pendientes", List.of(fila("id", 4L, "correo", "ana@ucundinamarca.edu.co",
                "nombre", "Ana", "password_hash", "p", "codigo_hash", "c", "intentos", 1, "vigente", true, "segundos", 75L)));
        Pendiente p = adapter.buscar("ANA@ucundinamarca.edu.co").orElseThrow();
        assertThat(p.vigente()).isTrue();
        assertThat(p.segundosDesdeEnvio()).isEqualTo(75L);

        adapter.guardar("ana@ucundinamarca.edu.co", "Ana", "p", "c", 10);
        adapter.renovarCodigo(4L, "c2", 10);
        adapter.sumarIntento(4L);
        adapter.eliminar(4L);
        adapter.limpiarAbandonados();
        assertThat(jdbc.actualizaciones).hasSize(5);
        assertThat(jdbc.actualizaciones.get(1).args()).containsExactly("c2", 10, 4L);
    }
}
