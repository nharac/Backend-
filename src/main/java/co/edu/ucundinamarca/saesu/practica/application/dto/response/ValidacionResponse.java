package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta al validar una respuesta.
 */
@Getter
@Setter
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
    /** Logros que se desbloquearon con este intento (vacío si ninguno). */
    private List<LogroResponse> nuevosLogros = new ArrayList<>();

    public ValidacionResponse(Boolean esCorrecta, Long opcionCorrectaId, String pista, String mensaje,
                              Boolean habilitarSiguiente, Boolean chatIaHabilitado,
                              Integer puntosGanados, Integer puntosEnJuego) {
        this.esCorrecta = esCorrecta;
        this.opcionCorrectaId = opcionCorrectaId;
        this.pista = pista;
        this.mensaje = mensaje;
        this.habilitarSiguiente = habilitarSiguiente;
        this.chatIaHabilitado = chatIaHabilitado;
        this.puntosGanados = puntosGanados;
        this.puntosEnJuego = puntosEnJuego;
    }
}