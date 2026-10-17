package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.PublicacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para PublicacionEntity.
 * Usa JOIN FETCH para traer al autor en una sola query (evita N+1).
 */
@Repository
public interface PublicacionJpaRepository extends JpaRepository<PublicacionEntity, Long> {

    /**
     * Lista publicaciones con filtros opcionales.
     * Se usa CAST para evitar el error "function lower(bytea) does not exist"
     * cuando los parámetros opcionales vienen nulos.
     */
    @Query("""
        SELECT DISTINCT p FROM PublicacionEntity p
        LEFT JOIN FETCH p.autor
        WHERE p.activa = true
          AND (CAST(:tema AS string) IS NULL OR p.tema = :tema)
          AND (CAST(:busqueda AS string) IS NULL
               OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%'))
               OR LOWER(p.contenido) LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%')))
        """)
    Page<PublicacionEntity> buscarConFiltros(
            @Param("tema") String tema,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );

    /**
     * Cuenta publicaciones con los filtros aplicados.
     */
    @Query("""
        SELECT COUNT(p) FROM PublicacionEntity p
        WHERE p.activa = true
          AND (CAST(:tema AS string) IS NULL OR p.tema = :tema)
          AND (CAST(:busqueda AS string) IS NULL
               OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%'))
               OR LOWER(p.contenido) LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%')))
        """)
    long contarConFiltros(
            @Param("tema") String tema,
            @Param("busqueda") String busqueda
    );
}