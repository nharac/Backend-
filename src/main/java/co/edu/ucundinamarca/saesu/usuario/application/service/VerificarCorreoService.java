package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.VerificarCorreoRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CorreoYaRegistradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort.Pendiente;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Doble verificación al crear la cuenta.
 * El registro NO crea el usuario: guarda los datos en registros_pendientes y envía un código de 6 dígitos.
 * La cuenta se crea en usuarios solo cuando el estudiante escribe el código correcto.
 * Mismas reglas que la recuperación de contraseña: vence en 15 minutos, 5 intentos y 60 s entre códigos.
 */
@Service
public class VerificarCorreoService {

    static final int MINUTOS_VIGENCIA = 15;
    static final int MAX_INTENTOS = 5;
    static final int SEGUNDOS_ENTRE_CODIGOS = 60;
    static final String MENSAJE_SIN_REGISTRO = "No encontramos un registro pendiente para ese correo. Vuelve a crear la cuenta.";

    private static final Logger log = LoggerFactory.getLogger(VerificarCorreoService.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final RegistroPendientePort pendientes;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public VerificarCorreoService(UsuarioRepositoryPort usuarioRepository, RegistroPendientePort pendientes,
                                  PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.pendientes = pendientes;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    /** Paso 1 (registro): guarda los datos pendientes y envía el código. No crea la cuenta. */
    @Transactional
    public void solicitar(RegistroRequest request) {
        String correo = limpiar(request.getCorreo());
        if (usuarioRepository.existePorCorreo(correo)) {
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }
        pendientes.limpiarAbandonados();

        // Si acaba de pedir un código, el que tiene sigue sirviendo: no se cambia ni se reenvía
        var previo = pendientes.buscar(correo);
        if (previo.isPresent() && previo.get().vigente() && previo.get().segundosDesdeEnvio() < SEGUNDOS_ENTRE_CODIGOS) {
            return;
        }

        String codigo = RecuperarPasswordService.generarCodigo();
        String nombre = request.getNombre().trim();
        pendientes.guardar(correo, nombre, passwordEncoder.encode(request.getPassword()),
                passwordEncoder.encode(codigo), MINUTOS_VIGENCIA);
        enviar(correo, nombre, codigo);
    }

    /** "Reenviar código": no dice si hay un registro pendiente para ese correo. */
    @Transactional
    public void reenviar(String correo) {
        pendientes.buscar(limpiar(correo))
                .filter(p -> p.segundosDesdeEnvio() >= SEGUNDOS_ENTRE_CODIGOS)
                .ifPresent(p -> {
                    String codigo = RecuperarPasswordService.generarCodigo();
                    pendientes.renovarCodigo(p.id(), passwordEncoder.encode(codigo), MINUTOS_VIGENCIA);
                    enviar(p.correo(), p.nombre(), codigo);
                });
    }

    /** Paso 2: con el código correcto se crea la cuenta y se abre la sesión (igual que el login). */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponse verificar(VerificarCorreoRequest request) {
        String correo = limpiar(request.correo());
        Pendiente p = pendientes.buscar(correo)
                .orElseThrow(() -> new IllegalArgumentException(usuarioRepository.existePorCorreo(correo)
                        ? "Esta cuenta ya está activa. Inicia sesión."
                        : MENSAJE_SIN_REGISTRO));

        if (!p.vigente()) {
            throw new IllegalArgumentException("El código venció. Pide uno nuevo.");
        }
        if (p.intentos() >= MAX_INTENTOS) {
            throw new IllegalArgumentException("Demasiados intentos fallidos. Pide un código nuevo.");
        }
        if (!passwordEncoder.matches(request.codigo().trim(), p.codigoHash())) {
            pendientes.sumarIntento(p.id());
            int restantes = MAX_INTENTOS - (p.intentos() + 1);
            throw new IllegalArgumentException(restantes <= 0
                    ? "Demasiados intentos fallidos. Pide un código nuevo."
                    : "Código incorrecto. Te queda" + (restantes == 1 ? " 1 intento." : "n " + restantes + " intentos."));
        }
        if (usuarioRepository.existePorCorreo(p.correo())) {
            pendientes.eliminar(p.id());
            throw new CorreoYaRegistradoException("El correo ya está registrado");
        }

        // Recién ahora se crea la cuenta
        Usuario nuevo = new Usuario();
        nuevo.setNombre(p.nombre());
        nuevo.setCorreo(p.correo());
        nuevo.setPassword(p.passwordHash());
        nuevo.setRol("ESTUDIANTE");
        nuevo.setFechaRegistro(LocalDateTime.now());
        Usuario guardado = usuarioRepository.guardar(nuevo);
        pendientes.eliminar(p.id());

        try {
            emailService.enviarCorreoBienvenida(guardado.getCorreo(), guardado.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo de bienvenida a {}", guardado.getCorreo());
        }

        return new AuthResponse(
                new UsuarioResponse(guardado.getId(), guardado.getNombre(), guardado.getCorreo(), guardado.getRol(),
                        guardado.getFechaRegistro()),
                jwtService.generarToken(guardado.getCorreo(), guardado.getRol()),
                jwtService.generarRefreshToken(guardado.getCorreo()));
    }

    private void enviar(String correo, String nombre, String codigo) {
        try {
            emailService.enviarCodigoVerificacion(correo, nombre, codigo, MINUTOS_VIGENCIA);
        } catch (Exception e) {
            log.error("No se pudo enviar el código de verificación a {}", correo, e);
        }
    }

    private static String limpiar(String correo) {
        return correo == null ? "" : correo.trim();
    }
}
