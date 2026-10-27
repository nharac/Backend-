package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest.OpcionRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.PreguntaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.ports.output.BancoPreguntasPort;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Validaciones del banco de preguntas administrado por el docente. */
@ExtendWith(MockitoExtension.class)
class BancoPreguntasServiceTest {

    @Mock private BancoPreguntasPort bancoPort;
    @Mock private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks private BancoPreguntasService servicio;

    private static List<OpcionRequest> opciones(String correcta) {
        return List.of("A", "B", "C", "D").stream()
                .map(l -> new OpcionRequest(l, "Opción " + l, l.equals(correcta)))
                .toList();
    }

    private static PreguntaDocenteRequest pregunta(List<OpcionRequest> opciones) {
        return new PreguntaDocenteRequest("derivadas", "¿Cuál es la derivada de x⁵?", "basico", "Regla de la potencia", opciones);
    }

    private static PreguntaDocenteResponse respuesta(Long id) {
        return new PreguntaDocenteResponse(id, "derivadas", "¿Cuál es la derivada de x⁵?", "basico", null,
                true, 0, 0.0, LocalDateTime.now(), List.of());
    }

    @Test
    @DisplayName("Crea la pregunta guardando qué docente la creó")
    void crear() {
        when(bancoPort.existeEnunciado(anyString(), isNull())).thenReturn(false);
        when(usuarioRepository.buscarPorCorreo("profe@u.edu.co")).thenReturn(Optional.of(
                new Usuario(9L, "Profe", "profe@u.edu.co", "x", "DOCENTE", LocalDateTime.now(), null)));
        when(bancoPort.crear(any(), eq(9L))).thenReturn(50L);
        when(bancoPort.buscarPorId(50L)).thenReturn(Optional.of(respuesta(50L)));

        PreguntaDocenteResponse creada = servicio.crear(pregunta(opciones("B")), "profe@u.edu.co");

        assertThat(creada.id()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Rechaza si no hay ninguna opción correcta o hay más de una")
    void unaSolaCorrecta() {
        assertThatThrownBy(() -> servicio.validar(pregunta(opciones("Z")), null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("una opción correcta");

        List<OpcionRequest> dos = List.of(
                new OpcionRequest("A", "1", true), new OpcionRequest("B", "2", true),
                new OpcionRequest("C", "3", false), new OpcionRequest("D", "4", false));
        assertThatThrownBy(() -> servicio.validar(pregunta(dos), null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("una opción correcta");
    }

    @Test
    @DisplayName("Rechaza letras repetidas o distintas de A-D")
    void letras() {
        List<OpcionRequest> repetidas = List.of(
                new OpcionRequest("A", "1", true), new OpcionRequest("A", "2", false),
                new OpcionRequest("C", "3", false), new OpcionRequest("D", "4", false));
        assertThatThrownBy(() -> servicio.validar(pregunta(repetidas), null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("A, B, C y D");
    }

    @Test
    @DisplayName("Rechaza opciones con el mismo texto")
    void textosRepetidos() {
        List<OpcionRequest> iguales = List.of(
                new OpcionRequest("A", "5x⁴", true), new OpcionRequest("B", "5X⁴ ", false),
                new OpcionRequest("C", "x⁴", false), new OpcionRequest("D", "4x⁵", false));
        assertThatThrownBy(() -> servicio.validar(pregunta(iguales), null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("diferentes");
    }

    @Test
    @DisplayName("Rechaza un enunciado que ya existe en otra pregunta")
    void enunciadoDuplicado() {
        when(bancoPort.existeEnunciado(anyString(), eq(3L))).thenReturn(true);
        assertThatThrownBy(() -> servicio.validar(pregunta(opciones("A")), 3L))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Ya existe");
    }

    @Test
    @DisplayName("Editar una pregunta que no existe: 404 y no se toca la BD")
    void editarNoExiste() {
        when(bancoPort.buscarPorId(77L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.actualizar(77L, pregunta(opciones("A"))))
                .isInstanceOf(PreguntaNoEncontradaException.class);
        verify(bancoPort, never()).actualizar(anyLong(), any());
    }

    @Test
    @DisplayName("Desactivar una pregunta que no existe: 404")
    void desactivarNoExiste() {
        when(bancoPort.cambiarEstado(anyLong(), anyBoolean())).thenReturn(false);
        assertThatThrownBy(() -> servicio.cambiarEstado(5L, false)).isInstanceOf(PreguntaNoEncontradaException.class);
    }

    @Test
    @DisplayName("El filtro de estado se traduce a activa = true / false / null")
    void filtroEstado() {
        servicio.listar(null, null, null, "activas");
        servicio.listar(null, null, null, "inactivas");
        servicio.listar(null, null, null, "todas");
        verify(bancoPort).listar(null, null, null, Boolean.TRUE);
        verify(bancoPort).listar(null, null, null, Boolean.FALSE);
        verify(bancoPort).listar(null, null, null, null);
    }
}
