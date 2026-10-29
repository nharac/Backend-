package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService;
import co.edu.ucundinamarca.saesu.docente.domain.exception.RespuestaOficialException;
import co.edu.ucundinamarca.saesu.docente.ports.input.GestionDocenteUseCase;
import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.application.service.ResumenForo;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionResumenPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionDocenteService implements GestionDocenteUseCase {

    private final DocenteRepositoryPort docenteRepository;
    private final PublicacionResumenPort resumenPort;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final PuntosService puntosService;

    /** Puntos para el estudiante cuando un docente marca su respuesta como oficial. */
    private static final int PUNTOS_RESPUESTA_OFICIAL = 15;

    /** Pendientes del docente: una sola consulta (antes eran ~4 por cada publicación del foro). */
    @Override
    public List<PublicacionResumenResponse> listarPublicacionesSinOficial() {
        return resumenPort.listarSinRespuestaOficial().stream()
                .map(ResumenForo::aResponse)
                .toList();
    }

    @Override
    @Transactional
    public void marcarRespuestaOficial(Long respuestaId, String correoDocente) {
        var respuesta = respuestaRepository.buscarPorId(respuestaId)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada"));

        // Regla: Solo 1 oficial por publicación. Quitamos las anteriores.
        docenteRepository.quitarOficialesDePublicacion(respuesta.getPublicacionId());
        
        // Marcamos la nueva guardando qué docente lo hizo (SCRUM-91)
        Long docenteId = usuarioRepository.buscarPorCorreo(correoDocente)
                .map(Usuario::getId)
                .orElse(null);
        docenteRepository.marcarOficial(respuestaId, docenteId);

        // Premio al estudiante autor (una sola vez por respuesta, aunque se marque y desmarque)
        usuarioRepository.buscarPorId(respuesta.getAutorId())
                .filter(autor -> "ESTUDIANTE".equalsIgnoreCase(String.valueOf(autor.getRol()).replace("ROLE_", "")))
                .ifPresent(autor -> puntosService.otorgarPuntosUnaVez(
                        autor.getId(), PUNTOS_RESPUESTA_OFICIAL, "Respuesta oficial #" + respuestaId));
    }

    @Override
    @Transactional
    public void quitarRespuestaOficial(Long respuestaId) {
        docenteRepository.quitarOficial(respuestaId);
    }
}
