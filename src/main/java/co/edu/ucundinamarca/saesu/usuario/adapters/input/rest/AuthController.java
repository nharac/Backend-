package co.edu.ucundinamarca.saesu.usuario.adapters.input.rest;

import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.ActualizarPerfilRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.LoginRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ActualizarPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.LoginUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.ObtenerPerfilUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.input.RegistrarUsuarioUseCase;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Autenticación", description = "Endpoints de registro, login y perfil de usuario")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final LoginUseCase loginUseCase;
    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;
    // NUEVAS DEPENDENCIAS
    private final JwtService jwtService;
    private final UsuarioRepositoryPort usuarioRepository;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                          LoginUseCase loginUseCase,
                          ObtenerPerfilUseCase obtenerPerfilUseCase,
                          ActualizarPerfilUseCase actualizarPerfilUseCase,
                          JwtService jwtService,
                          UsuarioRepositoryPort usuarioRepository) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.loginUseCase = loginUseCase;
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(
            summary = "Registrar estudiante",
            description = "Crea un nuevo usuario con rol ESTUDIANTE. Devuelve el usuario creado y un JWT."
    )
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrarUsuarioUseCase.registrar(request));
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario (estudiante o docente) y devuelve un JWT."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.login(request));
    }

    @Operation(
            summary = "Obtener perfil",
            description = "Devuelve el perfil del usuario autenticado. Requiere JWT."
    )
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> perfil(Authentication authentication) {
        return ResponseEntity.ok(obtenerPerfilUseCase.obtenerPerfil(authentication.getName()));
    }

    @Operation(
            summary = "Actualizar perfil",
            description = "Actualiza el nombre del usuario autenticado y, si se envían passwordActual y passwordNueva, "
                    + "también su contraseña. El correo y el rol no se pueden cambiar. Requiere JWT."
    )
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(actualizarPerfilUseCase.actualizarPerfil(authentication.getName(), request));
    }

    @Operation(summary = "Refrescar Token", description = "Obtiene un nuevo JWT usando un Refresh Token válido.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        if (!jwtService.validarToken(refreshToken) || !jwtService.esRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String correo = jwtService.extraerCorreo(refreshToken);
        Usuario usuario = usuarioRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoToken = jwtService.generarToken(usuario.getCorreo(), usuario.getRol());
        
        return ResponseEntity.ok(new AuthResponse(
                new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol(), usuario.getFechaRegistro()),
                nuevoToken,
                refreshToken // Devolvemos el mismo refresh token
        ));
    }
}