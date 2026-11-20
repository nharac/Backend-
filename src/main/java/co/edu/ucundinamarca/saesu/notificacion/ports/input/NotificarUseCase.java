package co.edu.ucundinamarca.saesu.notificacion.ports.input;

/**
 * Lo que usan los demás módulos para avisarle algo a un usuario (campanita).
 * Nunca lanza excepciones: si falla, solo se registra en el log.
 */
public interface NotificarUseCase {

    void notificar(Long usuarioId, String tipo, String titulo, String mensaje, String enlace);

    /** Avisa a todos los estudiantes (por ejemplo, una tarea para todo el grupo). */
    void notificarEstudiantes(String tipo, String titulo, String mensaje, String enlace);
}
