package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.RespuestaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para RespuestaEntity.
 */
@Repository
public interface RespuestaJpaRepository extends JpaRepository<RespuestaEntity, Long> {

    /**
     * Lista respuestas de una publicación con el autor cargado (JOIN FETCH).
     * Ordenadas por fecha ascendente.
     */
    @Query("""
        SELECT r FROM RespuestaEntity r
        LEFT JOIN FETCH r.autor
        WHERE r.publicacionId = :publicacionId
        ORDER BY r.fechaCreacion ASC
        """)
    List<RespuestaEntity> listarPorPublicacion(@Param("publicacionId") Long publicacionId);

    /**
     * Cuenta las respuestas de una publicación.
     */
    long countByPublicacionId(Long publicacionId);
}