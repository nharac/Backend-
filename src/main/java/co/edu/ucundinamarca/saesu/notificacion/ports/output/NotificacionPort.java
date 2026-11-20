package co.edu.ucundinamarca.saesu.notificacion.ports.output;

import co.edu.ucundinamarca.saesu.notificacion.application.dto.NotificacionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificacionPort {

    Optional<Long> idPorCorreo(String correo);

    void crear(Long usuarioId, String tipo, String titulo, String mensaje, String enlace, LocalDateTime creadaEn);

    void crearParaEstudiantes(String tipo, String titulo, String mensaje, String enlace, LocalDateTime creadaEn);

    List<NotificacionResponse> ultimas(Long usuarioId, int limite);

    long noLeidas(Long usuarioId);

    boolean marcarLeida(Long id, Long usuarioId);

    int marcarTodas(Long usuarioId);
}
