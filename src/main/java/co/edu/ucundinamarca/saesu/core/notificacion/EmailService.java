package co.edu.ucundinamarca.saesu.core.notificacion;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Correos de SAESU. Todos se envían en segundo plano (@Async, ver AsyncConfig). */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarCorreoBienvenida(String destinatario, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a SAESU! 🎓");
        mensaje.setText("Hola " + nombre + ",\n\n"
                + "¡Qué alegría tenerte en SAESU! Tu cuenta ha sido creada exitosamente.\n"
                + "Aquí podrás practicar matemáticas, resolver tus dudas en el foro y aprender con nuestra IA.\n\n"
                + "¡Mucho éxito en tu semestre!\n"
                + "El equipo de SAESU - Universidad de Cundinamarca");

        mailSender.send(mensaje);
    }

    @Async
    public void enviarCorreoNuevaRespuesta(String destinatario, String nombre, String tituloPublicacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Nueva respuesta en tu publicación de SAESU 💬");
        mensaje.setText("Hola " + nombre + ",\n\n"
                + "Alguien ha respondido a tu publicación: '" + tituloPublicacion + "'.\n"
                + "¡Entra a SAESU para ver la respuesta!\n\n"
                + "El equipo de SAESU");
        
        mailSender.send(mensaje);
    }

    /** Código de 6 dígitos para restablecer la contraseña. */
    @Async
    public void enviarCodigoRecuperacion(String destinatario, String nombre, String codigo, int minutos) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Tu código para restablecer la contraseña de SAESU: " + codigo);
        mensaje.setText("Hola " + nombre + ",\n\n"
                + "Recibimos una solicitud para restablecer la contraseña de tu cuenta de SAESU.\n\n"
                + "Tu código es:   " + String.join(" ", codigo.split("")) + "\n\n"
                + "Vence en " + minutos + " minutos y solo sirve una vez.\n\n"
                + "Si no fuiste tú, ignora este correo: tu contraseña no cambiará.\n\n"
                + "El equipo de SAESU - Universidad de Cundinamarca");
        mailSender.send(mensaje);
    }

    /** Aviso de seguridad después de cambiar la contraseña. */
    @Async
    public void enviarAvisoCambioPassword(String destinatario, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Tu contraseña de SAESU fue cambiada 🔒");
        mensaje.setText("Hola " + nombre + ",\n\n"
                + "La contraseña de tu cuenta de SAESU se acaba de cambiar.\n\n"
                + "Si fuiste tú, no tienes que hacer nada.\n"
                + "Si no fuiste tú, restablécela de inmediato desde \"¿Olvidaste tu contraseña?\" "
                + "y avísale a tu docente.\n\n"
                + "El equipo de SAESU - Universidad de Cundinamarca");
        mailSender.send(mensaje);
    }
}
