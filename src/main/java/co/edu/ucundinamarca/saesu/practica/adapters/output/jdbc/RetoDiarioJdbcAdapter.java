package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Adaptador JDBC del reto diario (preguntas y reto_diario_intentos). */
@Component
public class RetoDiarioJdbcAdapter implements RetoDiarioPort {

    private final JdbcTemplate jdbc;

    public RetoDiarioJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> preguntaDelDia(long indice) {
        List<Long> total = jdbc.query("SELECT COUNT(*) AS total FROM preguntas", (rs, i) -> rs.getLong("total"));
        long cuantas = total.isEmpty() ? 0 : total.get(0);
        if (cuantas == 0) {
            return Optional.empty();
        }
        List<Long> id = jdbc.query("SELECT id FROM preguntas ORDER BY id LIMIT 1 OFFSET ?",
                (rs, i) -> rs.getLong("id"), Math.floorMod(indice, cuantas));
        return id.stream().findFirst();
    }

    @Override
    public Optional<Intento> intento(Long usuarioId, LocalDate fecha) {
        return jdbc.query("""
                SELECT pregunta_id, opcion_id, correcta FROM reto_diario_intentos
                WHERE usuario_id = ? AND fecha = ?
                """, (rs, i) -> new Intento(rs.getLong("pregunta_id"), rs.getLong("opcion_id"), rs.getBoolean("correcta")),
                usuarioId, Date.valueOf(fecha)).stream().findFirst();
    }

    @Override
    public boolean registrar(Long usuarioId, LocalDate fecha, Intento intento) {
        return jdbc.update("""
                INSERT INTO reto_diario_intentos (usuario_id, fecha, pregunta_id, opcion_id, correcta, respondido_en)
                VALUES (?, ?, ?, ?, ?, NOW())
                ON CONFLICT (usuario_id, fecha) DO NOTHING
                """, usuarioId, Date.valueOf(fecha), intento.preguntaId(), intento.opcionId(), intento.correcta()) > 0;
    }
}
