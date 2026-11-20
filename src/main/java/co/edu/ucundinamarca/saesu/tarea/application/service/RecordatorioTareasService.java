package co.edu.ucundinamarca.saesu.tarea.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.Pendiente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Todos los días a las 7:00 (hora de Colombia) recuerda por correo y con la campanita las tareas que vencen
 * hoy o mañana y que el estudiante todavía no completa. Nunca envía dos veces el mismo recordatorio el mismo día.
 */
@Service
public class RecordatorioTareasService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioTareasService.class);

    private final TareaPort port;
    private final ZoneId zona;
    private EmailService correo;
    private NotificarUseCase avisos;

    public RecordatorioTareasService(TareaPort port, ZoneId zona) {
        this.port = port;
        this.zona = zona;
    }

    @Autowired(required = false)
    public void setCorreo(EmailService correo) {
        this.correo = correo;
    }

    @Autowired(required = false)
    public void setAvisos(NotificarUseCase avisos) {
        this.avisos = avisos;
    }

    @Scheduled(cron = "0 0 7 * * *", zone = "America/Bogota")
    public void recordarDiario() {
        recordar(LocalDate.now(zona));
    }

    /** Devuelve cuántos recordatorios se enviaron. */
    public int recordar(LocalDate hoy) {
        int enviados = 0;
        for (Pendiente p : port.asignacionesQueVencen(hoy, hoy.plusDays(1))) {
            long faltan = p.metaPreguntas() - p.resueltas();
            if (faltan <= 0 || !port.marcarRecordatorio(p.tareaId(), p.usuarioId(), hoy)) continue;
            boolean venceHoy = p.fechaLimite().equals(hoy);
            String tema = Tema.nombreDe(p.tema());
            if (avisos != null) {
                avisos.notificar(p.usuarioId(), "RECORDATORIO", "Tu tarea \"" + p.titulo() + "\" vence " + (venceHoy ? "hoy" : "mañana"),
                        "Te faltan " + faltan + " preguntas de " + tema + ".", "/dashboard");
            }
            if (correo != null && p.correo() != null) {
                try {
                    correo.enviarRecordatorioTarea(p.correo(), p.nombre(), p.titulo(), tema, (int) faltan, venceHoy);
                } catch (RuntimeException e) {
                    log.warn("No se pudo enviar el recordatorio de la tarea {}: {}", p.tareaId(), e.getMessage());
                }
            }
            enviados++;
        }
        if (enviados > 0) log.info("Recordatorios de tareas enviados: {}", enviados);
        return enviados;
    }
}
