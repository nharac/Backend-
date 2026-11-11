package co.edu.ucundinamarca.saesu.gamificacion.ports.input;

/**
 * Puerto de entrada de gamificación: lo que los otros módulos (práctica, foro, docente) pueden pedir
 * para premiar a un estudiante. Dependen de esta interfaz y no de la implementación (PuntosService).
 */
public interface OtorgarPuntosUseCase {

    /** Puntos Sumi (y sus Sumi Monedas) por un logro; también cuenta para la racha. */
    void otorgarPuntos(Long usuarioId, int cantidad, String motivo);

    /** Puntos por responder en el foro (con límite diario). @return true si los dio */
    boolean otorgarPuntosForo(Long usuarioId, Long autorPublicacionId);

    /** Premio por una respuesta marcada como oficial por un docente (una sola vez por respuesta). */
    void otorgarRespuestaOficial(Long autorId, Long respuestaId);

    /** Marca el día como activo para la racha, sin dar puntos. */
    void registrarActividad(Long usuarioId);
}
