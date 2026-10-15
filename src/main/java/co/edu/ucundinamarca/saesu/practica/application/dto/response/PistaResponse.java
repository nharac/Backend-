package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para una pista.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PistaResponse {
    private Long preguntaId;
    private String pista;
    private String mensaje;
}