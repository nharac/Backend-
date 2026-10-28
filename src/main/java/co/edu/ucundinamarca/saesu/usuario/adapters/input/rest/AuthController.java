package co.edu.ucundinamarca.saesu.usuario.adapters.input.rest;

import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.ActualizarPerfilRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.LoginRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.RegistroResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.VerificarCorreoRequest;
import co.edu.ucundinamarca.saesu.usuario.application.service.VerificarCorreoService;
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
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RecuperarPasswordRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RestablecerPasswordRequest;
import co.edu.ucundinamarca.saesu.usuario.application.service.RecuperarPasswordService;
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
    private final RecuperarPasswordService recuperarPasswordService;
    private final VerificarCorreoService verificarCorreoService;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                          LoginUseCase loginUseCase,
                          ObtenerPerfilUseCase obtenerPerfilUseCase,
                          ActualizarPerfilUseCase actualizarPerfilUseCase,
                          JwtService jwtService,
                          UsuarioRepositoryPort usuarioRepository,
                          RecuperarPasswordService recuperarPasswordService,
                          VerificarCorreoService verificarCorreoService) {
        this.recuperarPasswordService = recuperarPasswordService;
        this.verificarCorreoService = verificarCorreoService;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.loginUseCase = loginUseCase;
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(
            summary = "Registrar estudiante",
            description = "Todavía NO crea la cuenta: guarda el registro como pendiente y envía un código de 6 dígitos al correo "
                    + "institucional. La cuenta (rol ESTUDIANTE) se crea en POST /api/auth/verificar-correo."
    )
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrarUsuarioUseCase.registrar(request));
    }

    @Operation(
            summary = "Verificar correo (doble verificación)",
            description = "Valida el código de 6 dígitos enviado al registrarse (15 min, 5 intentos). Si es correcto crea la cuenta "
                    + "y devuelve el usuario con su JWT, igual que el login."
    )
    @PostMapping("/verificar-correo")
    public ResponseEntity<AuthResponse> verificarCorreo(@Valid @RequestBody VerificarCorreoRequest request) {
        return ResponseEntity.ok(verificarCorreoService.verificar(request));
    }

    @Operation(
            summary = "Reenviar código de verificación",
            description = "Envía un código nuevo si hay un registro pendiente para ese correo (máximo uno por minuto). "
                    + "Siempre responde lo mismo para no revelar qué correos están registrados."
    )
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<java.util.Map<String, String>> reenviarVerificacion(@Valid @RequestBody RecuperarPasswordRequest request) {
        verificarCorreoService.reenviar(request.correo());
        return ResponseEntity.ok(java.util.Map.of("mensaje",
                "Si tienes un registro pendiente, te enviamos un código nuevo."));
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

    // ============================================
    // Recuperación de contraseña (públicos, con el límite de 10 por minuto de /api/auth)
    // ============================================

    @Operation(summary = "Solicitar código de recuperación",
            description = "Si el correo está registrado, envía un código de 6 dígitos que vence en 15 minutos. "
                    + "La respuesta es la misma exista o no la cuenta.")
    @PostMapping("/recuperar")
    public ResponseEntity<java.util.Map<String, String>> recuperar(@Valid @RequestBody RecuperarPasswordRequest request) {
        recuperarPasswordService.solicitar(request.correo());
        return ResponseEntity.ok(java.util.Map.of("mensaje",
                "Si el correo está registrado, te enviamos un código para restablecer tu contraseña."));
    }

    @Operation(
            summary = "Recuperar contraseña: validar código",
            description = "Comprueba el código de 6 dígitos sin gastarlo, para pasar al formulario de la contraseña nueva. "
                    + "Un código incorrecto cuenta como intento (máximo 5)."
    )
    @PostMapping("/recuperar/validar")
    public ResponseEntity<java.util.Map<String, String>> validarCodigoRecuperacion(@Valid @RequestBody VerificarCorreoRequest request) {
        recuperarPasswordService.validarCodigo(request.correo(), request.codigo());
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Código correcto. Ya puedes crear tu nueva contraseña."));
    }

    @Operation(summary = "Restablecer contraseña con el código recibido")
    @PostMapping("/restablecer")
    public ResponseEntity<java.util.Map<String, String>> restablecer(@Valid @RequestBody RestablecerPasswordRequest request) {
        recuperarPasswordService.restablecer(request);
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Tu contraseña fue actualizada. Ya puedes iniciar sesión."));
    }
}
