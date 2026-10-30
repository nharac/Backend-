package co.edu.ucundinamarca.saesu.docente.adapters.output.jpa;

import co.edu.ucundinamarca.saesu.docente.adapters.output.jpa.adapter.DocenteRepositoryAdapter;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class DocenteRepositoryAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final DocenteRepositoryAdapter adapter = new DocenteRepositoryAdapter(jdbc);

    @Test
    void marcarYQuitarRespuestasOficiales() {
        adapter.marcarOficial(8L, 2L);
        adapter.quitarOficial(8L);
        adapter.quitarOficialesDePublicacion(1L);

        assertThat(jdbc.actualizaciones).hasSize(3);
        assertThat(jdbc.actualizaciones.get(0).args()).containsExactly(2L, 8L);
        assertThat(jdbc.actualizaciones.get(2).sql()).contains("publicacion_id = ?");
    }

    @Test
    void moderacionDevuelveSiAlgoCambio() {
        assertThat(adapter.ocultarRespuesta(8L, 2L)).isTrue();
        assertThat(adapter.ocultarPublicacion(1L, 2L)).isTrue();
        jdbc.filasAfectadas = 0;
        assertThat(adapter.restaurarRespuesta(8L)).isFalse();
        assertThat(adapter.restaurarPublicacion(1L)).isFalse();
    }

    @Test
    void contenidoOcultoConvierteFechasNulas() {
        LocalDateTime creada = LocalDateTime.of(2026, 10, 1, 10, 0);
        jdbc.filas("UNION ALL", List.of(
                fila("tipo", "PUBLICACION", "id", 1L, "publicacion_id", 1L, "titulo", "Spam", "contenido", "...",
                        "autor", "Pedro", "fecha_creacion", creada, "docente", "Profe", "fecha_oculta", creada.plusDays(1)),
                fila("tipo", "RESPUESTA", "id", 8L, "publicacion_id", 1L, "titulo", "Spam", "contenido", "x",
                        "autor", "Ana", "fecha_creacion", null, "docente", null, "fecha_oculta", null)));

        List<ContenidoOcultoResponse> lista = adapter.listarContenidoOculto();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).fechaOculta()).isEqualTo(creada.plusDays(1));
        assertThat(lista.get(1).fechaCreacion()).isNull();
        assertThat(lista.get(1).ocultadoPor()).isNull();
    }
}
