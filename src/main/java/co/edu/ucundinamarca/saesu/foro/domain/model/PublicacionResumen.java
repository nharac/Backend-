package co.edu.ucundinamarca.saesu.foro.domain.model;

import java.time.LocalDateTime;

/**
 * Lo que se muestra de una publicación en un listado: sus datos, el autor y cuántas respuestas tiene.
 * Se arma con una sola consulta para no ir a la base de datos una vez por publicación.
 *
 * @param inicioContenido solo los primeros caracteres del contenido (para la vista previa)
 */
public record PublicacionResumen(
        Long id,
        String titulo,
        String inicioContenido,
        String tema,
        LocalDateTime fechaCreacion,
        String imagenUrl,
        Long autorId,
        String autorNombre,
        String autorRol,
        long respuestas) {
}
