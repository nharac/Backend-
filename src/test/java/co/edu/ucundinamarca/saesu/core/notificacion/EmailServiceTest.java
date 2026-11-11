package co.edu.ucundinamarca.saesu.core.notificacion;

import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private final JavaMailSender sender = mock(JavaMailSender.class);
    private final EmailService email = new EmailService(sender, "saesu@gmail.com", "SAESU", "https://saesu.app/",
            java.time.Clock.systemUTC(), co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);

    EmailServiceTest() {
        when(sender.createMimeMessage()).thenAnswer(inv -> new MimeMessage(Session.getInstance(new Properties())));
    }

    private MimeMessage enviado() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender).send(captor.capture());
        return captor.getValue();
    }

    /** Vuelve a leer el correo tal como saldría y devuelve la parte del tipo pedido, ya decodificada. */
    private static String parte(MimeMessage m, String tipo) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.writeTo(out);
        return buscar(new MimeMessage(Session.getInstance(new Properties()), new ByteArrayInputStream(out.toByteArray())), tipo);
    }

    private static String buscar(Part p, String tipo) throws Exception {
        if (p.isMimeType(tipo)) {
            return ((String) p.getContent()).replace("\r\n", "\n");
        }
        if (p.getContent() instanceof Multipart mp) {
            for (int i = 0; i < mp.getCount(); i++) {
                String r = buscar(mp.getBodyPart(i), tipo);
                if (r != null) return r;
            }
        }
        return null;
    }

    private static void sencillo(String html) {
        // Nada de lo que suelen castigar los filtros de spam
        assertThat(html).doesNotContain("<img").doesNotContain("cid:").doesNotContain("<script")
                .doesNotContainPattern("[\\x{1F000}-\\x{1FAFF}\\x{2600}-\\x{27BF}]");
        assertThat(html.length()).isLessThan(3000);
    }

    @Test
    void codigoDeVerificacionEsCortoConElCodigoResaltado() throws Exception {
        email.enviarCodigoVerificacion("a@ucundinamarca.edu.co", "Pedro", "907315", 15);

        MimeMessage m = enviado();
        assertThat(((InternetAddress) m.getFrom()[0]).getAddress()).isEqualTo("saesu@gmail.com");
        assertThat(((InternetAddress) m.getFrom()[0]).getPersonal()).isEqualTo("SAESU");
        assertThat(m.getSubject()).isEqualTo("Tu código de SAESU");
        assertThat(parte(m, "text/plain")).isEqualTo("Tu código de SAESU es: 907315\nVence en 15 minutos.");
        String h = parte(m, "text/html");
        assertThat(h).contains("SAESU").contains(">907315</p>").contains("Vence en 15 minutos.")
                .doesNotContain("Pedro").doesNotContain("href");
        sencillo(h);
    }

    @Test
    void bienvenidaConElPrimerNombreYElEnlace() throws Exception {
        email.enviarCorreoBienvenida("laura@ucundinamarca.edu.co", "  Laura Gómez ");

        MimeMessage m = enviado();
        assertThat(m.getSubject()).isEqualTo("Bienvenido a SAESU");
        assertThat(m.getAllRecipients()[0].toString()).isEqualTo("laura@ucundinamarca.edu.co");
        assertThat(parte(m, "text/plain")).startsWith("Hola Laura,").contains("Entra en: https://saesu.app\n");
        String h = parte(m, "text/html");
        assertThat(h).contains("¡Bienvenido, Laura!").contains("href=\"https://saesu.app\"");
        sencillo(h);
    }

    @Test
    void codigoDeRecuperacionEsCorto() throws Exception {
        email.enviarCodigoRecuperacion("a@ucundinamarca.edu.co", null, "482915", 10);

        MimeMessage m = enviado();
        assertThat(m.getSubject()).isEqualTo("Tu código de SAESU");
        assertThat(parte(m, "text/plain")).contains("482915").contains("Vence en 10 minutos.");
        sencillo(parte(m, "text/html"));
    }

    @Test
    void nuevaRespuestaEscapaElTitulo() throws Exception {
        email.enviarCorreoNuevaRespuesta("a@ucundinamarca.edu.co", null, "<b>Límites</b> laterales");

        MimeMessage m = enviado();
        assertThat(m.getSubject()).isEqualTo("Nueva respuesta en tu publicación de SAESU");
        assertThat(parte(m, "text/plain")).startsWith("Hola estudiante,");
        assertThat(parte(m, "text/html")).contains("&lt;b&gt;L").doesNotContain("<b><b>");
    }

    @Test
    void avisoDeCambioDePasswordDiceCuandoYComo() throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-04T22:12:00Z"), ZoneOffset.UTC);
        EmailService conReloj = new EmailService(sender, "saesu@gmail.com", "SAESU", "https://saesu.app", reloj, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO);

        conReloj.enviarAvisoCambioPassword("ana@ucundinamarca.edu.co", "Ana Ruiz", true);
        conReloj.enviarAvisoCambioPassword("ana@ucundinamarca.edu.co", "Ana", false);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender, times(2)).send(captor.capture());
        MimeMessage conCodigo = captor.getAllValues().get(0);
        assertThat(conCodigo.getSubject()).isEqualTo("Tu contraseña de SAESU cambió");
        assertThat(parte(conCodigo, "text/plain")).startsWith("Hola Ana,")
                .contains("con un código de recuperación el miércoles 4 de noviembre de 2026, 5:12 p. m. (hora de Colombia)")
                .contains("https://saesu.app/auth/recuperar");
        assertThat(parte(conCodigo, "text/html")).contains("Hola, Ana").contains("href=\"https://saesu.app/auth/recuperar\"");
        assertThat(parte(captor.getAllValues().get(1), "text/plain")).contains("desde tu perfil");
    }

    @Test
    void fechaHoraColombiaEnEspanolYConAmPm() {
        ZonedDateTime medianoche = ZonedDateTime.parse("2026-11-09T05:05:00Z");   // lunes 0:05 en Colombia
        ZonedDateTime mediodia = ZonedDateTime.parse("2026-11-08T17:30:00Z");     // domingo 12:30 p. m.
        assertThat(EmailService.fechaHora(medianoche, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO)).isEqualTo("lunes 9 de noviembre de 2026, 12:05 a. m. (hora de Colombia)");
        assertThat(EmailService.fechaHora(mediodia, co.edu.ucundinamarca.saesu.core.config.TiempoConfig.ZONA_POR_DEFECTO)).isEqualTo("domingo 8 de noviembre de 2026, 12:30 p. m. (hora de Colombia)");
    }

    @Test
    void sinBarraFinalQuitaTodasLasBarras() {
        assertThat(EmailService.sinBarraFinal("https://saesu.app///")).isEqualTo("https://saesu.app");
        assertThat(EmailService.sinBarraFinal("")).isEmpty();
    }

    @Test
    void siGmailFallaSeLanzaUnErrorClaro() {
        doThrow(new MailSendException("SMTP caído")).when(sender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> email.enviarCodigoVerificacion("a@ucundinamarca.edu.co", "Pedro", "123456", 15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se pudo enviar el correo");
    }
}
