package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService; // <-- email
import co.edu.ucundinamarca.saesu.foro.application.dto.request.ResponderPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaPadreInvalidaException;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion; // <-- email
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.input.ResponderPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Caso de uso: responder a una publicación o a otra respuesta (anidamiento 1 nivel).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResponderPublicacionService implements ResponderPublicacionUseCase {

    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final EmailService emailService; // emails
    private final PuntosService puntosService; // servicio de puntos

    @Override
    @Transactional
    public RespuestaResponse responder(Long publicacionId,
                                        ResponderPublicacionRequest request,
                                        String correoAutor) {

        // Validar que la publicación existe 
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradaException(
                        "Publicación no encontrada con id: " + publicacionId));

        //  Validar respuesta padre (si aplica)
        if (request.getRespuestaPadreId() != null) {
            Respuesta padre = respuestaRepository.buscarPorId(request.getRespuestaPadreId())
                    .orElseThrow(() -> new RespuestaNoEncontradaException(
                            "Respuesta padre no encontrada con id: " + request.getRespuestaPadreId()));

            if (!padre.getPublicacionId().equals(publicacionId)) {
                throw new RespuestaPadreInvalidaException(
                        "La respuesta padre no pertenece a esta publicación");
            }

            if (padre.esAnidada()) {
                throw new RespuestaPadreInvalidaException(
                        "No se permiten más de 1 nivel de anidamiento");
            }
        }

        //  Buscar autor
        Usuario autor = usuarioRepository.buscarPorCorreo(correoAutor)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // Crear respuesta
        Respuesta respuesta = new Respuesta(
                null,
                publicacionId,
                autor.getId(),
                request.getRespuestaPadreId(),
                request.getContenido().trim(),
                false,
                LocalDateTime.now()
        );

        Respuesta guardada = respuestaRepository.guardar(respuesta);

        //  Enviar correo al autor de la publicación (si no es el mismo que responde)
        try {
            if (!publicacion.getAutorId().equals(autor.getId())) {
                Usuario autorPublicacion = usuarioRepository.buscarPorId(publicacion.getAutorId())
                        .orElseThrow(() -> new RuntimeException("Autor de la publicación no encontrado"));
                
                emailService.enviarCorreoNuevaRespuesta(
                        autorPublicacion.getCorreo(),
                        autorPublicacion.getNombre(),
                        publicacion.getTitulo()
                );
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar el aviso de nueva respuesta de la publicación {}: {}", publicacionId, e.getMessage());
        }

        // PUNTOS POR RESPONDER EN EL FORO: +10, máximo 3 por día y no en publicaciones propias
        puntosService.otorgarPuntosForo(autor.getId(), publicacion.getAutorId());

        // Convertir a DTO
        AutorResponse autorDto = new AutorResponse(
                autor.getId(),
                autor.getNombre(),
                calcularIniciales(autor.getNombre()),
                autor.getRol()
        );

        return new RespuestaResponse(
                guardada.getId(),
                autorDto,
                guardada.getContenido(),
                Boolean.TRUE.equals(guardada.getEsOficial()),
                guardada.getFechaCreacion(),
                Collections.emptyList()
        );
    }

    private String calcularIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }
        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }
}