package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'respuestas_estudiante'.
 */
@Entity
@Table(name = "respuestas_estudiante")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaEstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;

    @Column(name = "opcion_id", nullable = false)
    private Long opcionId;

    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta;

    @Column(name = "fecha_intento", nullable = false)
    private LocalDateTime fechaIntento;
}