package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ProgresoResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ProgresoResponse.ProgresoTema;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerProgresoUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.RespuestaEstudianteRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: obtener el progreso del estudiante por tema.
 */
@Service
@RequiredArgsConstructor
public class ObtenerProgresoService implements ObtenerProgresoUseCase {

    private final RespuestaEstudianteRepositoryPort respuestaRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    public ProgresoResponse obtenerProgreso(String correoEstudiante) {
        Long usuarioId = usuarioRepository.buscarPorCorreo(correoEstudiante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();

        List<Object[]> resultados = respuestaRepository.contarPorTema(usuarioId);

        ProgresoTema limites = new ProgresoTema(0, 0, 0.0);
        ProgresoTema derivadas = new ProgresoTema(0, 0, 0.0);
        ProgresoTema integrales = new ProgresoTema(0, 0, 0.0);

        for (Object[] fila : resultados) {
            String tema = (String) fila[0];
            long respondidas = ((Number) fila[1]).longValue();
            long correctas = ((Number) fila[2]).longValue();
            double porcentaje = respondidas > 0 ? (correctas * 100.0) / respondidas : 0.0;

            ProgresoTema progreso = new ProgresoTema(respondidas, correctas, porcentaje);

            switch (tema) {
                case "limites" -> limites = progreso;
                case "derivadas" -> derivadas = progreso;
                case "integrales" -> integrales = progreso;
            }
        }

        return new ProgresoResponse(limites, derivadas, integrales);
    }
}
