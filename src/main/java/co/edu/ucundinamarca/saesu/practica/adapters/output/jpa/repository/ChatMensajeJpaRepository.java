package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.ChatMensajeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para ChatMensajeEntity.
 */
@Repository
public interface ChatMensajeJpaRepository extends JpaRepository<ChatMensajeEntity, Long> {

    /**
     * Últimos N mensajes del chat libre (sin pregunta asociada).
     * Se devuelven ordenados descendente para limitar, el adapter los invierte.
     */
    @Query("""
        SELECT c FROM ChatMensajeEntity c
        WHERE c.usuarioId = :usuarioId AND c.preguntaId IS NULL AND c.oculto = false
        ORDER BY c.fechaHora DESC
        """)
    List<ChatMensajeEntity> historialLibre(@Param("usuarioId") Long usuarioId, Pageable pageable);

    /**
     * Últimos N mensajes del chat contextual a una pregunta.
     */
    @Query("""
        SELECT c FROM ChatMensajeEntity c
        WHERE c.usuarioId = :usuarioId AND c.preguntaId = :preguntaId AND c.oculto = false
        ORDER BY c.fechaHora DESC
        """)
    List<ChatMensajeEntity> historialPorPregunta(
            @Param("usuarioId") Long usuarioId,
            @Param("preguntaId") Long preguntaId,
            Pageable pageable
    );

    /**
     * Cuenta cuántos mensajes del USUARIO se enviaron desde una fecha.
     */
    @Query("""
        SELECT COUNT(c) FROM ChatMensajeEntity c
        WHERE c.usuarioId = :usuarioId
          AND c.rolEmisor = 'usuario'
          AND c.fechaHora >= :desde
        """)
    long contarMensajesUsuarioDesde(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde
    );

    /**
     * "Borra" el historial del chat libre: lo oculta, pero los mensajes
     * siguen contando para el límite diario (contarMensajesUsuarioDesde no filtra ocultos).
     */
    @Modifying
    @Query("UPDATE ChatMensajeEntity c SET c.oculto = true WHERE c.usuarioId = :usuarioId AND c.preguntaId IS NULL")
    void eliminarHistorialLibre(@Param("usuarioId") Long usuarioId);

    /**
     * "Borra" el historial del chat de una pregunta (lo oculta, ver arriba).
     */
    @Modifying
    @Query("UPDATE ChatMensajeEntity c SET c.oculto = true WHERE c.usuarioId = :usuarioId AND c.preguntaId = :preguntaId")
    void eliminarHistorialPorPregunta(
            @Param("usuarioId") Long usuarioId,
            @Param("preguntaId") Long preguntaId
    );
}