package co.edu.ucundinamarca.saesu.practica.ports.output;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida para las estadísticas personales del perfil (actividad, rendimiento y meta semanal).
 * El servicio no sabe de SQL: la consulta vive en MiAvanceJdbcAdapter.
 */
public interface MiAvancePort {

    /** Respuestas de un día (hora de Colombia). */
    record Dia(LocalDate fecha, long respondidas, long aciertos) { }

    /** Respuestas agrupadas por tema y dificultad. */
    record Rendimiento(String tema, String dificultad, long respondidas, long correctas) { }

    /** Días con al menos una respuesta desde la fecha dada (incluida), en orden. */
    List<Dia> actividadDesde(Long usuarioId, LocalDate desde);

    List<Rendimiento> rendimiento(Long usuarioId);

    int metaSemanal(Long usuarioId);

    void guardarMetaSemanal(Long usuarioId, int meta);
}
