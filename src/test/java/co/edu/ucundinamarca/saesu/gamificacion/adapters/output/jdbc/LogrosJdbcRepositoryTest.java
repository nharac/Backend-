package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Cifra;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort.Estadisticas;
import co.edu.ucundinamarca.saesu.testutil.JdbcFalso;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static co.edu.ucundinamarca.saesu.testutil.JdbcFalso.fila;
import static org.assertj.core.api.Assertions.assertThat;

class LogrosJdbcRepositoryTest {

    @Test
    void estadisticasJuntaLosTemasConLosContadores() {
        JdbcFalso jdbc = new JdbcFalso()
                .filas("GROUP BY p.tema", List.of(fila("tema", "limites", "total", 10L, "resueltas", 4L),
                        fila("tema", "derivadas", "total", 8L, "resueltas", 8L)))
                .filas("AS respondidas", List.of(fila("respondidas", 40L, "aciertos", 30L, "primera", 12L,
                        "avanzadas", 4L, "total_avanzadas", 40L, "max_aciertos_dia", 9L, "max_seguidas", 6L, "nocturnas", 2L,
                        "madrugadas", 0L, "racha", 5L, "quices", 3L, "quices_perfectos", 1L, "publicaciones", 2L,
                        "respuestas_foro", 7L, "oficiales", 1L, "mensajes_tutor", 15L, "puntos", 640L, "correo", "ana@ucundinamarca.edu.co")));

        Estadisticas e = new LogrosJdbcRepository(jdbc, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO).estadisticas(3L);

        assertThat(e.get(Cifra.ACIERTOS)).isEqualTo(30L);
        assertThat(e.get(Cifra.OFICIALES)).isEqualTo(1L);
        assertThat(e.get(Cifra.PUNTOS)).isEqualTo(640L);
        assertThat(e.get(Cifra.MAX_SEGUIDAS)).isEqualTo(6L);
        assertThat(e.get(Cifra.MENSAJES_TUTOR)).isEqualTo(15L);
        assertThat(e.get(Cifra.TOTAL_AVANZADAS)).isEqualTo(40L);
        assertThat(e.correo()).isEqualTo("ana@ucundinamarca.edu.co");
        assertThat(e.get(Cifra.LOGROS)).as("las derivadas no salen del SQL").isZero();
        assertThat(e.cifras()).hasSize(LogrosJdbcRepository.CIFRAS_SQL.size());
        assertThat(e.temas().get("derivadas")).containsExactly(8L, 8L);
        assertThat(e.preguntasDelTema("integrales")).isZero();
        assertThat(jdbc.consultas.get(1).args()).as("el id entra una sola vez").containsExactly(3L);
    }

    @Test
    void rachaYPuntosDeUnUsuarioQueNoExisteSonCero() {
        JdbcFalso jdbc = new JdbcFalso();
        LogrosJdbcRepository repo = new LogrosJdbcRepository(jdbc, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);
        assertThat(repo.rachaYPuntos(99L)).containsExactly(0L, 0L);

        jdbc.filas("AS racha", List.of(fila("racha", 4L, "puntos", 220L)));
        assertThat(repo.rachaYPuntos(3L)).containsExactly(4L, 220L);
    }

    @Test
    void logrosObtenidosYGuardar() {
        OffsetDateTime cuando = OffsetDateTime.of(2026, 10, 20, 15, 0, 0, 0, ZoneOffset.UTC);
        JdbcFalso jdbc = new JdbcFalso().filas("FROM usuario_logros", List.of(fila("codigo", "PRIMER_ACIERTO", "fecha_obtenido", cuando)));
        LogrosJdbcRepository repo = new LogrosJdbcRepository(jdbc, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);

        assertThat(repo.obtenidos(3L)).containsEntry("PRIMER_ACIERTO", cuando);
        assertThat(repo.guardar(3L, "RACHA_7")).isTrue();
        jdbc.filasAfectadas = 0;
        assertThat(repo.guardar(3L, "RACHA_7")).as("ya lo tenía").isFalse();
    }
}
