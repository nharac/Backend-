package co.edu.ucundinamarca.saesu.practica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Opción de respuesta (A, B, C, D) de una pregunta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Opcion {
    private Long id;
    private Long preguntaId;
    private String letra;
    private String texto;
    private Boolean esCorrecta;
}