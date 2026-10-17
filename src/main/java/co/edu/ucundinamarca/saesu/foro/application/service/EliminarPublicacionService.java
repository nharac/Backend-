package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EliminarPublicacionService {

    private final PublicacionRepositoryPort publicacionRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    public void ejecutar(Long publicacionId, String correoAutenticado) {
        // 1. Buscamos la publicación
        Publicacion publicacion = publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // 2. Buscamos al usuario que intenta borrar
        Usuario usuarioLogueado = usuarioRepository.buscarPorCorreo(correoAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Validamos permisos (autor o docente)
        boolean esAutor = publicacion.getAutorId().equals(usuarioLogueado.getId());
        boolean esDocente = "DOCENTE".equals(usuarioLogueado.getRol());

        if (!esAutor && !esDocente) {
            throw new AccessDeniedException("No tienes permisos para eliminar esta publicación");
        }

        // 4. Eliminamos
        publicacionRepository.eliminar(publicacionId);
    }
}