package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.ChatMensajeMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.ChatMensajeJpaRepository;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.ports.output.ChatMensajeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adaptador que implementa ChatMensajeRepositoryPort usando JPA.
 */
@Component
@RequiredArgsConstructor
public class ChatMensajeRepositoryAdapter implements ChatMensajeRepositoryPort {

    private final ChatMensajeJpaRepository jpaRepository;
    private final ChatMensajeMapper mapper;

    @Override
    public ChatMensaje guardar(ChatMensaje mensaje) {
        var entity = mapper.toEntity(mensaje);
        var guardada = jpaRepository.save(entity);
        return mapper.toDominio(guardada);
    }

    @Override
    public List<ChatMensaje> listarHistorialLibre(Long usuarioId, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        List<ChatMensaje> mensajes = new ArrayList<>();
        jpaRepository.historialLibre(usuarioId, pageable).forEach(e -> mensajes.add(mapper.toDominio(e)));
        // Invertimos para que vengan en orden cronológico ascendente
        Collections.reverse(mensajes);
        return mensajes;
    }

    @Override
    public List<ChatMensaje> listarHistorialPorPregunta(Long usuarioId, Long preguntaId, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        List<ChatMensaje> mensajes = new ArrayList<>();
        jpaRepository.historialPorPregunta(usuarioId, preguntaId, pageable)
                .forEach(e -> mensajes.add(mapper.toDominio(e)));
        Collections.reverse(mensajes);
        return mensajes;
    }

    @Override
    public long contarMensajesHoy(Long usuarioId, LocalDateTime inicioDelDia) {
        return jpaRepository.contarMensajesUsuarioDesde(usuarioId, inicioDelDia);
    }

    @Override
    @Transactional
    public void eliminarHistorialLibre(Long usuarioId) {
        jpaRepository.eliminarHistorialLibre(usuarioId);
    }

    @Override
    @Transactional
    public void eliminarHistorialPorPregunta(Long usuarioId, Long preguntaId) {
        jpaRepository.eliminarHistorialPorPregunta(usuarioId, preguntaId);
    }
}