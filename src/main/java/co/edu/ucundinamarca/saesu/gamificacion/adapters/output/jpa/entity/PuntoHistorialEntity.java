package co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jpa.entity;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "puntos_historial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PuntoHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 100)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;
}