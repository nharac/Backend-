package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.LoginRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CredencialesInvalidasException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.LoginUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(UsuarioRepositoryPort usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String correo = RequestLoggingFilter.enmascarar(request.getCorreo());
        Usuario usuario = usuarioRepository.buscarPorCorreo(request.getCorreo())
                .orElseThrow(() -> {
                    log.warn("Login fallido: el correo {} no está registrado", correo);
                    return new CredencialesInvalidasException("Credenciales inválidas");
                });

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("Login fallido: contraseña incorrecta para {}", correo);
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }
        log.info("Login correcto: {} (rol {})", correo, usuario.getRol());

        String token = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
        String refreshToken = jwtService.generarRefreshToken(usuario.getCorreo()); 
        
        return new AuthResponse(mapearAResponse(usuario), token, refreshToken); // <-- refreshToken
    }

    private UsuarioResponse mapearAResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.getFechaRegistro());
    }
}