package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * su contraseña (verificando primero la contraseña actual). Si cambia la contraseña,
 * se le envía un aviso de seguridad por correo.
 */
@Service
public class ActualizarPerfilService implements ActualizarPerfilUseCase {

    private static final Logger log = LoggerFactory.getLogger(ActualizarPerfilService.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ActualizarPerfilService(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

        // 3. Aviso de seguridad por correo si cambió la contraseña (si el correo falla, el cambio ya quedó hecho)
        if (nueva != null && !nueva.isBlank()) {
            try {
                emailService.enviarAvisoCambioPassword(guardado.getCorreo(), guardado.getNombre(), false);
            } catch (Exception e) {
                log.warn("No se pudo enviar el aviso de cambio de contraseña a {}",
                        RequestLoggingFilter.enmascarar(guardado.getCorreo()));
            }
        }

        return new UsuarioResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRol(),
                guardado.getFechaRegistro()
        );
    }
}
