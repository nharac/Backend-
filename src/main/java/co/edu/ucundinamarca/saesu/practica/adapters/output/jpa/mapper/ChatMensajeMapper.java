package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.ChatMensajeEntity;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import org.springframework.stereotype.Component;

/**
 * Convierte entre ChatMensajeEntity (JPA) y ChatMensaje (dominio).
 */
@Component
public class ChatMensajeMapper {

    public ChatMensaje toDominio(ChatMensajeEntity entity) {
        return new ChatMensaje(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getPreguntaId(),
                RolEmisor.desdeValor(entity.getRolEmisor()),
                entity.getMensaje(),
                entity.getFechaHora()
        );
    }

    public ChatMensajeEntity toEntity(ChatMensaje dominio) {
        return new ChatMensajeEntity(
                dominio.getId(),
                dominio.getUsuarioId(),
                dominio.getPreguntaId(),
                dominio.getRolEmisor().getValor(),
                dominio.getMensaje(),
                dominio.getFechaHora()
        );
    }
}