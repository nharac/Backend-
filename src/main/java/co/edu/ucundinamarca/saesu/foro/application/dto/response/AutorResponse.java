package co.edu.ucundinamarca.saesu.foro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con la información mínima del autor (usuario) para mostrarla en el foro.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutorResponse {

    private Long id;
    private String nombre;
    private String iniciales;   // ej: "CP" para Carlos Pérez
    private String rol;         // ESTUDIANTE o DOCENTE
}