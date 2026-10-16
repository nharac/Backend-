package co.edu.ucundinamarca.saesu.practica.ports.output;

import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;

import java.util.List;

/**
 * Puerto de salida para persistencia de intentos del estudiante.
 */
public interface RespuestaEstudianteRepositoryPort {

    /**
     * Guarda un intento de respuesta.
     */
    RespuestaEstudiante guardar(RespuestaEstudiante respuesta);

    /**
     * Lista todos los intentos del estudiante.
     */
    List<RespuestaEstudiante> listarPorUsuario(Long usuarioId);

    /**
     * Cuenta cuántas preguntas respondió el estudiante (únicas).
     */
    long contarPreguntasRespondidas(Long usuarioId);

    /**
     * Cuenta cuántas preguntas respondió correctamente (únicas).
     */
    long contarPreguntasCorrectas(Long usuarioId);

    /**
     * Cuenta por tema: respondidas y correctas.
     */
    List<Object[]> contarPorTema(Long usuarioId);
}