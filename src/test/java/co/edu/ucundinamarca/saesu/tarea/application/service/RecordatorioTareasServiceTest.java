package co.edu.ucundinamarca.saesu.tarea.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.Pendiente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecordatorioTareasServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 20);

    @Test
    void recuerdaSoloLasIncompletasYUnaVezAlDia() {
        TareaPort port = mock(TareaPort.class);
        EmailService correo = mock(EmailService.class);
        NotificarUseCase avisos = mock(NotificarUseCase.class);
        RecordatorioTareasService s = new RecordatorioTareasService(port, ZoneId.of("America/Bogota"));
        s.setCorreo(correo);
        s.setAvisos(avisos);

        Pendiente vencehoy = new Pendiente(1L, "Repaso", "algebra", 5, HOY, 10L, "ana@u.edu.co", "Ana Ruiz", 2);
        Pendiente completa = new Pendiente(2L, "Límites", "limites", 3, HOY.plusDays(1), 10L, "ana@u.edu.co", "Ana Ruiz", 3);
        Pendiente yaAvisada = new Pendiente(3L, "Integrales", "integrales", 4, HOY.plusDays(1), 11L, "beto@u.edu.co", "Beto", 0);
        when(port.asignacionesQueVencen(HOY, HOY.plusDays(1))).thenReturn(List.of(vencehoy, completa, yaAvisada));
        when(port.marcarRecordatorio(1L, 10L, HOY)).thenReturn(true);
        when(port.marcarRecordatorio(3L, 11L, HOY)).thenReturn(false);

        assertThat(s.recordar(HOY)).isEqualTo(1);

        verify(correo).enviarRecordatorioTarea("ana@u.edu.co", "Ana Ruiz", "Repaso", "Álgebra", 3, true);
        verify(avisos).notificar(eq(10L), eq("RECORDATORIO"), contains("vence hoy"), anyString(), eq("/dashboard"));
        verify(port, never()).marcarRecordatorio(2L, 10L, HOY);
    }
}
