package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.ports.input.ListarPublicacionesUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPublicacionesService implements ListarPublicacionesUseCase {

    private static final int PREVIEW_MAX = 150;

    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public List<PublicacionResumenResponse> listar(String tema, String busqueda, String orden, int page, int size) {
        List<Publicacion> publicaciones = publicacionRepository.listar(tema, busqueda, orden, page, size);

        return publicaciones.stream()
                .map(this::convertirAResumen)
                .toList();
    }

    @Override
    public long contar(String tema, String busqueda) {
        return publicacionRepository.contar(tema, busqueda);
    }

    private PublicacionResumenResponse convertirAResumen(Publicacion publicacion) {
        AutorResponse autor = construirAutor(publicacion.getAutorId());
        long respuestasCount = respuestaRepository.contarPorPublicacion(publicacion.getId());
        String preview = construirPreview(publicacion.getContenido());

        return new PublicacionResumenResponse(
                publicacion.getId(),
                publicacion.getTitulo(),
                preview,
                publicacion.getTema(),
                autor,
                publicacion.getFechaCreacion(),
                respuestasCount,
                publicacion.getImagenUrl() // <-- NUEVO
        );
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

    private String construirPreview(String contenido) {
        if (contenido == null) return "";
        if (contenido.length() <= PREVIEW_MAX) return contenido;
        return contenido.substring(0, PREVIEW_MAX) + "...";
    }
}