package co.edu.ucundinamarca.saesu.practica.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.practica.ports.output.RepasoEspaciadoPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class RepasoEspaciadoJdbcAdapter implements RepasoEspaciadoPort {

    private final JdbcTemplate jdbc;

    public RepasoEspaciadoJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void fallo(Long usuarioId, Long preguntaId, LocalDate hoy) {
        jdbc.update("""
                INSERT INTO repaso_espaciado (usuario_id, pregunta_id, caja, proxima, actualizado)
                VALUES (?, ?, 1, ?, NOW())
                ON CONFLICT (usuario_id, pregunta_id) DO UPDATE SET caja = 1, proxima = EXCLUDED.proxima, actualizado = NOW()
                """, usuarioId, preguntaId, Date.valueOf(hoy.plusDays(INTERVALOS[0])));
    }

    @Override
    public void acierto(Long usuarioId, Long preguntaId, LocalDate hoy) {
        jdbc.update("""
                UPDATE repaso_espaciado
                SET caja = caja + 1,
                    proxima = CASE caja WHEN 1 THEN ?::date WHEN 2 THEN ?::date ELSE NULL END,
                    actualizado = NOW()
                WHERE usuario_id = ? AND pregunta_id = ? AND proxima IS NOT NULL AND proxima <= ?
                """, Date.valueOf(hoy.plusDays(INTERVALOS[1])), Date.valueOf(hoy.plusDays(INTERVALOS[2])),
                usuarioId, preguntaId, Date.valueOf(hoy));
    }

    @Override
    public Resumen resumen(Long usuarioId, LocalDate hoy) {
        return jdbc.queryForObject("""
                SELECT COUNT(*) FILTER (WHERE proxima <= ?) AS hoy,
                       COUNT(*) FILTER (WHERE proxima = ?) AS manana,
                       COUNT(*) FILTER (WHERE proxima > ? AND proxima <= ?) AS semana,
                       COUNT(*) FILTER (WHERE proxima IS NULL) AS dominadas,
                       COUNT(*) FILTER (WHERE proxima IS NOT NULL) AS en_repaso
                FROM repaso_espaciado re
                JOIN preguntas p ON p.id = re.pregunta_id AND p.activa
                WHERE re.usuario_id = ?
                """, (rs, i) -> new Resumen(rs.getLong("hoy"), rs.getLong("manana"), rs.getLong("semana"),
                        rs.getLong("dominadas"), rs.getLong("en_repaso")),
                Date.valueOf(hoy), Date.valueOf(hoy.plusDays(1)), Date.valueOf(hoy), Date.valueOf(hoy.plusDays(7)), usuarioId);
    }
}
