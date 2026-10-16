package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.RespuestaEstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para RespuestaEstudianteEntity.
 */
@Repository
public interface RespuestaEstudianteJpaRepository
        extends JpaRepository<RespuestaEstudianteEntity, Long> {

    /**
     * Lista todas las respuestas de un usuario.
     */
    List<RespuestaEstudianteEntity> findByUsuarioId(Long usuarioId);

    /**
     * Cuenta cuántas preguntas respondió el usuario (únicas).
     */
    @Query("SELECT COUNT(DISTINCT r.preguntaId) FROM RespuestaEstudianteEntity r WHERE r.usuarioId = :usuarioId")
    long contarPreguntasRespondidas(@Param("usuarioId") Long usuarioId);

    /**
     * Cuenta cuántas preguntas correctas respondió el usuario (únicas).
     */
    @Query("""
        SELECT COUNT(DISTINCT r.preguntaId) FROM RespuestaEstudianteEntity r
        WHERE r.usuarioId = :usuarioId AND r.esCorrecta = true
        """)
    long contarPreguntasCorrectas(@Param("usuarioId") Long usuarioId);

    /**
     * Agrupa por tema: cuenta respondidas y correctas.
     * Devuelve Object[] con [tema, total, correctas].
     */
    @Query("""
        SELECT p.tema, COUNT(DISTINCT r.preguntaId), 
               COUNT(DISTINCT CASE WHEN r.esCorrecta = true THEN r.preguntaId END)
        FROM RespuestaEstudianteEntity r
        JOIN PreguntaEntity p ON p.id = r.preguntaId
        WHERE r.usuarioId = :usuarioId
        GROUP BY p.tema
        """)
    List<Object[]> contarPorTema(@Param("usuarioId") Long usuarioId);
}