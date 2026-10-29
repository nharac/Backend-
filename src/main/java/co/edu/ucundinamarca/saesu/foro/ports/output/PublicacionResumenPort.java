package co.edu.ucundinamarca.saesu.foro.ports.output;

import co.edu.ucundinamarca.saesu.foro.domain.model.PublicacionResumen;

import java.util.List;

/**
 * Puerto de lectura para los listados del foro (lista de estudiantes y pendientes del docente).
 * Cada método resuelve todo en una sola consulta: publicación + autor + número de respuestas.
 */
public interface PublicacionResumenPort {

    /** Caracteres del contenido que se traen para la vista previa. */
    int LARGO_INICIO = 151;

    /**
     * Publicaciones activas con filtros opcionales.
     * @param orden "reciente" (por defecto), "antiguo" o "respuestas" (las más respondidas primero)
     */
    List<PublicacionResumen> listar(String tema, String busqueda, String orden, int page, int size);

    /** Publicaciones activas que todavía no tienen una respuesta oficial, de la más nueva a la más antigua. */
    List<PublicacionResumen> listarSinRespuestaOficial();
}
