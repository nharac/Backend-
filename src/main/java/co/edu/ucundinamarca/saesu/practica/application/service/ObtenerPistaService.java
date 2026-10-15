package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.PistaResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.ports.input.ObtenerPistaUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: obtener la pista de una pregunta.
 * NUNCA revela la respuesta correcta.
 */
@Service
@RequiredArgsConstructor
public class ObtenerPistaService implements ObtenerPistaUseCase {

    private final PreguntaRepositoryPort preguntaRepository;

    @Override
    public PistaResponse obtenerPista(Long preguntaId) {
        Pregunta pregunta = preguntaRepository.buscarPorId(preguntaId)
                .orElseThrow(() -> new PreguntaNoEncontradaException(
                        "Pregunta no encontrada con id: " + preguntaId));

        String mensaje = pregunta.getPista() != null
                ? "Pista cargada correctamente"
                : "No hay pista disponible para esta pregunta";

        return new PistaResponse(
                pregunta.getId(),
                pregunta.getPista(),
                mensaje
        );
    }
}