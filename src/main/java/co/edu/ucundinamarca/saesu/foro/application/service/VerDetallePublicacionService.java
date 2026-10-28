package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.input.VerDetallePublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerDetallePublicacionService implements VerDetallePublicacionUseCase {

    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    static final String CONTENIDO_OCULTO = "Esta respuesta fue ocultada por un docente.";

    /**
     * Una sola transacción y los autores en una sola consulta: antes se buscaba el autor
     * de cada respuesta por separado (una ida a la base de datos por respuesta).
     */
    @Override
    @Transactional(readOnly = true)
    public PublicacionDetalleResponse verDetalle(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradaException(
                        "Publicación no encontrada con id: " + publicacionId));

        if (Boolean.FALSE.equals(publicacion.getActiva())) {
            throw new PublicacionNoEncontradaException("Publicación no encontrada o eliminada");
        }

        List<Respuesta> todasLasRespuestas = respuestaRepository.listarPorPublicacion(publicacionId);
        Set<Long> ids = new HashSet<>();
        ids.add(publicacion.getAutorId());
        todasLasRespuestas.forEach(r -> ids.add(r.getAutorId()));
        Map<Long, Usuario> autores = usuarioRepository.buscarPorIds(ids);

        AutorResponse autor = construirAutor(autores, publicacion.getAutorId());
        List<RespuestaResponse> respuestasAnidadas = armarArbol(todasLasRespuestas, autores);

        return new PublicacionDetalleResponse(
                publicacion.getId(),
                publicacion.getTitulo(),
                publicacion.getContenido(),
                publicacion.getTema(),
                autor,
                publicacion.getFechaCreacion(),
                respuestasAnidadas,
                publicacion.getImagenUrl() // <-- NUEVO
        );
    }

    private List<RespuestaResponse> armarArbol(List<Respuesta> respuestas, Map<Long, Usuario> autores) {
        Map<Long, RespuestaResponse> mapaRespuestas = respuestas.stream()
                .collect(Collectors.toMap(
                        Respuesta::getId,
                        r -> new RespuestaResponse(
                                r.getId(),
                                construirAutor(autores, r.getAutorId()),
                                // Moderación: se conserva el lugar en el hilo, pero no el contenido
                                r.isOculta() ? CONTENIDO_OCULTO : r.getContenido(),
                                !r.isOculta() && Boolean.TRUE.equals(r.getEsOficial()),
                                r.getFechaCreacion(),
                                new ArrayList<>(),
                                r.isOculta()
                        )
                ));

        List<RespuestaResponse> raices = new ArrayList<>();

        for (Respuesta r : respuestas) {
            RespuestaResponse dto = mapaRespuestas.get(r.getId());
            if (r.getRespuestaPadreId() == null) {
                raices.add(dto);
            } else {
                RespuestaResponse padre = mapaRespuestas.get(r.getRespuestaPadreId());
                if (padre != null) {
                    padre.getRespuestasHijas().add(dto);
                } else {
                    raices.add(dto);
                }
            }
        }

        return raices;
    }

    private AutorResponse construirAutor(Map<Long, Usuario> autores, Long autorId) {
        Usuario usuario = autores.get(autorId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con id: " + autorId);
        }
        return ResumenForo.autor(usuario);
    }
}
