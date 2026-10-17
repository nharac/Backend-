package co.edu.ucundinamarca.saesu.foro.ports.output;

import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de publicaciones.
 */
public interface PublicacionRepositoryPort {

    /**
     * Lista publicaciones con filtros y paginación.
     */
    List<Publicacion> listar(String tema, String busqueda, String orden, int page, int size);

    /**
     * Cuenta total de publicaciones con los filtros aplicados.
     */
    long contar(String tema, String busqueda);

    /**
     * Busca una publicación por ID.
     */
    Optional<Publicacion> buscarPorId(Long id);

    /**
     * Guarda una publicación.
     */
    Publicacion guardar(Publicacion publicacion);
    
        /**
     * Elimina una publicación por ID.
     */
    void eliminar(Long id);
}