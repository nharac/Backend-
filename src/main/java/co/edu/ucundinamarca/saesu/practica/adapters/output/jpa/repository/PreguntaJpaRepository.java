package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.PreguntaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para PreguntaEntity.
 * Filtra por tema y dificultad opcionales.
 */
@Repository
public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, Long> {

    @Query("""
        SELECT p FROM PreguntaEntity p
        WHERE (:tema IS NULL OR p.tema = :tema)
          AND (:dificultad IS NULL OR p.dificultad = :dificultad)
        """)
    Page<PreguntaEntity> buscarConFiltros(
            @Param("tema") String tema,
            @Param("dificultad") String dificultad,
            Pageable pageable
    );
}