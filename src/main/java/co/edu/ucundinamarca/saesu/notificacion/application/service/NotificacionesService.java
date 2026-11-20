package co.edu.ucundinamarca.saesu.notificacion.application.service;

import co.edu.ucundinamarca.saesu.notificacion.application.dto.BandejaResponse;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import co.edu.ucundinamarca.saesu.notificacion.ports.output.NotificacionPort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/** Campanita de notificaciones. Crear una notificación nunca hace fallar la acción que la origina. */
@Service
public class NotificacionesService implements NotificarUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesService.class);
    static final int LIMITE = 30;
    private static final int MAX_TITULO = 140;
    private static final int MAX_MENSAJE = 300;

    private final NotificacionPort port;
    private final Clock clock;

    public NotificacionesService(NotificacionPort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    private static String corto(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    @Override
    public void notificar(Long usuarioId, String tipo, String titulo, String mensaje, String enlace) {
        if (usuarioId == null) return;
        try {
            port.crear(usuarioId, tipo, corto(titulo, MAX_TITULO), corto(mensaje, MAX_MENSAJE), enlace, LocalDateTime.now(clock));
        } catch (RuntimeException e) {
            log.warn("No se pudo crear la notificación {} para el usuario {}: {}", tipo, usuarioId, e.getMessage());
        }
    }

    @Override
    public void notificarEstudiantes(String tipo, String titulo, String mensaje, String enlace) {
        try {
            port.crearParaEstudiantes(tipo, corto(titulo, MAX_TITULO), corto(mensaje, MAX_MENSAJE), enlace, LocalDateTime.now(clock));
        } catch (RuntimeException e) {
            log.warn("No se pudo crear la notificación {} para el grupo: {}", tipo, e.getMessage());
        }
    }

    private Long idDe(String correo) {
        return port.idPorCorreo(correo).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    public BandejaResponse bandeja(String correo) {
        Long id = idDe(correo);
        return new BandejaResponse(port.noLeidas(id), port.ultimas(id, LIMITE));
    }

    public long noLeidas(String correo) {
        return port.noLeidas(idDe(correo));
    }

    public boolean marcarLeida(String correo, Long notificacionId) {
        return port.marcarLeida(notificacionId, idDe(correo));
    }

    public int marcarTodas(String correo) {
        return port.marcarTodas(idDe(correo));
    }
}
