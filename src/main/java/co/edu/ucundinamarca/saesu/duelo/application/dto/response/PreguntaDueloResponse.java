package co.edu.ucundinamarca.saesu.duelo.application.dto.response;

import java.util.List;

/** Pregunta del reto sin la respuesta correcta; {@code puntos} es lo que vale (modo a puntos). */
public record PreguntaDueloResponse(Long id, String enunciado, int puntos, List<Opcion> opciones) {
    public record Opcion(String letra, String texto) {}
}
