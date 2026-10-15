package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para el progreso del estudiante por tema.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoResponse {
    private ProgresoTema limites;
    private ProgresoTema derivadas;
    private ProgresoTema integrales;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgresoTema {
        private long respondidas;
        private long correctas;
        private double porcentaje;
    }
}