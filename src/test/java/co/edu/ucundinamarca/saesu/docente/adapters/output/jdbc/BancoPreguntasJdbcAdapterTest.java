package co.edu.ucundinamarca.saesu.docente.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest.OpcionRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class BancoPreguntasJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final BancoPreguntasJdbcAdapter adapter = new BancoPreguntasJdbcAdapter(jdbc);

    private static PreguntaDocenteRequest request(String pista) {
        return new PreguntaDocenteRequest("derivadas", "  Deriva x²  ", "basico", pista, List.of(
                new OpcionRequest(" a ", " 2x ", true), new OpcionRequest("B", "x", false),
                new OpcionRequest("C", "2", false), new OpcionRequest("D", "x²", false)));
    }

    @Test
    void listarJuntaCadaPreguntaConSusOpcionesYElPorcentaje() {
        jdbc.filas("FROM preguntas p", List.of(
                        fila("id", 7L, "tema", "derivadas", "enunciado", "Deriva x²", "dificultad", "basico", "pista", null,
                                "activa", true, "intentos", 8L, "correctas", 6L, "fecha_actualizacion", LocalDateTime.of(2026, 10, 1, 8, 0)),
                        fila("id", 9L, "tema", "limites", "enunciado", "lim", "dificultad", "avanzado", "pista", "p",
                                "activa", false, "intentos", 0L, "correctas", 0L, "fecha_actualizacion", null)))
            .filas("FROM opciones", List.of(
                        fila("id", 1L, "pregunta_id", 7L, "letra", "A ", "texto", "2x", "es_correcta", true),
                        fila("id", 2L, "pregunta_id", 7L, "letra", "B", "texto", "x", "es_correcta", false)));

        List<PreguntaDocenteResponse> lista = adapter.listar("derivadas", "basico", " Deriva ", true);

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).porcentajeAcierto()).isEqualTo(75.0);
        assertThat(lista.get(0).opciones()).extracting(PreguntaDocenteResponse.OpcionDocente::letra).containsExactly("A", "B");
        assertThat(lista.get(1).opciones()).isEmpty();
        assertThat(lista.get(1).fechaActualizacion()).isNull();
        JdbcFalso.Llamada consulta = jdbc.consultas.get(0);
        assertThat(consulta.args()).containsExactly("derivadas", "basico", "%deriva%", true);
        assertThat(jdbc.consultas.get(1).sql()).contains("IN (?, ?)");
    }

    @Test
    void sinResultadosNoConsultaOpcionesYBuscarPorId() {
        assertThat(adapter.listar(null, " ", null, null)).isEmpty();
        assertThat(jdbc.consultas).hasSize(1);
        assertThat(adapter.buscarPorId(7L)).isEmpty();
        assertThat(jdbc.consultas.get(1).args()).containsExactly(7L);
    }

    @Test
    void existeEnunciado() {
        jdbc.filas("LOWER(TRIM(enunciado))", List.of(fila("count", 1L)));
        assertThat(adapter.existeEnunciado("Deriva x²", null)).isTrue();
        assertThat(jdbc.consultas.get(0).args()).containsExactly("Deriva x²", -1L);
        assertThat(adapter.existeEnunciado("Deriva x²", 7L)).isTrue();
    }

    @Test
    void crearLimpiaLosTextosEInsertaLasCuatroOpciones() {
        jdbc.filas("RETURNING id", List.of(fila("id", 33L)));

        assertThat(adapter.crear(request("  "), 2L)).isEqualTo(33L);

        assertThat(jdbc.consultas.get(0).args()).containsExactly("derivadas", "Deriva x²", "basico", null, 2L);
        assertThat(jdbc.actualizaciones).hasSize(4);
        assertThat(jdbc.actualizaciones.get(0).args()).containsExactly(33L, "A", "2x", true);
    }

    @Test
    void actualizarEditaPorLetraEInsertaLasQueNoExisten() {
        adapter.actualizar(7L, request(" Usa la regla de la potencia "));
        assertThat(jdbc.actualizaciones).hasSize(5);
        assertThat(jdbc.actualizaciones.get(0).args()[3]).isEqualTo("Usa la regla de la potencia");

        jdbc.actualizaciones.clear();
        jdbc.filasAfectadas = 0;
        adapter.actualizar(7L, request(null));
        assertThat(jdbc.actualizaciones).hasSize(9); // 1 UPDATE + 4 UPDATE sin efecto + 4 INSERT
        assertThat(adapter.cambiarEstado(7L, false)).isFalse();
    }
}
