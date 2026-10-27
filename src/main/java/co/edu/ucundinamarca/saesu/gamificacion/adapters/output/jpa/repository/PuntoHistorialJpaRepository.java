package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.repository;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.entity.PuntoHistorialEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PuntoHistorialJpaRepository extends JpaRepository<PuntoHistorialEntity, Long> {
    
    // Suma todos los puntos de un usuario (para uso individual)
    @Query("SELECT COALESCE(SUM(p.cantidad), 0) FROM PuntoHistorialEntity p WHERE p.usuario.id = :usuarioId")
    long sumarPuntosPorUsuario(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId);

    // ¿Ya recibió puntos con este motivo? (para premios que se dan una sola vez)
    @Query("SELECT COUNT(p) > 0 FROM PuntoHistorialEntity p WHERE p.usuario.id = :usuarioId AND p.motivo = :motivo")
    boolean existePorMotivo(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId,
                            @org.springframework.data.repository.query.Param("motivo") String motivo);

    // Cuántas veces recibió puntos con este motivo desde una fecha (tope diario del foro)
    @Query("SELECT COUNT(p) FROM PuntoHistorialEntity p WHERE p.usuario.id = :usuarioId AND p.motivo = :motivo AND p.fecha >= :desde")
    long contarPorMotivoDesde(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId,
                              @org.springframework.data.repository.query.Param("motivo") String motivo,
                              @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    // Trae el Top de usuarios con sus puntos sumados
    // Retorna un arreglo de objetos: [UsuarioEntity, SumaPuntos]
    @Query("""
        SELECT u, COALESCE(SUM(p.cantidad), 0) as totalPuntos
        FROM UsuarioEntity u
        JOIN PuntoHistorialEntity p ON p.usuario.id = u.id
        WHERE UPPER(u.rol) IN ('ESTUDIANTE', 'ROLE_ESTUDIANTE')
        GROUP BY u.id, u.nombre, u.fotoPerfilUrl, u.rachaActual
        HAVING COALESCE(SUM(p.cantidad), 0) > 0
        ORDER BY totalPuntos DESC
    """)
    List<Object[]> obtenerTopUsuarios(Pageable pageable);
}