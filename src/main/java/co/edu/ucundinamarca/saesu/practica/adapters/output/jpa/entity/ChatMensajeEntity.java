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
 * Entidad JPA para la tabla 'chat_mensaje'.
 */
@Entity
@Table(name = "chat_mensaje")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMensajeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "pregunta_id")
    private Long preguntaId;

    @Column(name = "rol_emisor", nullable = false, length = 20)
    private String rolEmisor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
}