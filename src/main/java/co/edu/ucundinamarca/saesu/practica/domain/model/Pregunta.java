package co.edu.ucundinamarca.saesu.practica.domain.model;

import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Pregunta de práctica tipo ICFES.
 * Modelo de dominio puro, sin anotaciones JPA.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pregunta {
    private Long id;
    private Tema tema;
    private String enunciado;
    private Dificultad dificultad;
    private String pista;
    private LocalDateTime fechaCreacion;
    private List<Opcion> opciones;
}