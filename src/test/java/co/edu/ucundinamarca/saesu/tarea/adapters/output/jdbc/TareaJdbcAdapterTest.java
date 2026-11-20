package co.edu.ucundinamarca.saesu.tarea.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaDocente;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaEstudiante;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class TareaJdbcAdapterTest {

    private final JdbcFalso jdbc = new JdbcFalso();
    private final TareaJdbcAdapter adapter = new TareaJdbcAdapter(jdbc);

    @Test
    void mapeaLasTareasDelDocente() {
        jdbc.filas("FROM tareas t", List.of(fila("id", 7L, "titulo", "Repaso", "instrucciones", null, "tema", "algebra",
                "meta_preguntas", 5, "fecha_limite", Date.valueOf(LocalDate.of(2026, 11, 25)), "estudiante_id", null,
                "estudiante", null, "creada_en", Timestamp.valueOf(LocalDateTime.of(2026, 11, 20, 8, 0)),
                "asignados", 4L, "completadas", 2L)));

        List<FilaTareaDocente> t = adapter.delDocente(1L);

        assertThat(t).hasSize(1);
        assertThat(t.get(0).estudianteId()).isNull();
        assertThat(t.get(0).fechaLimite()).isEqualTo(LocalDate.of(2026, 11, 25));
        assertThat(t.get(0).completadas()).isEqualTo(2);
        assertThat(jdbc.consultas.get(0).sql()).contains("LATERAL").contains("r.fecha_intento >= t.creada_en");
    }

    @Test
    void mapeaLasTareasDelEstudianteYPasaSuIdDosVeces() {
        jdbc.filas("FROM tareas t", List.of(fila("id", 3L, "titulo", "Limites", "instrucciones", "Haz 3", "tema", "limites",
                "meta_preguntas", 3, "fecha_limite", Date.valueOf(LocalDate.of(2026, 11, 22)), "docente", "Profe", "resueltas", 1L)));

        List<FilaTareaEstudiante> t = adapter.delEstudiante(5L);

        assertThat(t.get(0).resueltas()).isEqualTo(1);
        assertThat(t.get(0).docente()).isEqualTo("Profe");
        assertThat(jdbc.consultas.get(0).args()).containsExactly(5L, 5L);
    }

    @Test
    void creaConInstruccionesLimpiasYEliminaSoloLasPropias() {
        jdbc.filas("INSERT INTO tareas", List.of(fila("id", 11L)));
        Long id = adapter.crear(new TareaRequest(" Repaso ", "   ", "algebra", 5, LocalDate.of(2026, 11, 25), null),
                1L, LocalDateTime.of(2026, 11, 20, 8, 0));
        assertThat(id).isEqualTo(11L);
        assertThat(jdbc.consultas.get(0).args()[2]).isEqualTo("Repaso");
        assertThat(jdbc.consultas.get(0).args()[3]).isNull();

        assertThat(adapter.eliminar(11L, 1L)).isTrue();
        assertThat(jdbc.actualizaciones.get(0).sql()).contains("docente_id = ?");
    }
}
