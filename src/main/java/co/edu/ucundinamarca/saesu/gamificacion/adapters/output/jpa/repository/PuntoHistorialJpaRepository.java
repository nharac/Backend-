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

    // Trae el Top de usuarios con sus puntos sumados
    // Retorna un arreglo de objetos: [UsuarioEntity, SumaPuntos]
    @Query("""
        SELECT u, COALESCE(SUM(p.cantidad), 0) as totalPuntos
        FROM UsuarioEntity u
        LEFT JOIN PuntoHistorialEntity p ON p.usuario.id = u.id
        GROUP BY u.id, u.nombre, u.fotoPerfilUrl, u.rachaActual
        ORDER BY totalPuntos DESC
    """)
    List<Object[]> obtenerTopUsuarios(Pageable pageable);
}