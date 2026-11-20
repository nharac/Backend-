package co.edu.ucundinamarca.saesu.duelo.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort.FilaDuelo;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class DueloJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final DueloJdbcAdapter adapter = new DueloJdbcAdapter(jdbc);

    @Test
    void mapeaLosRetosConEntregasNulas() {
        jdbc.filas("FROM duelos d", List.of(fila("id", 7L, "tema", "algebra", "retador_id", 1L, "retador", "Ana", "rival_id", 2L,
                "rival", "Beto", "creado_en", Timestamp.valueOf(LocalDateTime.of(2026, 11, 20, 8, 0)), "total", 5L,
                "correctas_retador", 3, "segundos_retador", 40, "correctas_rival", null, "segundos_rival", null)));

        List<FilaDuelo> d = adapter.duelosDe(1L, 30);

        assertThat(d.get(0).correctasRetador()).isEqualTo(3);
        assertThat(d.get(0).correctasRival()).isNull();
        assertThat(d.get(0).totalPreguntas()).isEqualTo(5);
        assertThat(jdbc.consultas.get(0).args()).containsExactly(1L, 1L, 30);
    }

    @Test
    void creaElRetoConSusPreguntasEnOrden() {
        jdbc.filas("INSERT INTO duelos", List.of(fila("id", 7L)));
        Long id = adapter.crear(1L, 2L, "algebra", List.of(10L, 11L), LocalDateTime.of(2026, 11, 20, 8, 0));
        assertThat(id).isEqualTo(7L);
        assertThat(jdbc.lotes.get(0)).hasSize(2);
        assertThat(jdbc.lotes.get(0).get(1)).containsExactly(7L, 2, 11L);
    }

    @Test
    void agrupaLasOpcionesPorPregunta() {
        jdbc.filas("FROM duelo_preguntas dp", List.of(
                fila("id", 10L, "tema", "algebra", "dificultad", "basico", "enunciado", "P", "pista", null, "letra", "A", "texto", "x", "es_correcta", true),
                fila("id", 10L, "tema", "algebra", "dificultad", "basico", "enunciado", "P", "pista", null, "letra", "B", "texto", "y", "es_correcta", false)));
        List<PreguntaQuiz> p = adapter.preguntas(7L);
        assertThat(p).hasSize(1);
        assertThat(p.get(0).opciones()).extracting(PreguntaQuiz.Opcion::letra).containsExactly("A", "B");
    }

    @Test
    void entregarUsaOnConflict() {
        jdbc.filasAfectadas = 0;
        assertThat(adapter.entregar(7L, 1L, 3, 40, LocalDateTime.of(2026, 11, 20, 8, 0))).isFalse();
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("ON CONFLICT");
    }
}
