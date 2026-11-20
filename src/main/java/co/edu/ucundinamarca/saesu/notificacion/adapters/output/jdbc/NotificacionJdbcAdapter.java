package co.edu.ucundinamarca.saesu.notificacion.adapters.output.jdbc;

import co.edu.ucundinamarca.saesu.notificacion.application.dto.NotificacionResponse;
import co.edu.ucundinamarca.saesu.notificacion.ports.output.NotificacionPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class NotificacionJdbcAdapter implements NotificacionPort {

    private final JdbcTemplate jdbc;

    public NotificacionJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> idPorCorreo(String correo) {
        return jdbc.query("SELECT id FROM usuarios WHERE LOWER(correo) = LOWER(?)", (rs, i) -> rs.getLong("id"), correo)
                .stream().findFirst();
    }

    @Override
    public void crear(Long usuarioId, String tipo, String titulo, String mensaje, String enlace, LocalDateTime creadaEn) {
        jdbc.update("INSERT INTO notificaciones (usuario_id, tipo, titulo, mensaje, enlace, creada_en) VALUES (?, ?, ?, ?, ?, ?)",
                usuarioId, tipo, titulo, mensaje, enlace, Timestamp.valueOf(creadaEn));
    }

    @Override
    public void crearParaEstudiantes(String tipo, String titulo, String mensaje, String enlace, LocalDateTime creadaEn) {
        jdbc.update("""
                INSERT INTO notificaciones (usuario_id, tipo, titulo, mensaje, enlace, creada_en)
                SELECT u.id, ?, ?, ?, ?, ? FROM usuarios u WHERE UPPER(REPLACE(u.rol, 'ROLE_', '')) = 'ESTUDIANTE'
                """, tipo, titulo, mensaje, enlace, Timestamp.valueOf(creadaEn));
    }

    @Override
    public List<NotificacionResponse> ultimas(Long usuarioId, int limite) {
        return jdbc.query("""
                SELECT id, tipo, titulo, mensaje, enlace, leida, creada_en FROM notificaciones
                WHERE usuario_id = ? ORDER BY creada_en DESC, id DESC LIMIT ?
                """, (rs, i) -> {
            Timestamp t = rs.getTimestamp("creada_en");
            return new NotificacionResponse(rs.getLong("id"), rs.getString("tipo"), rs.getString("titulo"), rs.getString("mensaje"),
                    rs.getString("enlace"), rs.getBoolean("leida"), t == null ? null : t.toLocalDateTime());
        }, usuarioId, limite);
    }

    @Override
    public long noLeidas(Long usuarioId) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM notificaciones WHERE usuario_id = ? AND NOT leida", Long.class, usuarioId);
        return n == null ? 0 : n;
    }

    @Override
    public boolean marcarLeida(Long id, Long usuarioId) {
        return jdbc.update("UPDATE notificaciones SET leida = TRUE WHERE id = ? AND usuario_id = ?", id, usuarioId) > 0;
    }

    @Override
    public int marcarTodas(Long usuarioId) {
        return jdbc.update("UPDATE notificaciones SET leida = TRUE WHERE usuario_id = ? AND NOT leida", usuarioId);
    }
}
