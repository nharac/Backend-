package co.edu.ucundinamarca.saesu.foro.ports.output;

import java.util.Map;
import java.util.Set;

/** Puerto de salida: marcas de "Me sirvió" en las respuestas del foro (tabla respuestas_utiles, V23). */
public interface UtilesPort {

    /** Id del usuario por su correo, o null si no existe. */
    Long idUsuario(String correo);

    /** ¿Existe la respuesta (y no está oculta)? */
    boolean respuestaVisible(Long respuestaId);

    /** Cuántos "Me sirvió" tiene cada respuesta de la publicación (solo las que tienen alguno). */
    Map<Long, Long> contarPorPublicacion(Long publicacionId);

    /** Respuestas de la publicación que este usuario marcó. */
    Set<Long> marcadasPor(Long publicacionId, Long usuarioId);

    /** Marca la respuesta; false si ya estaba marcada. */
    boolean marcar(Long respuestaId, Long usuarioId);

    /** Quita la marca; false si no estaba marcada. */
    boolean desmarcar(Long respuestaId, Long usuarioId);

    long contar(Long respuestaId);
}
