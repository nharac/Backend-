package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.ContenidoOcultoResponse;
import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Moderación del foro por parte del docente.
 * Nada se borra: lo oculto deja de verse en el foro y se puede restaurar desde el panel de moderación.
 */
@Service
@RequiredArgsConstructor
public class ModeracionForoService {

    private final DocenteRepositoryPort docenteRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Transactional
    public void ocultarRespuesta(Long respuestaId, String correoDocente) {
        if (!docenteRepository.ocultarRespuesta(respuestaId, idDocente(correoDocente))) {
            throw new RespuestaNoEncontradaException("Respuesta no encontrada con id: " + respuestaId);
        }
    }

    @Transactional
    public void restaurarRespuesta(Long respuestaId) {
        if (!docenteRepository.restaurarRespuesta(respuestaId)) {
            throw new RespuestaNoEncontradaException("Respuesta no encontrada con id: " + respuestaId);
        }
    }

    @Transactional
    public void ocultarPublicacion(Long publicacionId, String correoDocente) {
        if (!docenteRepository.ocultarPublicacion(publicacionId, idDocente(correoDocente))) {
            throw new PublicacionNoEncontradaException("Publicación no encontrada con id: " + publicacionId);
        }
    }

    @Transactional
    public void restaurarPublicacion(Long publicacionId) {
        if (!docenteRepository.restaurarPublicacion(publicacionId)) {
            throw new PublicacionNoEncontradaException("Publicación no encontrada con id: " + publicacionId);
        }
    }

    public List<ContenidoOcultoResponse> listarOculto() {
        return docenteRepository.listarContenidoOculto();
    }

    /** Si no se encuentra el docente se guarda null (igual queda oculto); en la práctica siempre existe. */
    private Long idDocente(String correo) {
        return usuarioRepository.buscarPorCorreo(correo).map(Usuario::getId).orElse(null);
    }
}
