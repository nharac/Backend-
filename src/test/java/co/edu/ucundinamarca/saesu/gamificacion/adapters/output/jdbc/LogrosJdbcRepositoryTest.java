package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc;

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
                .filas("AS aciertos", List.of(fila("aciertos", 30L, "primera", 12L, "racha", 5L, "publicaciones", 2L,
                        "respuestas_foro", 7L, "oficiales", 1L, "puntos", 640L)));

        Estadisticas e = new LogrosJdbcRepository(jdbc).estadisticas(3L);

        assertThat(e.aciertos()).isEqualTo(30L);
        assertThat(e.respuestasOficiales()).isEqualTo(1L);
        assertThat(e.puntos()).isEqualTo(640L);
        assertThat(e.temas().get("derivadas")).containsExactly(8L, 8L);
        assertThat(jdbc.consultas.get(1).args()).hasSize(7).containsOnly(3L);
    }

    @Test
    void rachaYPuntosDeUnUsuarioQueNoExisteSonCero() {
        JdbcFalso jdbc = new JdbcFalso();
        LogrosJdbcRepository repo = new LogrosJdbcRepository(jdbc);
        assertThat(repo.rachaYPuntos(99L)).containsExactly(0L, 0L);

        jdbc.filas("AS racha", List.of(fila("racha", 4L, "puntos", 220L)));
        assertThat(repo.rachaYPuntos(3L)).containsExactly(4L, 220L);
    }

    @Test
    void logrosObtenidosYGuardar() {
        OffsetDateTime cuando = OffsetDateTime.of(2026, 10, 20, 15, 0, 0, 0, ZoneOffset.UTC);
        JdbcFalso jdbc = new JdbcFalso().filas("FROM usuario_logros", List.of(fila("codigo", "PRIMER_ACIERTO", "fecha_obtenido", cuando)));
        LogrosJdbcRepository repo = new LogrosJdbcRepository(jdbc);

        assertThat(repo.obtenidos(3L)).containsEntry("PRIMER_ACIERTO", cuando);
        assertThat(repo.guardar(3L, "RACHA_7")).isTrue();
        jdbc.filasAfectadas = 0;
        assertThat(repo.guardar(3L, "RACHA_7")).as("ya lo tenía").isFalse();
    }
}
