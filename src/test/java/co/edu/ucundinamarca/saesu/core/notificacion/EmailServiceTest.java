package co.edu.ucundinamarca.saesu.core.notificacion;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private final JavaMailSender sender = mock(JavaMailSender.class);
    private final EmailService email = new EmailService(sender, "saesu@ucundinamarca.edu.co", "SAESU", "https://saesu.app/");

    EmailServiceTest() {
        when(sender.createMimeMessage()).thenAnswer(inv -> new MimeMessage(Session.getInstance(new Properties())));
    }

    private MimeMessage enviado() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender).send(captor.capture());
        return captor.getValue();
    }

    private static String contenido(MimeMessage m) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.writeTo(out);
        return out.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Test
    void bienvenidaUsaElPrimerNombreYElEnlaceDeLaApp() throws Exception {
        email.enviarCorreoBienvenida("laura@ucundinamarca.edu.co", "  Laura Gómez ");

        MimeMessage m = enviado();
        assertThat(m.getSubject()).contains("Laura").doesNotContain("Gómez");
        assertThat(((InternetAddress) m.getFrom()[0]).getAddress()).isEqualTo("saesu@ucundinamarca.edu.co");
        assertThat(m.getAllRecipients()[0].toString()).isEqualTo("laura@ucundinamarca.edu.co");
        assertThat(contenido(m)).contains("https://saesu.app/practica");
    }

    @Test
    void codigoDeRecuperacionVaEnElAsuntoYEnLasCasillas() throws Exception {
        email.enviarCodigoRecuperacion("a@ucundinamarca.edu.co", null, "482915", 15);

        MimeMessage m = enviado();
        assertThat(m.getSubject()).contains("482915");
        assertThat(contenido(m)).contains("estudiante").contains("15 minutos");
    }

    @Test
    void codigoDeVerificacionAvisoDeCambioYNuevaRespuesta() throws Exception {
        email.enviarCodigoVerificacion("a@ucundinamarca.edu.co", "Pedro", "123456", 10);
        email.enviarAvisoCambioPassword("a@ucundinamarca.edu.co", "Pedro");
        email.enviarCorreoNuevaRespuesta("a@ucundinamarca.edu.co", "Pedro", "<b>Límites</b> laterales");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(sender, org.mockito.Mockito.times(3)).send(captor.capture());
        assertThat(captor.getAllValues().get(0).getSubject()).contains("123456");
        assertThat(captor.getAllValues().get(1).getSubject()).contains("contraseña");
        // El título del usuario se escapa en el HTML
        assertThat(contenido(captor.getAllValues().get(2))).contains("&lt;b&gt;L");
    }

    @Test
    void siGmailFallaSeLanzaUnErrorClaro() {
        doThrow(new MailSendException("SMTP caído")).when(sender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> email.enviarAvisoCambioPassword("a@ucundinamarca.edu.co", "Pedro"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se pudo enviar el correo");
    }
}
