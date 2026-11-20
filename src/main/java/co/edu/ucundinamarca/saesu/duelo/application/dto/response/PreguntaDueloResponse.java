package co.edu.ucundinamarca.saesu.duelo.application.dto.response;

import java.util.List;

/** Pregunta del reto sin la respuesta correcta. */
public record PreguntaDueloResponse(Long id, String enunciado, List<Opcion> opciones) {
    public record Opcion(String letra, String texto) {}
}
