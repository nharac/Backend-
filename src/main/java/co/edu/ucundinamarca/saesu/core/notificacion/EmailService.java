package co.edu.ucundinamarca.saesu.core.notificacion;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Correos de SAESU cortos, con texto plano y una versión HTML sencilla (sin imágenes, botones ni emojis),
 * para que los filtros de spam de la universidad no los retengan.
 * Todos se envían en segundo plano (@Async, ver AsyncConfig).
 * El remitente se muestra como "SAESU" (MAIL_FROM_NAME) con el correo MAIL_FROM, o MAIL_USERNAME si no se define.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");
    private static final String FIRMA = "El equipo de SAESU - Universidad de Cundinamarca";

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String nombreRemitente;
    private final String urlApp;
    private final Clock clock;

    @Autowired
    public EmailService(JavaMailSender mailSender,
                        @Value("${mail.remitente.correo}") String remitente,
                        @Value("${mail.remitente.nombre:SAESU}") String nombreRemitente,
                        @Value("${app.url:http://localhost}") String urlApp) {
        this(mailSender, remitente, nombreRemitente, urlApp, Clock.system(COLOMBIA));
    }

    /** Constructor para pruebas (reloj fijo). */
    EmailService(JavaMailSender mailSender, String remitente, String nombreRemitente, String urlApp, Clock clock) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
        this.urlApp = sinBarraFinal(urlApp);
        this.clock = clock;
    }

    @Async
    public void enviarCorreoBienvenida(String destinatario, String nombre) {
        String n = primerNombre(nombre);
        enviar(destinatario, "Bienvenido a SAESU", "Bienvenida",
                "Hola " + n + ",\n\n"
                        + "Tu cuenta en SAESU ha sido creada exitosamente.\n"
                        + "Aquí podrás practicar cálculo, resolver tus dudas en el foro y aprender con la tutora Sumi.\n\n"
                        + "Entra en: " + urlApp + "\n\n"
                        + "¡Mucho éxito en tu semestre!\n" + FIRMA,
                titulo("¡Bienvenido, " + n + "!")
                        + parrafo("Tu cuenta en SAESU ha sido creada exitosamente.")
                        + parrafo("Aquí podrás practicar cálculo, resolver tus dudas en el foro y aprender con la tutora Sumi.")
                        + parrafo("Entra en: <a href=\"" + esc(urlApp) + "\" style=\"color:" + INDIGO + "\">" + esc(urlApp) + "</a>")
                        + parrafo("¡Mucho éxito en tu semestre!"));
    }

    @Async
    public void enviarCorreoNuevaRespuesta(String destinatario, String nombre, String tituloPublicacion) {
        String n = primerNombre(nombre);
        enviar(destinatario, "Nueva respuesta en tu publicación de SAESU", "Foro colaborativo",
                "Hola " + n + ",\n\n"
                        + "Alguien respondió a tu publicación: \"" + tituloPublicacion + "\".\n"
                        + "Entra a SAESU para ver la respuesta.\n\n" + FIRMA,
                titulo("Hola, " + n)
                        + parrafo("Alguien respondió a tu publicación: <b>" + esc(tituloPublicacion) + "</b>.")
                        + parrafo("Entra a SAESU para ver la respuesta."));
    }

    @Async
    public void enviarCodigoRecuperacion(String destinatario, String nombre, String codigo, int minutos) {
        enviar(destinatario, "Tu código de SAESU", "Recupera tu contraseña",
                "Tu código para cambiar la contraseña de SAESU es: " + codigo + "\n"
                        + "Vence en " + minutos + " minutos.",
                parrafo("Tu código para cambiar la contraseña de SAESU es:") + caja(codigo)
                        + parrafo("Vence en " + minutos + " minutos."));
    }

    @Async
    public void enviarCodigoVerificacion(String destinatario, String nombre, String codigo, int minutos) {
        enviar(destinatario, "Tu código de SAESU", "Confirma tu correo",
                "Tu código de SAESU es: " + codigo + "\nVence en " + minutos + " minutos.",
                parrafo("Tu código de SAESU es:") + caja(codigo) + parrafo("Vence en " + minutos + " minutos."));
    }

    /** Aviso después de cambiar la contraseña (desde el perfil o con un código de recuperación). */
    @Async
    public void enviarAvisoCambioPassword(String destinatario, String nombre, boolean conCodigoDeRecuperacion) {
        String n = primerNombre(nombre);
        String como = conCodigoDeRecuperacion ? "con un código de recuperación" : "desde tu perfil";
        String cuando = fechaHoraColombia(ZonedDateTime.now(clock));
        String recuperar = urlApp + "/auth/recuperar";
        enviar(destinatario, "Tu contraseña de SAESU cambió", "Aviso de seguridad",
                "Hola " + n + ",\n\n"
                        + "La contraseña de tu cuenta de SAESU se cambió " + como + " el " + cuando + ".\n\n"
                        + "Si fuiste tú, no tienes que hacer nada.\n"
                        + "Si no fuiste tú, restablécela en " + recuperar + "\n\n" + FIRMA,
                titulo("Hola, " + n)
                        + parrafo("La contraseña de tu cuenta de SAESU se cambió " + esc(como) + " el " + esc(cuando) + ".")
                        + parrafo("Si fuiste tú, no tienes que hacer nada.")
                        + parrafo("Si no fuiste tú, restablécela en <a href=\"" + esc(recuperar) + "\" style=\"color:" + INDIGO + "\">"
                                + esc(recuperar) + "</a>."));
    }

    private static final String[] DIAS = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
    private static final String[] MESES = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto",
            "septiembre", "octubre", "noviembre", "diciembre"};

    /** "miércoles 4 de noviembre de 2026, 5:12 p. m. (hora de Colombia)", sin depender del idioma del servidor. */
    static String fechaHoraColombia(ZonedDateTime momento) {
        ZonedDateTime t = momento.withZoneSameInstant(COLOMBIA);
        int hora12 = t.getHour() % 12 == 0 ? 12 : t.getHour() % 12;
        return "%s %d de %s de %d, %d:%02d %s (hora de Colombia)".formatted(
                DIAS[t.getDayOfWeek().getValue() - 1], t.getDayOfMonth(), MESES[t.getMonthValue() - 1], t.getYear(),
                hora12, t.getMinute(), t.getHour() < 12 ? "a. m." : "p. m.");
    }

    private void enviar(String para, String asunto, String etiqueta, String texto, String cuerpoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(remitente, nombreRemitente, StandardCharsets.UTF_8.name()));
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(texto, plantilla(cuerpoHtml));
            mailSender.send(mensaje);
            // Nunca se registra el contenido: los de verificación y recuperación llevan el código
            log.info("Correo \"{}\" enviado a {}", etiqueta, RequestLoggingFilter.enmascarar(para));
        } catch (Exception e) {
            log.error("No se pudo enviar el correo \"{}\" a {}: {}", etiqueta, RequestLoggingFilter.enmascarar(para), e.getMessage());
            throw new IllegalStateException("No se pudo enviar el correo", e);
        }
    }

    // ============================================
    // Diseño sencillo: solo HTML con estilos en línea, sin imágenes, scripts ni botones,
    // para que los filtros de spam (Microsoft 365 de la universidad) no lo retengan.
    // ============================================

    private static final String INDIGO = "#4f46e5";

    static String plantilla(String cuerpo) {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"margin:0;padding:24px 12px;background:#f5f3ff;font-family:Arial,Helvetica,sans-serif\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:480px;"
                + "background:#ffffff;border-radius:12px;border-top:4px solid " + INDIGO + "\">"
                + "<tr><td style=\"padding:24px 28px 8px;font-size:20px;font-weight:bold;color:" + INDIGO + ";letter-spacing:1px\">SAESU</td></tr>"
                + "<tr><td style=\"padding:8px 28px 20px\">" + cuerpo + "</td></tr>"
                + "<tr><td style=\"padding:14px 28px;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280\">"
                + FIRMA + "</td></tr>"
                + "</table></td></tr></table></body></html>";
    }

    private static String titulo(String texto) {
        return "<p style=\"margin:0 0 12px;font-size:18px;font-weight:bold;color:#1f2937\">" + esc(texto) + "</p>";
    }

    /** Párrafo con HTML ya escapado por quien lo llama. */
    private static String parrafo(String html) {
        return "<p style=\"margin:0 0 12px;font-size:15px;line-height:1.5;color:#374151\">" + html + "</p>";
    }

    private static String caja(String codigo) {
        return "<p style=\"margin:4px 0 16px;padding:14px 0;border-radius:10px;background:#eef2ff;border:1px solid #c7d2fe;"
                + "text-align:center;font-family:'Courier New',monospace;font-size:30px;font-weight:bold;letter-spacing:6px;color:"
                + INDIGO + "\">" + esc(codigo) + "</p>";
    }

    private static String esc(String s) {
        return HtmlUtils.htmlEscape(s == null ? "" : s, StandardCharsets.UTF_8.name());
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
}
