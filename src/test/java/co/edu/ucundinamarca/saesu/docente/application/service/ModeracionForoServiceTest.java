package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Ocultar y restaurar contenido del foro (docente). */
@ExtendWith(MockitoExtension.class)
class ModeracionForoServiceTest {

    @Mock private DocenteRepositoryPort docenteRepository;
    @Mock private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks private ModeracionForoService servicio;

    @Test
    @DisplayName("Ocultar una respuesta guarda qué docente lo hizo")
    void ocultarRespuesta() {
        when(usuarioRepository.buscarPorCorreo("profe@u.edu.co")).thenReturn(Optional.of(
                new Usuario(9L, "Profe", "profe@u.edu.co", "x", "DOCENTE", LocalDateTime.now(), null)));
        when(docenteRepository.ocultarRespuesta(100L, 9L)).thenReturn(true);

        servicio.ocultarRespuesta(100L, "profe@u.edu.co");

        verify(docenteRepository).ocultarRespuesta(100L, 9L);
    }

    @Test
    @DisplayName("Ocultar una respuesta que no existe: 404")
    void ocultarRespuestaNoExiste() {
        when(docenteRepository.ocultarRespuesta(anyLong(), any())).thenReturn(false);
        assertThatThrownBy(() -> servicio.ocultarRespuesta(1L, "profe@u.edu.co"))
                .isInstanceOf(RespuestaNoEncontradaException.class);
    }

    @Test
    @DisplayName("Eliminar para siempre publicaciones y respuestas; si no existen: 404")
    void eliminar() {
        when(docenteRepository.eliminarPublicacion(5L)).thenReturn(true);
        when(docenteRepository.eliminarRespuesta(8L)).thenReturn(true);
        servicio.eliminarPublicacion(5L);
        servicio.eliminarRespuesta(8L);
        verify(docenteRepository).eliminarPublicacion(5L);
        verify(docenteRepository).eliminarRespuesta(8L);

        when(docenteRepository.eliminarPublicacion(6L)).thenReturn(false);
        when(docenteRepository.eliminarRespuesta(9L)).thenReturn(false);
        assertThatThrownBy(() -> servicio.eliminarPublicacion(6L)).isInstanceOf(PublicacionNoEncontradaException.class);
        assertThatThrownBy(() -> servicio.eliminarRespuesta(9L)).isInstanceOf(RespuestaNoEncontradaException.class);
    }

    @Test
    @DisplayName("Restaurar una publicación que no existe: 404")
    void restaurarPublicacionNoExiste() {
        when(docenteRepository.restaurarPublicacion(5L)).thenReturn(false);
        assertThatThrownBy(() -> servicio.restaurarPublicacion(5L))
                .isInstanceOf(PublicacionNoEncontradaException.class);
    }
}
