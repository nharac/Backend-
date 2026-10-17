package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.OpcionResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PreguntaResponse;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.ports.input.ListarPreguntasUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: listar preguntas con filtros.
 * Aplica SRP: solo se encarga de listar y mapear a DTOs.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ListarPreguntasService implements ListarPreguntasUseCase {

    private final PreguntaRepositoryPort preguntaRepository;
    private final OpcionRepositoryPort opcionRepository;

    @Override
    @Cacheable(value = "preguntas", key = "#tema + '-' + #dificultad + '-' + #page + '-' + #size")
    public List<PreguntaResponse> listar(String tema, String dificultad, int page, int size) {
       
    
        
        List<Pregunta> preguntas = preguntaRepository.listar(tema, dificultad, page, size);

        return preguntas.stream()
                .map(this::convertirADto)
                .toList();
    }

    @Override
    @Cacheable(value = "contadorPreguntas", key = "#tema + '-' + #dificultad")
    public long contar(String tema, String dificultad) {
        return preguntaRepository.contar(tema, dificultad);
    }

    /**
     * Convierte una Pregunta de dominio a PreguntaResponse.
     * Carga las opciones asociadas y las mapea SIN es_correcta.
     */
    private PreguntaResponse convertirADto(Pregunta pregunta) {
        List<Opcion> opciones = opcionRepository.listarPorPreguntaId(pregunta.getId());

        List<OpcionResponse> opcionesDto = opciones.stream()
                .map(o -> new OpcionResponse(o.getId(), o.getLetra(), o.getTexto()))
                .toList();

        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getTema().getValor(),
                pregunta.getEnunciado(),
                pregunta.getDificultad().getValor(),
                opcionesDto
        );
    }
}