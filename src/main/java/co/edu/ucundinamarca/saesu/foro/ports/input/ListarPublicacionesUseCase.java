package co.edu.ucundinamarca.saesu.foro.ports.input;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;

import java.util.List;

/**
 * Caso de uso: listar publicaciones del foro con filtros.
 */
public interface ListarPublicacionesUseCase {

    /**
     * Lista publicaciones con filtros opcionales.
     *
     * @param tema     filtro por tema (limites, derivadas, integrales). Opcional.
     * @param busqueda búsqueda por texto en título/contenido. Opcional.
     * @param orden    reciente | antiguo | respuestas. Default: reciente.
     * @param page     página (empezando en 0).
     * @param size     tamaño de página.
     * @return lista paginada de publicaciones resumidas.
     */
    List<PublicacionResumenResponse> listar(String tema, String busqueda, String orden, int page, int size);

    /**
     * Cuenta total de publicaciones con los filtros aplicados.
     */
    long contar(String tema, String busqueda);
}
