package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ProgresoResponse;

/**
 * Caso de uso: obtener el progreso del estudiante por tema.
 */
public interface ObtenerProgresoUseCase {

    /**
     * Calcula el progreso del estudiante autenticado.
     *
     * @param correoEstudiante correo del estudiante autenticado.
     * @return progreso por tema.
     */
    ProgresoResponse obtenerProgreso(String correoEstudiante);
}