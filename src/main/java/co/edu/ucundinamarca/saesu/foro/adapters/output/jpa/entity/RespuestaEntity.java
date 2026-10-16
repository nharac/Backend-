package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity;

import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'respuestas'.
 */
@Entity
@Table(name = "respuestas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publicacion_id", nullable = false)
    private Long publicacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity autor;

    @Column(name = "respuesta_padre_id")
    private Long respuestaPadreId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "es_oficial", nullable = false)
    private Boolean esOficial;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}