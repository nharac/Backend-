package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import java.util.List;
import java.util.Map;

/**
 * "Me sirvió" de las respuestas de una publicación.
 * @param conteos respuestaId → cuántas personas marcaron "Me sirvió" (solo las que tienen alguna)
 * @param mias    respuestas que marcó quien consulta
 */
public record UtilesResponse(Map<Long, Long> conteos, List<Long> mias) {
}
