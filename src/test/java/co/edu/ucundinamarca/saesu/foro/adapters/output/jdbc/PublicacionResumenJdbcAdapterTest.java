package co.edu.ucundinamarca.saesu.foro.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.foro.domain.model.PublicacionResumen;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class PublicacionResumenJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso().filas("FROM publicaciones p", List.of(
            fila("id", 1L, "titulo", "Límite lateral", "inicio", "¿Cómo se calcula...", "tema", "limites",
                    "fecha_creacion", LocalDateTime.of(2026, 10, 28, 8, 0), "imagen_url", null,
                    "autor_id", 4L, "autor_nombre", "Ana", "autor_rol", "ESTUDIANTE", "respuestas", 3L),
            fila("id", 2L, "titulo", "Sin fecha", "inicio", "x", "tema", null, "fecha_creacion", null,
                    "imagen_url", "https://img/2.png", "autor_id", 5L, "autor_nombre", "Leo", "autor_rol", "DOCENTE",
                    "respuestas", 0L)));
    private final PublicacionResumenJdbcAdapter adapter = new PublicacionResumenJdbcAdapter(jdbc);

    @Test
    void listarConFiltrosArmaElSqlYLosParametros() {
        List<PublicacionResumen> lista = adapter.listar("limites", " 50%_off ", "respuestas", 2, 500);

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).respuestas()).isEqualTo(3L);
        assertThat(lista.get(1).fechaCreacion()).isNull();
        JdbcFalso.Llamada llamada = jdbc.consultas.get(0);
        assertThat(llamada.sql()).contains("p.tema = ?", "ILIKE", "respuestas DESC");
        assertThat(llamada.args()).containsExactly("limites", "%50\\%\\_off%", "%50\\%\\_off%", 100, 200L);
    }

    @Test
    void sinFiltrosYOrdenAntiguoOPorDefecto() {
        adapter.listar(null, "  ", "antiguo", -1, 0);
        adapter.listar("", null, null, 0, 10);

        assertThat(jdbc.consultas.get(0).sql()).contains("p.fecha_creacion ASC").doesNotContain("ILIKE", "p.tema = ?");
        assertThat(jdbc.consultas.get(0).args()).containsExactly(1, 0L);
        assertThat(jdbc.consultas.get(1).sql()).contains("p.fecha_creacion DESC");
    }

    @Test
    void sinRespuestaOficialYEscaparLike() {
        assertThat(adapter.listarSinRespuestaOficial()).hasSize(2);
        assertThat(PublicacionResumenJdbcAdapter.escaparLike("a\\b")).isEqualTo("a\\\\b");
    }
}
