package co.edu.ucundinamarca.saesu.gamificacion.ports.output;

import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Cifra;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Puerto de salida de los logros. LogrosService solo conoce esta interfaz;
 * la consulta SQL vive en el adaptador LogrosJdbcRepository (inversión de dependencias).
 */
public interface LogrosPort {

    /**
     * Lo que se necesita para evaluar los logros.
     * @param cifras las cifras de base de datos del estudiante (las que falten valen 0)
     * @param temas  por tema: {preguntas activas del tema, de esas cuántas ha acertado alguna vez}
     * @param correo para los logros exclusivos (p. ej. desarrolladores)
     */
    record Estadisticas(Map<Cifra, Long> cifras, Map<String, long[]> temas, String correo) {

        public long get(Cifra cifra) {
            return cifras.getOrDefault(cifra, 0L);
        }

        public long preguntasDelTema(String tema) {
            long[] t = temas.get(tema);
            return t == null ? 0 : t[0];
        }

        public long resueltasDelTema(String tema) {
            long[] t = temas.get(tema);
            return t == null ? 0 : t[1];
        }
    }

    Estadisticas estadisticas(Long usuarioId);

    /** {racha actual, puntos totales} en una sola consulta (para el perfil). */
    long[] rachaYPuntos(Long usuarioId);

    /** Logros ya desbloqueados: código → fecha. */
    Map<String, OffsetDateTime> obtenidos(Long usuarioId);

    /** Registra el logro; devuelve false si ya lo tenía. */
    boolean guardar(Long usuarioId, String codigo);
}
