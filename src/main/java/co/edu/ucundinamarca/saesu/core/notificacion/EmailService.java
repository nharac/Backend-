package co.edu.ucundinamarca.saesu.core.notificacion;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

/**
 * Correos de SAESU en HTML (con versión de texto plano para clientes que no muestran HTML).
 * Todos se envían en segundo plano (@Async, ver AsyncConfig).
 * El remitente se muestra como "SAESU" aunque la cuenta sea un Gmail (MAIL_USERNAME).
 * Las animaciones son CSS: Apple Mail y la app de iOS las muestran; Gmail y Outlook las ignoran
 * y ven el mismo correo quieto, sin que se rompa nada.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String nombreRemitente;
    private final String urlApp;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String remitente,
                        @Value("${mail.remitente.nombre:SAESU}") String nombreRemitente,
                        @Value("${app.url:http://localhost}") String urlApp) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
        this.urlApp = sinBarraFinal(urlApp);
    }

    @Async
    public void enviarCorreoBienvenida(String destinatario, String nombre) {
        String n = primerNombre(nombre);
        String cuerpo = """
                <p class="t" style="margin:0 0 6px;font-size:26px;font-weight:800;color:#1f2f3f">¡Bienvenido, %s! <span class="ola">👋</span></p>
                <p style="margin:0 0 22px;font-size:15px;line-height:1.6;color:#4b5563">Tu cuenta en <b>SAESU</b> ya está lista. Aquí vas a prepararte para el ICFES de cálculo a tu ritmo, con ayuda cuando la necesites.</p>
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin:0 0 24px">
                  %s%s%s%s
                </table>
                %s
                <p style="margin:22px 0 0;font-size:13px;color:#6b7280;text-align:center">Tu primera respuesta correcta vale <b style="color:#059669">+20 puntos</b>. ¡A por el ranking! 🏆</p>
                """.formatted(esc(n),
                fila("📝", "Práctica tipo ICFES", "Límites, derivadas e integrales con pistas cuando te equivocas."),
                fila("🤖", "Tutor con IA", "Te guía con preguntas para que llegues tú a la respuesta."),
                fila("💬", "Foro colaborativo", "Pregunta, ayuda a tus compañeros y recibe respuestas oficiales."),
                fila("🔥", "Puntos, racha y logros", "Practica cada día, sube en el ranking y gana medallas."),
                boton("Empezar a practicar", urlApp + "/practica"));
        enviar(destinatario, "¡Bienvenido a SAESU, " + n + "! 🎓", "🎓", "Tu cuenta está lista", cuerpo,
                "Hola " + n + ",\n\nTu cuenta en SAESU ya está lista. Practica tipo ICFES, pregúntale al tutor IA "
                        + "y participa en el foro.\n\nEmpieza aquí: " + urlApp + "/practica\n\nEl equipo de SAESU");
    }

    @Async
    public void enviarCorreoNuevaRespuesta(String destinatario, String nombre, String tituloPublicacion) {
        String n = primerNombre(nombre);
        String cuerpo = """
                <p style="margin:0 0 6px;font-size:22px;font-weight:800;color:#1f2f3f">¡Te respondieron, %s! 💬</p>
                <p style="margin:0 0 18px;font-size:15px;line-height:1.6;color:#4b5563">Alguien respondió tu publicación:</p>
                <p style="margin:0 0 24px;padding:14px 16px;border-left:4px solid #4f46e5;background:#eef2ff;border-radius:8px;font-size:15px;font-weight:600;color:#312e81">%s</p>
                %s
                """.formatted(esc(n), esc(tituloPublicacion), boton("Ver la respuesta", urlApp + "/foro"));
        enviar(destinatario, "Nueva respuesta en tu publicación de SAESU 💬", "💬", "Foro colaborativo", cuerpo,
                "Hola " + n + ",\n\nAlguien respondió tu publicación: '" + tituloPublicacion + "'.\n"
                        + "Entra a SAESU para verla: " + urlApp + "/foro\n\nEl equipo de SAESU");
    }

    /** Código de 6 dígitos para restablecer la contraseña. */
    @Async
    public void enviarCodigoRecuperacion(String destinatario, String nombre, String codigo, int minutos) {
        String n = primerNombre(nombre);
        String cuerpo = """
                <p style="margin:0 0 6px;font-size:24px;font-weight:800;color:#1f2f3f">Hola, %s <span class="ola">🔑</span></p>
                <p style="margin:0 0 22px;font-size:15px;line-height:1.6;color:#4b5563">Recibimos una solicitud para restablecer tu contraseña. Escribe este código en SAESU:</p>
                <div style="padding:22px 10px;border-radius:16px;background:#eef2ff;text-align:center">
                  <table role="presentation" cellpadding="0" cellspacing="0" align="center"><tr>%s</tr></table>
                  <p style="margin:14px 0 0;font-size:13px;color:#4338ca">⏱️ Vence en <b>%d minutos</b> y solo sirve una vez</p>
                </div>
                <p style="margin:22px 0 0;padding:12px 14px;border-radius:10px;background:#fffbeb;font-size:13px;line-height:1.5;color:#92400e">🛡️ ¿No fuiste tú? Ignora este correo: tu contraseña no cambia. Nadie de SAESU te pedirá este código.</p>
                """.formatted(esc(n), casillas(codigo), minutos);
        enviar(destinatario, "Tu código de SAESU: " + codigo + " 🔑", "🔐", "Recupera tu contraseña", cuerpo,
                "Hola " + n + ",\n\nTu código para restablecer la contraseña de SAESU es: " + codigo + "\n\n"
                        + "Vence en " + minutos + " minutos y solo sirve una vez.\n"
                        + "Si no fuiste tú, ignora este correo.\n\nEl equipo de SAESU");
    }

    /** Código de 6 dígitos para activar la cuenta (doble verificación al registrarse). */
    @Async
    public void enviarCodigoVerificacion(String destinatario, String nombre, String codigo, int minutos) {
        String n = primerNombre(nombre);
        String cuerpo = """
                <p style="margin:0 0 6px;font-size:24px;font-weight:800;color:#1f2f3f">¡Ya casi, %s! <span class="ola">🚀</span></p>
                <p style="margin:0 0 22px;font-size:15px;line-height:1.6;color:#4b5563">Para activar tu cuenta de <b>SAESU</b> y confirmar que este correo institucional es tuyo, escribe este código:</p>
                <div style="padding:22px 10px;border-radius:16px;background:#ecfdf5;text-align:center">
                  <table role="presentation" cellpadding="0" cellspacing="0" align="center"><tr>%s</tr></table>
                  <p style="margin:14px 0 0;font-size:13px;color:#047857">⏱️ Vence en <b>%d minutos</b> · si vence, pide otro desde SAESU</p>
                </div>
                <p style="margin:22px 0 0;font-size:14px;line-height:1.6;color:#4b5563;text-align:center">Después de activarla tendrás práctica tipo ICFES, tutor con IA y el foro listos para ti 🎓</p>
                <p style="margin:18px 0 0;padding:12px 14px;border-radius:10px;background:#fffbeb;font-size:13px;line-height:1.5;color:#92400e">🛡️ ¿No creaste una cuenta en SAESU? Ignora este correo y la cuenta no se activará.</p>
                """.formatted(esc(n), casillas(codigo), minutos);
        enviar(destinatario, "Activa tu cuenta de SAESU: " + codigo + " ✉️", "✉️", "Confirma tu correo", cuerpo,
                "Hola " + n + ",\n\nTu código para activar tu cuenta de SAESU es: " + codigo + "\n\n"
                        + "Vence en " + minutos + " minutos.\n"
                        + "Si no creaste una cuenta, ignora este correo.\n\nEl equipo de SAESU");
    }

    /** Aviso de seguridad después de cambiar la contraseña. */
    @Async
    public void enviarAvisoCambioPassword(String destinatario, String nombre) {
        String n = primerNombre(nombre);
        String cuerpo = """
                <p style="margin:0 0 6px;font-size:22px;font-weight:800;color:#1f2f3f">Tu contraseña cambió ✅</p>
                <p style="margin:0 0 18px;font-size:15px;line-height:1.6;color:#4b5563">Hola %s, la contraseña de tu cuenta de SAESU se acaba de cambiar. Si fuiste tú, no tienes que hacer nada.</p>
                <p style="margin:0 0 24px;padding:12px 14px;border-radius:10px;background:#fef2f2;font-size:13px;line-height:1.5;color:#991b1b">⚠️ ¿No fuiste tú? Restablécela de inmediato desde «¿Olvidaste tu contraseña?» y avísale a tu docente.</p>
                %s
                """.formatted(esc(n), boton("Iniciar sesión", urlApp + "/auth"));
        enviar(destinatario, "Tu contraseña de SAESU fue cambiada 🔒", "🔒", "Aviso de seguridad", cuerpo,
                "Hola " + n + ",\n\nLa contraseña de tu cuenta de SAESU se acaba de cambiar.\n"
                        + "Si no fuiste tú, restablécela desde \"¿Olvidaste tu contraseña?\" y avísale a tu docente.\n\n"
                        + "El equipo de SAESU");
    }

    // ============================================
    // Plantilla común
    // ============================================

    private void enviar(String para, String asunto, String icono, String etiqueta, String cuerpoHtml, String texto) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(remitente, nombreRemitente, StandardCharsets.UTF_8.name()));
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(texto, plantilla(icono, etiqueta, cuerpoHtml));
            mailSender.send(mensaje);
            // Nunca se registra el asunto: los de verificación y recuperación llevan el código
            log.info("Correo \"{}\" enviado a {}", etiqueta, RequestLoggingFilter.enmascarar(para));
        } catch (Exception e) {
            log.error("No se pudo enviar el correo \"{}\" a {}: {}", etiqueta, RequestLoggingFilter.enmascarar(para), e.getMessage());
            throw new IllegalStateException("No se pudo enviar el correo", e);
        }
    }

    private String plantilla(String icono, String etiqueta, String cuerpo) {
        return """
                <!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width">
                <style>
                  @keyframes entrar{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:none}}
                  @keyframes pop{0%%{transform:scale(.5);opacity:0}70%%{transform:scale(1.12)}100%%{transform:scale(1);opacity:1}}
                  @keyframes ola{0%%,100%%{transform:rotate(0)}25%%{transform:rotate(18deg)}75%%{transform:rotate(-12deg)}}
                  .tarjeta{animation:entrar .6s ease-out both}
                  .icono{animation:pop .7s cubic-bezier(.34,1.56,.64,1) .2s both}
                  .ola{display:inline-block;animation:ola 1.4s ease-in-out .8s 2}
                  .d{animation:pop .5s cubic-bezier(.34,1.56,.64,1) both}
                  @media (prefers-reduced-motion:reduce){.tarjeta,.icono,.ola,.d{animation:none}}
                </style></head>
                <body style="margin:0;padding:0;background:#eef2ff;font-family:Poppins,'Segoe UI',Arial,sans-serif">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#eef2ff;padding:28px 12px"><tr><td align="center">
                  <table role="presentation" class="tarjeta" width="100%%" cellpadding="0" cellspacing="0" style="max-width:560px;background:#ffffff;border-radius:24px;overflow:hidden;box-shadow:0 12px 32px rgba(79,70,229,.15)">
                    <tr><td style="background:#4f46e5;background-image:linear-gradient(135deg,#4f46e5,#7c3aed);padding:28px 24px;text-align:center">
                      <div class="icono" style="display:inline-block;width:64px;height:64px;line-height:64px;border-radius:20px;background:rgba(255,255,255,.18);font-size:32px">%s</div>
                      <p style="margin:12px 0 0;font-size:22px;font-weight:800;letter-spacing:1px;color:#ffffff">∫ SAESU</p>
                      <p style="margin:2px 0 0;font-size:13px;color:#e0e7ff">%s</p>
                    </td></tr>
                    <tr><td style="padding:30px 28px">%s</td></tr>
                    <tr><td style="padding:18px 28px;background:#f9fafb;text-align:center;font-size:12px;line-height:1.5;color:#9ca3af">
                      SAESU · Universidad de Cundinamarca<br>Este correo se envió automáticamente, no lo respondas.
                    </td></tr>
                  </table>
                </td></tr></table>
                </body></html>
                """.formatted(icono, esc(etiqueta), cuerpo);
    }

    /** Los 6 dígitos del código en casillas grandes que aparecen una tras otra. */
    private static String casillas(String codigo) {
        StringBuilder casillas = new StringBuilder();
        for (int i = 0; i < codigo.length(); i++) {
            casillas.append("<td class=\"d\" style=\"animation-delay:").append(i * 90).append("ms;padding:0 3px\">")
                    .append("<div style=\"width:44px;height:54px;line-height:54px;border-radius:12px;background:#ffffff;")
                    .append("border:2px solid #c7d2fe;font-size:28px;font-weight:800;color:#4f46e5;text-align:center;")
                    .append("font-family:'Courier New',monospace\">").append(esc(codigo.substring(i, i + 1)))
                    .append("</div></td>");
        }
        return casillas.toString();
    }

    private static String fila(String emoji, String titulo, String texto) {
        return """
                <tr><td style="padding:0 0 12px"><table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f9fafb;border-radius:14px"><tr>
                  <td width="52" style="padding:12px 0 12px 14px;font-size:26px;vertical-align:middle">%s</td>
                  <td style="padding:12px 14px 12px 6px"><b style="font-size:14px;color:#1f2f3f">%s</b><br><span style="font-size:13px;color:#6b7280">%s</span></td>
                </tr></table></td></tr>
                """.formatted(emoji, esc(titulo), esc(texto));
    }

    private static String boton(String texto, String url) {
        return """
                <table role="presentation" cellpadding="0" cellspacing="0" align="center"><tr><td style="border-radius:12px;background:#4f46e5">
                  <a href="%s" style="display:inline-block;padding:14px 30px;font-size:15px;font-weight:700;color:#ffffff;text-decoration:none;border-radius:12px">%s →</a>
                </td></tr></table>
                """.formatted(esc(url), esc(texto));
    }

    /** "https://saesu.app///" → "https://saesu.app" (sin regex: evita backtracking, regla S8786). */
    static String sinBarraFinal(String url) {
        int fin = url.length();
        while (fin > 0 && url.charAt(fin - 1) == '/') {
            fin--;
        }
        return url.substring(0, fin);
    }

    private static String primerNombre(String nombre) {
        String limpio = nombre == null ? "" : nombre.trim();
        return limpio.isEmpty() ? "estudiante" : limpio.split("\\s+")[0];
    }

    private static String esc(String s) {
        return HtmlUtils.htmlEscape(s == null ? "" : s, StandardCharsets.UTF_8.name());
    }
}
