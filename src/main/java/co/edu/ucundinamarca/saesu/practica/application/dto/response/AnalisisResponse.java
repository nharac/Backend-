package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import java.util.List;

/**
 * Fortalezas y debilidades (GET /api/practica/analisis).
 * @param detalle        las 9 combinaciones de tema y dificultad (las que no tienen respuestas van en 0)
 * @param recomendacion  qué practicar ahora (null si todavía no ha respondido nada)
 */
public record AnalisisResponse(List<Rendimiento> detalle, List<Rendimiento> fortalezas,
                               List<Rendimiento> debilidades, Recomendacion recomendacion) {

    public record Rendimiento(String tema, String dificultad, long respondidas, long correctas, int porcentaje) { }

    /** El motivo lo dice Sumi en el perfil. */
    public record Recomendacion(String tema, String dificultad, String motivo) { }
}
