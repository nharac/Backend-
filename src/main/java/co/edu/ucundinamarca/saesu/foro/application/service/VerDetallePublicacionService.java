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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerDetallePublicacionService implements VerDetallePublicacionUseCase {

    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public PublicacionDetalleResponse verDetalle(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new PublicacionNoEncontradaException(
                        "Publicación no encontrada con id: " + publicacionId));

        if (Boolean.FALSE.equals(publicacion.getActiva())) {
            throw new PublicacionNoEncontradaException("Publicación no encontrada o eliminada");
        }

        AutorResponse autor = construirAutor(publicacion.getAutorId());
        List<Respuesta> todasLasRespuestas = respuestaRepository.listarPorPublicacion(publicacionId);
        List<RespuestaResponse> respuestasAnidadas = armarArbol(todasLasRespuestas);

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

    private List<RespuestaResponse> armarArbol(List<Respuesta> respuestas) {
        Map<Long, RespuestaResponse> mapaRespuestas = respuestas.stream()
                .collect(Collectors.toMap(
                        Respuesta::getId,
                        r -> new RespuestaResponse(
                                r.getId(),
                                construirAutor(r.getAutorId()),
                                r.getContenido(),
                                Boolean.TRUE.equals(r.getEsOficial()),
                                r.getFechaCreacion(),
                                new ArrayList<>()
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

    private AutorResponse construirAutor(Long autorId) {
        Usuario usuario = usuarioRepository.buscarPorId(autorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + autorId));
        return new AutorResponse(
                usuario.getId(),
                usuario.getNombre(),
                calcularIniciales(usuario.getNombre()),
                usuario.getRol()
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