package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService; // <-- NUEVO
import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CorreoYaRegistradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.RegistrarUsuarioUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService; // <-- NUEVO

    public RegistrarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService,
                                   EmailService emailService) { // <-- emailService agregado al constructor
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existePorCorreo(request.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevo.setRol("ESTUDIANTE");
        nuevo.setFechaRegistro(LocalDateTime.now());

        Usuario guardado = usuarioRepository.guardar(nuevo);
        
        String token = jwtService.generarToken(guardado.getCorreo(), guardado.getRol());
        String refreshToken = jwtService.generarRefreshToken(guardado.getCorreo()); 

        // Enviamos el correo (en un bloque try-catch por si falla la red, no dañe el registro)
        try {
            emailService.enviarCorreoBienvenida(guardado.getCorreo(), guardado.getNombre());
        } catch (Exception e) {
            System.err.println("Error enviando correo de bienvenida: " + e.getMessage());
        }

        return new AuthResponse(mapearAResponse(guardado), token, refreshToken); 
    }

    private UsuarioResponse mapearAResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.getFechaRegistro());
    }
}