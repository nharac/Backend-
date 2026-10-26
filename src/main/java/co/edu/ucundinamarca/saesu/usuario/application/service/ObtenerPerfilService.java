package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc.LogrosJdbcRepository;

import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ObtenerPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ObtenerPerfilService implements ObtenerPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final LogrosJdbcRepository gamificacion;

    public ObtenerPerfilService(UsuarioRepositoryPort usuarioRepository, LogrosJdbcRepository gamificacion) {
        this.usuarioRepository = usuarioRepository;
        this.gamificacion = gamificacion;
    }

    @Override
    public UsuarioResponse obtenerPerfil(String correo) {
        Usuario usuario = usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        UsuarioResponse respuesta = new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getFechaRegistro()
        );
        respuesta.setFotoPerfilUrl(usuario.getFotoPerfilUrl());
        respuesta.setRachaActual((int) gamificacion.rachaActual(usuario.getId()));
        respuesta.setPuntosTotales(gamificacion.puntosTotales(usuario.getId()));
        return respuesta;
    }
}