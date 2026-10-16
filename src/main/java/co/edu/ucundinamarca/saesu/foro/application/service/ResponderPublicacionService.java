package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.request.ResponderPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaPadreInvalidaException;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.input.ResponderPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Caso de uso: responder a una publicación o a otra respuesta (anidamiento 1 nivel).
 */
@Service
@RequiredArgsConstructor
public class ResponderPublicacionService implements ResponderPublicacionUseCase {

    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    @Transactional
    public RespuestaResponse responder(Long publicacionId,
                                        ResponderPublicacionRequest request,
                                        String correoAutor) {

        // 1. Validar que la publicación existe
        publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradaException(
                        "Publicación no encontrada con id: " + publicacionId));

        // 2. Validar respuesta padre (si aplica)
        if (request.getRespuestaPadreId() != null) {
            Respuesta padre = respuestaRepository.buscarPorId(request.getRespuestaPadreId())
                    .orElseThrow(() -> new RespuestaNoEncontradaException(
                            "Respuesta padre no encontrada con id: " + request.getRespuestaPadreId()));

            // El padre debe pertenecer a la misma publicación
            if (!padre.getPublicacionId().equals(publicacionId)) {
                throw new RespuestaPadreInvalidaException(
                        "La respuesta padre no pertenece a esta publicación");
            }

            // Solo 1 nivel: el padre no puede ser ya una respuesta anidada
            if (padre.esAnidada()) {
                throw new RespuestaPadreInvalidaException(
                        "No se permiten más de 1 nivel de anidamiento");
            }
        }

        // 3. Buscar autor
        Usuario autor = usuarioRepository.buscarPorCorreo(correoAutor)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        // 4. Crear respuesta
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

        // 5. Convertir a DTO
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