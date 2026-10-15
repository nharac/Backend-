package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para una opción.
 * NO incluye es_correcta por seguridad.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpcionResponse {
    private Long id;
    private String letra;
    private String texto;
}