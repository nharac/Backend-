package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ValidacionResponse;

/**
 * Caso de uso: validar la respuesta seleccionada por el estudiante.
 */
public interface ValidarRespuestaUseCase {

    /**
     * Valida la opción seleccionada contra la correcta.
     *
     * @param preguntaId       ID de la pregunta.
     * @param opcionId         ID de la opción seleccionada.
     * @param correoEstudiante correo del estudiante autenticado (para registrar intento).
     * @return resultado de la validación.
     */
    ValidacionResponse validar(Long preguntaId, Long opcionId, String correoEstudiante);
}