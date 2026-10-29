package co.edu.ucundinamarca.saesu.usuario.ports.output;

import java.util.Optional;

/**
 * Registros que esperan la verificación del correo (tabla registros_pendientes, V18).
 * La cuenta en usuarios se crea solo cuando el código es correcto.
 */
public interface RegistroPendientePort {

    /**
     * @param vigente              el código no ha vencido
     * @param segundosDesdeEnvio   tiempo desde el último código enviado (para no reenviar en ráfaga)
     */
    record Pendiente(Long id, String correo, String nombre, String passwordHash, String codigoHash,
                     int intentos, boolean vigente, long segundosDesdeEnvio) {}

    Optional<Pendiente> buscar(String correo);

    /** Crea o reemplaza el registro pendiente de ese correo con datos y código nuevos (intentos en 0). */
    void guardar(String correo, String nombre, String passwordHash, String codigoHash, int minutosVigencia);

    /** Cambia solo el código (al reenviarlo): vigencia nueva e intentos en 0. */
    void renovarCodigo(Long id, String codigoHash, int minutosVigencia);

    void sumarIntento(Long id);

    void eliminar(Long id);

    /** Borra los registros que nadie verificó en más de un día. */
    void limpiarAbandonados();
}
