package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionResumenPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Marcar respuesta oficial: una sola por publicación, se guarda qué docente la marcó (SCRUM-91). */
@ExtendWith(MockitoExtension.class)
class GestionDocenteServiceTest {

    private static final String CORREO_DOCENTE = "profe@ucundinamarca.edu.co";

    @Mock private DocenteRepositoryPort docenteRepository;
    @Mock private PublicacionResumenPort resumenPort;
    @Mock private RespuestaRepositoryPort respuestaRepository;
    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private PuntosService puntosService;

    @InjectMocks private GestionDocenteService servicio;

    private static Usuario usuario(Long id, String rol) {
        return new Usuario(id, "Nombre " + id, id + "@ucundinamarca.edu.co", "hash", rol, LocalDateTime.now(), null);
    }

    @BeforeEach
    void preparar() {
        lenient().when(usuarioRepository.buscarPorCorreo(CORREO_DOCENTE)).thenReturn(Optional.of(usuario(9L, "DOCENTE")));
    }

    @Test
    @DisplayName("Quita la oficial anterior, marca la nueva con el id del docente y premia al estudiante")
    void marcaOficial() {
        when(respuestaRepository.buscarPorId(100L))
                .thenReturn(Optional.of(new Respuesta(100L, 1L, 3L, null, "Es 2x", false, LocalDateTime.now())));
        when(usuarioRepository.buscarPorId(3L)).thenReturn(Optional.of(usuario(3L, "ESTUDIANTE")));

        servicio.marcarRespuestaOficial(100L, CORREO_DOCENTE);

        InOrder orden = inOrder(docenteRepository);
        orden.verify(docenteRepository).quitarOficialesDePublicacion(1L);
        orden.verify(docenteRepository).marcarOficial(100L, 9L);
        verify(puntosService).otorgarPuntosUnaVez(3L, 15, "Respuesta oficial #100");
    }

    @Test
    @DisplayName("Si la respuesta es de un docente, no recibe puntos")
    void docenteNoRecibePuntos() {
        when(respuestaRepository.buscarPorId(101L))
                .thenReturn(Optional.of(new Respuesta(101L, 1L, 9L, null, "Aplica la regla", false, LocalDateTime.now())));
        when(usuarioRepository.buscarPorId(9L)).thenReturn(Optional.of(usuario(9L, "DOCENTE")));

        servicio.marcarRespuestaOficial(101L, CORREO_DOCENTE);

        verify(docenteRepository).marcarOficial(101L, 9L);
        verify(puntosService, never()).otorgarPuntosUnaVez(anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Quitar la oficial delega en el repositorio")
    void quitaOficial() {
        servicio.quitarRespuestaOficial(100L);

        verify(docenteRepository).quitarOficial(100L);
    }
}
