package co.edu.ucundinamarca.saesu.foro.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Publicación del foro colaborativo.
 * Modelo de dominio puro, sin anotaciones JPA.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Publicacion {

    private Long id;
    private Long autorId;      // FK al usuario autor
    private String titulo;
    private String contenido;
    private String tema;       // limites, derivadas, integrales (o null)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}