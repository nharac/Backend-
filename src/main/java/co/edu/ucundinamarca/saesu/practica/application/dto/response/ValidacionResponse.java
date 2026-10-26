package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta al validar una respuesta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidacionResponse {
    private Boolean esCorrecta;
    private Long opcionCorrectaId;
    private String pista;
    private String mensaje;
    private Boolean habilitarSiguiente;
    private Boolean chatIaHabilitado;
    /** Puntos que se ganaron con este intento (0 si falló o si la pregunta ya estaba resuelta). */
    private Integer puntosGanados;
    /** Puntos que vale acertar en el siguiente intento (baja con cada fallo). */
    private Integer puntosEnJuego;
}