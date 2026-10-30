package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RestablecerPasswordRequest;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort.CodigoActivo;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Recuperación de contraseña en dos pasos:
 *  1. solicitar(correo): si la cuenta existe, se envía un código de 6 dígitos por correo.
 *  2. restablecer(correo, código, nueva): si el código es válido, se cambia la contraseña.
 *
 * Seguridad:
 *  - La respuesta del paso 1 es la misma exista o no la cuenta (no revela qué correos están registrados).
 *  - El código se guarda cifrado con BCrypt, vence en {@value #MINUTOS_VIGENCIA} minutos y sirve una sola vez.
 *  - Después de {@value #MAX_INTENTOS} intentos fallidos el código deja de servir.
 *  - No se envía otro código antes de {@value #SEGUNDOS_ENTRE_CODIGOS} segundos (evita llenar el correo de alguien).
 */
@Service
public class RecuperarPasswordService {

    /** Hora de Colombia: las diferencias de tiempo se calculan con zona (regla S8700). */
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    static final int MINUTOS_VIGENCIA = 15;
    static final int MAX_INTENTOS = 5;
    static final int SEGUNDOS_ENTRE_CODIGOS = 60;

    static final String MENSAJE_CODIGO_INVALIDO = "El código no es válido o ya venció. Solicita uno nuevo.";

    private static final Logger log = LoggerFactory.getLogger(RecuperarPasswordService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepositoryPort usuarioRepository;
    private final CodigoRecuperacionPort codigoPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Clock clock;

    @Autowired
    public RecuperarPasswordService(UsuarioRepositoryPort usuarioRepository, CodigoRecuperacionPort codigoPort,
                                    PasswordEncoder passwordEncoder, EmailService emailService) {
        this(usuarioRepository, codigoPort, passwordEncoder, emailService, Clock.systemDefaultZone());
    }

    RecuperarPasswordService(UsuarioRepositoryPort usuarioRepository, CodigoRecuperacionPort codigoPort,
                             PasswordEncoder passwordEncoder, EmailService emailService, Clock clock) {
        this.usuarioRepository = usuarioRepository;
        this.codigoPort = codigoPort;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.clock = clock;
    }

    /** Paso 1. No lanza error si el correo no existe (para no revelar cuentas). */
    @Transactional
    public void solicitar(String correo) {
        Optional<Usuario> usuario = usuarioRepository.buscarPorCorreo(limpiar(correo));
        if (usuario.isEmpty()) {
            // Mismo trabajo (BCrypt) que con una cuenta real: así el tiempo de respuesta no revela si el correo existe
            passwordEncoder.encode(generarCodigo());
            return;
        }
        Long id = usuario.get().getId();

        Optional<LocalDateTime> ultimo = codigoPort.fechaUltimoCodigo(id);
        if (ultimo.isPresent()
                && Duration.between(ultimo.get().atZone(ZONA), LocalDateTime.now(clock).atZone(ZONA)).getSeconds() < SEGUNDOS_ENTRE_CODIGOS) {
            return; // ya se envió uno hace menos de un minuto
        }

        String codigo = generarCodigo();
        codigoPort.invalidarActivos(id);
        codigoPort.guardar(id, passwordEncoder.encode(codigo), MINUTOS_VIGENCIA);

        try {
            emailService.enviarCodigoRecuperacion(usuario.get().getCorreo(), usuario.get().getNombre(), codigo, MINUTOS_VIGENCIA);
        } catch (Exception e) {
            log.error("No se pudo enviar el código de recuperación a {}", RequestLoggingFilter.enmascarar(usuario.get().getCorreo()), e);
        }
    }

    /** Paso 2. Lanza IllegalArgumentException (400) con un mensaje para el usuario si algo no cuadra. */
    /**
     * Paso 2: solo comprueba que el código sea correcto, sin gastarlo, para que el frontend
     * muestre el formulario de la contraseña nueva únicamente con un código válido.
     * Un código incorrecto cuenta como intento fallido.
     */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void validarCodigo(String correo, String codigo) {
        comprobar(correo, codigo);
    }

    /** Paso 3: vuelve a comprobar el código (el servidor no confía en el paso 2) y cambia la contraseña. */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void restablecer(RestablecerPasswordRequest request) {
        Comprobado ok = comprobar(request.correo(), request.codigo());
        Usuario usuario = ok.usuario();

        codigoPort.actualizarPassword(usuario.getId(), passwordEncoder.encode(request.passwordNueva()));
        codigoPort.marcarUsado(ok.codigo().id());
        codigoPort.invalidarActivos(usuario.getId());

        try {
            emailService.enviarAvisoCambioPassword(usuario.getCorreo(), usuario.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo enviar el aviso de cambio de contraseña a {}", RequestLoggingFilter.enmascarar(usuario.getCorreo()));
        }
    }

    private record Comprobado(Usuario usuario, CodigoActivo codigo) {}

    /** Lanza IllegalArgumentException (400) con un mensaje para el usuario si el código no sirve. */
    private Comprobado comprobar(String correo, String codigo) {
        Usuario usuario = usuarioRepository.buscarPorCorreo(limpiar(correo))
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_CODIGO_INVALIDO));

        CodigoActivo activo = codigoPort.buscarActivo(usuario.getId(), MAX_INTENTOS)
                .orElseThrow(() -> new IllegalArgumentException(MENSAJE_CODIGO_INVALIDO));

        if (!passwordEncoder.matches(codigo == null ? "" : codigo.trim(), activo.codigoHash())) {
            codigoPort.sumarIntento(activo.id());
            int restantes = MAX_INTENTOS - (activo.intentos() + 1);
            throw new IllegalArgumentException(restantes <= 0
                    ? "Demasiados intentos fallidos. Solicita un código nuevo."
                    : "Código incorrecto. Te queda" + (restantes == 1 ? " 1 intento." : "n " + restantes + " intentos."));
        }
        return new Comprobado(usuario, activo);
    }

    /** Código de 6 dígitos (con ceros a la izquierda) generado con SecureRandom. */
    static String generarCodigo() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private static String limpiar(String correo) {
        return correo == null ? "" : correo.trim();
    }
}
