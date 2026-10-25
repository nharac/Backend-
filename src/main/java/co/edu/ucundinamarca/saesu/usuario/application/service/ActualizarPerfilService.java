package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.ActualizarPerfilRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.ContrasenaIncorrectaException;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ActualizarPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: el usuario autenticado actualiza su nombre y, si lo pide,
 * su contraseña (verificando primero la contraseña actual).
 */
@Service
public class ActualizarPerfilService implements ActualizarPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ActualizarPerfilService(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponse actualizarPerfil(String correo, ActualizarPerfilRequest request) {
        Usuario usuario = usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        // 1. Nombre
        usuario.setNombre(request.getNombre().trim());

        // 2. Contraseña (solo si se envía una nueva)
        String nueva = request.getPasswordNueva();
        if (nueva != null && !nueva.isBlank()) {
            String actual = request.getPasswordActual();
            if (actual == null || actual.isBlank()) {
                throw new ContrasenaIncorrectaException("Debes ingresar tu contraseña actual para cambiarla");
            }
            if (!passwordEncoder.matches(actual, usuario.getPassword())) {
                throw new ContrasenaIncorrectaException("La contraseña actual no es correcta");
            }
            if (passwordEncoder.matches(nueva, usuario.getPassword())) {
                throw new ContrasenaIncorrectaException("La nueva contraseña debe ser diferente a la actual");
            }
            usuario.setPassword(passwordEncoder.encode(nueva));
        }

        Usuario guardado = usuarioRepository.guardar(usuario);

        return new UsuarioResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRol(),
                guardado.getFechaRegistro()
        );
    }
}
