package co.edu.ucundinamarca.saesu.practica.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta para una pregunta.
 * NO incluye el campo es_correcta en las opciones (por seguridad).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreguntaResponse {
    private Long id;
    private String tema;
    private String enunciado;
    private String dificultad;
    private List<OpcionResponse> opciones;
}