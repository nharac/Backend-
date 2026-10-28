package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.AutorResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.domain.model.PublicacionResumen;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;

/**
 * Convierte los datos del foro a las respuestas de la API.
 * Lo usan el listado del foro, el detalle y los pendientes del docente.
 */
public final class ResumenForo {

    static final int PREVIEW_MAX = 150;

    private ResumenForo() {
    }

    public static PublicacionResumenResponse aResponse(PublicacionResumen p) {
        return new PublicacionResumenResponse(
                p.id(),
                p.titulo(),
                preview(p.inicioContenido()),
                p.tema(),
                autor(p.autorId(), p.autorNombre(), p.autorRol()),
                p.fechaCreacion(),
                p.respuestas(),
                p.imagenUrl());
    }

    public static AutorResponse autor(Usuario u) {
        return autor(u.getId(), u.getNombre(), u.getRol());
    }

    public static AutorResponse autor(Long id, String nombre, String rol) {
        return new AutorResponse(id, nombre, iniciales(nombre), rol);
    }

    /** "Carlos Pérez" → "CP"; un solo nombre → su inicial. */
    public static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }
        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    /** Los primeros 150 caracteres, con "..." solo si había más. */
    public static String preview(String contenido) {
        if (contenido == null) return "";
        if (contenido.length() <= PREVIEW_MAX) return contenido;
        return contenido.substring(0, PREVIEW_MAX) + "...";
    }
}
