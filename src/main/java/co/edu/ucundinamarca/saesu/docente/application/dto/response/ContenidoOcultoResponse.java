package co.edu.ucundinamarca.saesu.docente.application.dto.response;

import java.time.LocalDateTime;

/**
 * Publicación o respuesta del foro que un docente ocultó (se puede restaurar).
 * GET /api/docente/moderacion
 */
public record ContenidoOcultoResponse(
        String tipo,            // "PUBLICACION" o "RESPUESTA"
        Long id,
        Long publicacionId,
        String tituloPublicacion,
        String contenido,
        String autor,
        LocalDateTime fechaCreacion,
        String ocultadoPor,
        LocalDateTime fechaOculta
) {}
