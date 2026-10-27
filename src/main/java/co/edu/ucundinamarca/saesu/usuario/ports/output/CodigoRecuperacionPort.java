package co.edu.ucundinamarca.saesu.usuario.ports.output;

import java.time.LocalDateTime;
import java.util.Optional;

/** Códigos de recuperación de contraseña (tabla codigos_recuperacion, V16). */
public interface CodigoRecuperacionPort {

    /** Código vigente: no usado, sin vencer y con intentos disponibles. */
    record CodigoActivo(Long id, String codigoHash, int intentos) {}

    Optional<CodigoActivo> buscarActivo(Long usuarioId, int maxIntentos);

    /** Fecha del último código pedido por el usuario (para no reenviar en ráfaga). */
    Optional<LocalDateTime> fechaUltimoCodigo(Long usuarioId);

    /** Anula los códigos vigentes del usuario (al pedir uno nuevo o al cambiar la contraseña). */
    void invalidarActivos(Long usuarioId);

    void guardar(Long usuarioId, String codigoHash, int minutosVigencia);

    void sumarIntento(Long codigoId);

    void marcarUsado(Long codigoId);

    void actualizarPassword(Long usuarioId, String passwordHash);
}
