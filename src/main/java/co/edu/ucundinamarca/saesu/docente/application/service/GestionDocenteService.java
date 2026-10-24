package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.domain.exception.RespuestaOficialException;
import co.edu.ucundinamarca.saesu.docente.ports.input.GestionDocenteUseCase;
import co.edu.ucundinamarca.saesu.docente.ports.output.DocenteRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
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
    private final PublicacionRepositoryPort publicacionRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public List<PublicacionResumenResponse> listarPublicacionesSinOficial() {
        List<Publicacion> publicaciones = docenteRepository.listarSinRespuestaOficial();
        
        return publicaciones.stream().map(p -> {
            Usuario autor = usuarioRepository.buscarPorId(p.getAutorId()).orElseThrow();
            AutorResponse autorDto = new AutorResponse(
                autor.getId(), autor.getNombre(), 
                calcularIniciales(autor.getNombre()), autor.getRol()
            );
            long cant = respuestaRepository.contarPorPublicacion(p.getId());
            return new PublicacionResumenResponse(
                p.getId(), p.getTitulo(), p.getContenido().substring(0, Math.min(150, p.getContenido().length())) + "...",
                p.getTema(), autorDto, p.getFechaCreacion(), cant, p.getImagenUrl()
            );
        }).toList();
    }

    @Override
    @Transactional
    public void marcarRespuestaOficial(Long respuestaId, String correoDocente) {
        var respuesta = respuestaRepository.buscarPorId(respuestaId)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada"));

        // Regla: Solo 1 oficial por publicación. Quitamos las anteriores.
        docenteRepository.quitarOficialesDePublicacion(respuesta.getPublicacionId());
        
        // Marcamos la nueva
        docenteRepository.marcarOficial(respuestaId);
    }

    @Override
    @Transactional
    public void quitarRespuestaOficial(Long respuestaId) {
        docenteRepository.quitarOficial(respuestaId);
    }

    private String calcularIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        return partes.length == 1 ? partes[0].substring(0, 1).toUpperCase() : 
               (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }
    
}