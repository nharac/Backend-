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
}