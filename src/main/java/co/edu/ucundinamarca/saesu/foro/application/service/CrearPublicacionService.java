package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.request.CrearPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.ports.input.CrearPublicacionUseCase;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CrearPublicacionService implements CrearPublicacionUseCase {

    private final PublicacionRepositoryPort publicacionRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    @Transactional
    public PublicacionDetalleResponse crear(CrearPublicacionRequest request, String correoAutor) {
        Usuario autor = usuarioRepository.buscarPorCorreo(correoAutor)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Publicacion publicacion = new Publicacion(
                null,
                autor.getId(),
                request.getTitulo().trim(),
                request.getContenido().trim(),
                request.getTema(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                request.getImagenUrl() // <-- NUEVO
        );

        Publicacion guardada = publicacionRepository.guardar(publicacion);

        AutorResponse autorDto = new AutorResponse(
                autor.getId(),
                autor.getNombre(),
                calcularIniciales(autor.getNombre()),
                autor.getRol()
        );

        return new PublicacionDetalleResponse(
                guardada.getId(),
                guardada.getTitulo(),
                guardada.getContenido(),
                guardada.getTema(),
                autorDto,
                guardada.getFechaCreacion(),
                Collections.emptyList(),
                guardada.getImagenUrl() // <-- NUEVO
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