package co.edu.ucundinamarca.saesu.practica.domain.model;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa un mensaje del chat entre el estudiante y la IA.
 *
 * Cada mensaje pertenece a un usuario y opcionalmente a una pregunta:
 *   - preguntaId == null  → mensaje del chat libre (sin contexto).
 *   - preguntaId != null  → mensaje del chat contextual a una pregunta.
 *
 * Modelo de dominio puro. Sin anotaciones JPA.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMensaje {

    private Long id;
    private Long usuarioId;
    private Long preguntaId;
    private RolEmisor rolEmisor;
    private String mensaje;
    private LocalDateTime fechaHora;

    /**
     * Verifica si este mensaje es del chat libre (sin pregunta asociada).
     */
    public boolean esChatLibre() {
        return this.preguntaId == null;
    }

    /**
     * Verifica si este mensaje es del chat contextual (con pregunta).
     */
    public boolean esChatContextual() {
        return this.preguntaId != null;
    }
}