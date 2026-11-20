package co.edu.ucundinamarca.saesu.duelo.application.dto.response;

import java.time.LocalDateTime;

/**
 * Un reto visto por uno de los dos participantes.
 *
 * @param estado    POR_JUGAR (aún no respondo), ESPERANDO (respondí, falta el otro) o TERMINADO
 * @param resultado GANASTE, PERDISTE o EMPATE cuando está terminado (si no, null)
 */
public record DueloResponse(
        Long id,
        String tema,
        String temaNombre,
        String rival,
        boolean soyRetador,
        String estado,
        String resultado,
        Integer misCorrectas,
        Integer misSegundos,
        Integer suyasCorrectas,
        Integer suyosSegundos,
        int totalPreguntas,
        LocalDateTime creadoEn
) {}
