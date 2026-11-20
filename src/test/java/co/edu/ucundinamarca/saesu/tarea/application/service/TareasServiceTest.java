package co.edu.ucundinamarca.saesu.tarea.application.service;

import co.edu.ucundinamarca.saesu.tarea.application.dto.request.TareaRequest;
import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaDocenteResponse;
import co.edu.ucundinamarca.saesu.tarea.application.dto.response.TareaEstudianteResponse;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaDocente;
import co.edu.ucundinamarca.saesu.tarea.ports.output.TareaPort.FilaTareaEstudiante;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareasServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDate HOY = LocalDate.of(2026, 11, 20);

    @Mock private TareaPort port;
    private TareasService servicio;

    @BeforeEach
    void preparar() {
        servicio = new TareasService(port, Clock.fixed(HOY.atTime(10, 0).atZone(BOGOTA).toInstant(), BOGOTA));
    }

    private static TareaRequest request(Long estudianteId) {
        return new TareaRequest("Repaso", null, "algebra", 5, HOY.plusDays(3), estudianteId);
    }

    @Test
    @DisplayName("Crea una tarea para todo el grupo y la devuelve con su avance")
    void crearParaElGrupo() {
        when(port.idPorCorreo("profe@u.edu.co")).thenReturn(Optional.of(1L));
        when(port.crear(any(), eq(1L), any())).thenReturn(7L);
        when(port.delDocente(1L)).thenReturn(List.of(new FilaTareaDocente(7L, "Repaso", null, "algebra", 5, HOY.plusDays(3),
                null, null, 4, 1, LocalDateTime.of(2026, 11, 20, 10, 0))));

        TareaDocenteResponse t = servicio.crear(request(null), "profe@u.edu.co");

        assertThat(t.asignadaA()).isEqualTo("Todo el grupo");
        assertThat(t.temaNombre()).isEqualTo("Álgebra");
        assertThat(t.completadas()).isEqualTo(1);
        assertThat(t.vencida()).isFalse();
        verify(port, never()).esEstudiante(any());
    }

    @Test
    @DisplayName("No deja asignar a alguien que no es estudiante")
    void estudianteInvalido() {
        when(port.esEstudiante(99L)).thenReturn(false);
        assertThatThrownBy(() -> servicio.crear(request(99L), "profe@u.edu.co")).isInstanceOf(IllegalArgumentException.class);
        verify(port, never()).crear(any(), any(), any());
    }

    @Test
    @DisplayName("Mis tareas: pendientes primero, luego vencidas y al final completadas")
    void misTareasOrdenadas() {
        when(port.idPorCorreo("e@u.edu.co")).thenReturn(Optional.of(5L));
        when(port.delEstudiante(5L)).thenReturn(List.of(
                new FilaTareaEstudiante(1L, "Hecha", null, "limites", 3, HOY.minusDays(1), "Profe", 4),
                new FilaTareaEstudiante(2L, "Vencida", null, "algebra", 5, HOY.minusDays(2), "Profe", 1),
                new FilaTareaEstudiante(3L, "Pendiente", null, "trigonometria", 5, HOY.plusDays(2), "Profe", 2)));

        List<TareaEstudianteResponse> t = servicio.misTareas("e@u.edu.co");

        assertThat(t).extracting(TareaEstudianteResponse::titulo).containsExactly("Pendiente", "Vencida", "Hecha");
        assertThat(t.get(0).diasRestantes()).isEqualTo(2);
        assertThat(t.get(1).vencida()).isTrue();
        assertThat(t.get(2).completada()).isTrue();
        assertThat(t.get(2).resueltas()).isEqualTo(3);
    }

    @Test
    @DisplayName("Usuario desconocido: 404")
    void usuarioDesconocido() {
        when(port.idPorCorreo("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.misTareas("x")).isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("Eliminar delega con el id del docente")
    void eliminar() {
        when(port.idPorCorreo("profe@u.edu.co")).thenReturn(Optional.of(1L));
        when(port.eliminar(7L, 1L)).thenReturn(true);
        assertThat(servicio.eliminar(7L, "profe@u.edu.co")).isTrue();
    }
}
