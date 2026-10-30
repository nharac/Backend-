package co.edu.ucundinamarca.saesu.docente.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaEstudiante;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class EstadisticasDocenteJdbcAdapterTest {

    private static final LocalDateTime AYER = LocalDateTime.of(2026, 10, 29, 18, 0);

    private final JdbcFalso jdbc = new JdbcFalso();
    private final EstadisticasDocenteJdbcAdapter adapter = new EstadisticasDocenteJdbcAdapter(jdbc);

    @Test
    void estudiantesConYSinQuices() {
        jdbc.filas("LEFT JOIN LATERAL", List.of(
                fila("id", 3L, "nombre", "Laura", "correo", "laura@ucundinamarca.edu.co", "foto_perfil_url", null,
                        "puntos", 640L, "racha", 5, "intentos", 20L, "correctas", 15L, "ultima", AYER,
                        "fecha_registro", AYER.minusDays(30), "quices", 2L, "promedio_quiz", 80.0, "ultimo_quiz", AYER),
                fila("id", 4L, "nombre", "Pedro", "correo", "p@ucundinamarca.edu.co", "foto_perfil_url", null,
                        "puntos", 0L, "racha", 0, "intentos", 0L, "correctas", 0L, "ultima", null,
                        "fecha_registro", AYER, "quices", 0L, "promedio_quiz", null, "ultimo_quiz", null)));

        List<FilaEstudiante> lista = adapter.listarEstudiantes();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).promedioQuiz()).isEqualTo(80.0);
        assertThat(lista.get(1).promedioQuiz()).isNull();
        assertThat(lista.get(1).ultimaActividad()).isNull();
        assertThat(jdbc.consultas.get(0).sql()).contains("ORDER BY LOWER(u.nombre)").doesNotContain("%s");
        assertThat(adapter.buscarEstudiante(3L)).isPresent();
        assertThat(jdbc.consultas.get(1).sql()).contains("AND u.id = ?");
    }

    @Test
    void progresoDeTodosODeUnEstudiante() {
        jdbc.filas("resueltas", List.of(fila("usuario_id", 3L, "tema", "limites", "intentos", 10L, "correctas", 7L, "resueltas", 5L)));

        assertThat(adapter.progresoPorTema(null)).singleElement().satisfies(f -> assertThat(f.resueltas()).isEqualTo(5L));
        assertThat(adapter.progresoPorTema(3L)).hasSize(1);
        assertThat(jdbc.consultas.get(0).sql()).doesNotContain("r.usuario_id = ?");
        assertThat(jdbc.consultas.get(1).args()).containsExactly(3L);
    }

    @Test
    void resumenDelGrupo() {
        jdbc.filas("GROUP BY tema", List.of(fila("tema", "limites", "n", 10L)))
            .filas("AS activos", List.of(fila("activos", 4L, "intentos", 60L)))
            .filas("AS t(tema)", List.of(fila("tema", "limites", "intentos", 8L, "correctas", 6L, "estudiantes", 3L, "activas", 10L),
                    fila("tema", "integrales", "intentos", 0L, "correctas", 0L, "estudiantes", 0L, "activas", 4L)))
            .filas("error_frecuente", List.of(fila("id", 7L, "enunciado", "Deriva", "tema", "derivadas", "dificultad", "basico",
                    "intentos", 4L, "fallos", 3L, "error_frecuente", "x")));

        assertThat(adapter.preguntasActivasPorTema()).containsEntry("limites", 10L);
        assertThat(adapter.resumenSemana().intentos()).isEqualTo(60L);
        assertThat(adapter.estadisticasPorTema()).extracting(t -> t.porcentaje()).containsExactly(75.0, 0.0);
        assertThat(adapter.preguntasDificiles(5)).singleElement().satisfies(p -> assertThat(p.porcentajeFallo()).isEqualTo(75.0));
    }

    @Test
    void actividadDelGrupoODeUnEstudiante() {
        jdbc.filas("generate_series", List.of(fila("fecha", LocalDate.of(2026, 10, 29), "intentos", 5L, "estudiantes", 2L)));

        assertThat(adapter.actividad(7, null)).singleElement().satisfies(d -> assertThat(d.fecha()).isEqualTo(LocalDate.of(2026, 10, 29)));
        adapter.actividad(0, 3L);

        assertThat(jdbc.consultas.get(0).args()).containsExactly(6);
        assertThat(jdbc.consultas.get(1).args()).containsExactly(0, 3L);
        assertThat(jdbc.consultas.get(1).sql()).contains("AND r.usuario_id = ?").doesNotContain("%");
    }

    @Test
    void detalleDeUnEstudiante() {
        jdbc.filas("o.letra || ') '", List.of(fila("id", 7L, "enunciado", "Deriva", "tema", "derivadas", "opcion", "A) 2x",
                        "es_correcta", true, "fecha_intento", AYER)))
            .filas("BOOL_OR", List.of(fila("id", 7L, "enunciado", "Deriva", "tema", "derivadas", "fallos", 2L, "resuelta", true)))
            .filas("AS logros", List.of(fila("publicaciones", 1L, "respuestas", 4L, "oficiales", 1L, "logros", 3L)))
            .filas("FROM quiz_intentos", List.of(fila("id", 1L, "tema", null, "correctas", 4, "cantidad", 5,
                    "iniciado_en", AYER.minusMinutes(10), "finalizado_en", AYER, "fuera_de_tiempo", false)));

        assertThat(adapter.ultimosIntentos(3L, 10)).singleElement().satisfies(i -> assertThat(i.fecha()).isEqualTo(AYER));
        assertThat(adapter.preguntasConMasFallos(3L, 5)).singleElement().satisfies(p -> assertThat(p.resuelta()).isTrue());
        assertThat(adapter.participacion(3L).logros()).isEqualTo(3L);
        assertThat(adapter.ultimosQuices(3L, 5)).singleElement().satisfies(q -> assertThat(q.total()).isEqualTo(5));
        assertThat(EstadisticasDocenteJdbcAdapter.porcentaje(1, 3)).isEqualTo(33.3);
    }
}
