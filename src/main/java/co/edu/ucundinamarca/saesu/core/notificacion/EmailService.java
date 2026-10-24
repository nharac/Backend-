package co.edu.ucundinamarca.saesu.core.notificacion;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
}