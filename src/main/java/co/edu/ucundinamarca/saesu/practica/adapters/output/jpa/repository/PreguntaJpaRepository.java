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

    // SQL nativo para filtrar por la columna "activa" (V14), que no está mapeada en la entidad:
    // así, al guardar una pregunta con JPA, la base de datos aplica su valor por defecto (TRUE).
    @Query(value = """
        SELECT p.* FROM preguntas p
        WHERE p.activa = TRUE
          AND (CAST(:tema AS VARCHAR) IS NULL OR p.tema = CAST(:tema AS VARCHAR))
          AND (CAST(:dificultad AS VARCHAR) IS NULL OR p.dificultad = CAST(:dificultad AS VARCHAR))
        ORDER BY p.id
        """,
        countQuery = """
        SELECT COUNT(*) FROM preguntas p
        WHERE p.activa = TRUE
          AND (CAST(:tema AS VARCHAR) IS NULL OR p.tema = CAST(:tema AS VARCHAR))
          AND (CAST(:dificultad AS VARCHAR) IS NULL OR p.dificultad = CAST(:dificultad AS VARCHAR))
        """,
        nativeQuery = true)
    Page<PreguntaEntity> buscarConFiltros(
            @Param("tema") String tema,
            @Param("dificultad") String dificultad,
            Pageable pageable
    );
}