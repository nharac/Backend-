package co.edu.ucundinamarca.saesu.practica.ports.input;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.PreguntaResponse;

import java.util.List;

/**
 * Caso de uso: listar preguntas con filtros opcionales.
 */
public interface ListarPreguntasUseCase {

    /**
     * Lista las preguntas disponibles con filtros opcionales.
     *
     * @param tema       filtro por tema (limites, derivadas, integrales). Opcional.
     * @param dificultad filtro por dificultad (basico, intermedio, avanzado). Opcional.
     * @param page       número de página (empezando en 0).
     * @param size       tamaño de página.
     * @return lista paginada de preguntas.
     */
    List<PreguntaResponse> listar(String tema, String dificultad, int page, int size);

    /**
     * Cuenta total de preguntas con los filtros aplicados.
     * Útil para paginación.
     */
    long contar(String tema, String dificultad);
}