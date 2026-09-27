package co.edu.ucundinamarca.saesu.usuario.application.service;

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

    public RegistrarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        return new AuthResponse(mapearAResponse(guardado), token);
    }

    private UsuarioResponse mapearAResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.getFechaRegistro());
    }
}