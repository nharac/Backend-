package co.edu.ucundinamarca.saesu.quiz.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.quiz.domain.model.IntentoQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort.Eleccion;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort.Totales;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class QuizJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final QuizJdbcAdapter adapter = new QuizJdbcAdapter(jdbc);

    private static List<java.util.Map<String, Object>> opcionesDeDosPreguntas() {
        return List.of(
                fila("id", 7L, "tema", "limites", "dificultad", "basico", "enunciado", "lim x->0", "pista", "factoriza",
                        "letra", "A", "texto", "0", "es_correcta", true),
                fila("id", 7L, "tema", "limites", "dificultad", "basico", "enunciado", "lim x->0", "pista", "factoriza",
                        "letra", "B", "texto", "1", "es_correcta", false),
                fila("id", 9L, "tema", "limites", "dificultad", "avanzado", "enunciado", "lim x->inf", "pista", null,
                        "letra", "A", "texto", "inf", "es_correcta", true));
    }

    @Test
    void preguntasAlAzarAgrupaLasOpcionesPorPregunta() {
        jdbc.filas("random()", opcionesDeDosPreguntas());

        List<PreguntaQuiz> preguntas = adapter.preguntasAlAzar("limites", 2);

        assertThat(preguntas).extracting(PreguntaQuiz::id).containsExactly(7L, 9L);
        assertThat(preguntas.get(0).opciones()).extracting(PreguntaQuiz.Opcion::letra).containsExactly("A", "B");
        assertThat(preguntas.get(0).opciones().get(0).correcta()).isTrue();
        assertThat(jdbc.consultas.get(0).args()).containsExactly("limites", "limites", 2);
    }

    @Test
    void preguntasDelIntentoUsaElMismoAgrupamiento() {
        jdbc.filas("quiz_preguntas q", opcionesDeDosPreguntas());
        assertThat(adapter.preguntasDelIntento(5L)).hasSize(2);
    }

    @Test
    void crearIntentoInsertaElIntentoYSusPreguntasEnOrden() {
        jdbc.llaveGenerada = 42L;
        Instant ahora = Instant.parse("2026-10-30T14:00:00Z");

        Long id = adapter.crearIntento(3L, "derivadas", ahora, ahora.plusSeconds(600), List.of(11L, 12L, 13L));

        assertThat(id).isEqualTo(42L);
        List<Object[]> filas = jdbc.lotes.get(0);
        assertThat(filas).hasSize(3);
        assertThat(filas.get(2)).containsExactly(42L, 3, 13L);
    }

    @Test
    void buscarIntentoConvierteLasFechasYLosNulos() {
        OffsetDateTime inicio = OffsetDateTime.of(2026, 10, 30, 9, 0, 0, 0, ZoneOffset.UTC);
        jdbc.filas("FROM quiz_intentos WHERE id", List.of(fila("id", 5L, "usuario_id", 3L, "tema", null, "cantidad", 10,
                "iniciado_en", inicio, "limite_en", inicio.plusMinutes(15), "finalizado_en", null,
                "correctas", null, "fuera_de_tiempo", false)));

        Optional<IntentoQuiz> intento = adapter.buscarIntento(5L);

        assertThat(intento).isPresent();
        assertThat(intento.get().iniciadoEn()).isEqualTo(inicio.toInstant());
        assertThat(intento.get().finalizadoEn()).isNull();
        assertThat(intento.get().correctas()).isNull();
        assertThat(adapter.buscarIntento(6L)).isPresent(); // misma fila registrada para cualquier id
    }

    @Test
    void finalizarGuardaLasEleccionesSoloSiElIntentoSeguiaAbierto() {
        List<Eleccion> elecciones = List.of(new Eleccion(11L, "A", true), new Eleccion(12L, "C", false));

        assertThat(adapter.finalizar(5L, Instant.now(), 1, false, elecciones)).isTrue();
        assertThat(jdbc.lotes.get(0)).hasSize(2);
        assertThat(jdbc.lotes.get(0).get(1)).containsExactly("C", false, 5L, 12L);

        jdbc.filasAfectadas = 0;
        assertThat(adapter.finalizar(5L, Instant.now(), 1, false, elecciones)).isFalse();
        assertThat(jdbc.lotes).hasSize(1);
    }

    @Test
    void eleccionesHistorialYTotales() {
        jdbc.filas("letra_elegida", List.of(fila("pregunta_id", 11L, "letra_elegida", "B", "es_correcta", true)))
            .filas("ORDER BY finalizado_en DESC", List.of(fila("id", 1L, "usuario_id", 3L, "tema", "integrales",
                    "cantidad", 5, "iniciado_en", null, "limite_en", null,
                    "finalizado_en", OffsetDateTime.now(ZoneOffset.UTC), "correctas", 4, "fuera_de_tiempo", true)))
            .filas("COUNT(*) AS intentos", List.of(fila("intentos", 3L, "promedio", 66.6, "mejor", 100.0)));

        assertThat(adapter.eleccionesDe(5L)).containsExactly(new Eleccion(11L, "B", true));
        assertThat(adapter.historial(3L, 10)).singleElement()
                .satisfies(i -> {
                    assertThat(i.correctas()).isEqualTo(4);
                    assertThat(i.fueraDeTiempo()).isTrue();
                });
        Totales t = adapter.totales(3L);
        assertThat(t.intentos()).isEqualTo(3);
        assertThat(t.mejorPorcentaje()).isEqualTo(100.0);
    }
}
