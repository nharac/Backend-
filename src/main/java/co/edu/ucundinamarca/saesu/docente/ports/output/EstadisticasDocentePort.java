package co.edu.ucundinamarca.saesu.docente.ports.output;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.ActividadDia;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.PreguntaDificil;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse.IntentoReciente;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse.PreguntaConFallos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Consultas de solo lectura para las estadísticas del docente.
 * Cruzan tablas de varios módulos (usuarios, práctica, foro, gamificación).
 */
public interface EstadisticasDocentePort {

    /** Datos base de un estudiante (puntos, racha vigente, intentos y última actividad). */
    record FilaEstudiante(
            Long id,
            String nombre,
            String correo,
            String fotoPerfilUrl,
            long puntos,
            int racha,
            long intentos,
            long correctas,
            LocalDateTime ultimaActividad,
            LocalDateTime fechaRegistro,
            long quices,
            Double promedioQuiz,
            LocalDateTime ultimoQuiz
    ) {}

    /** Un quiz entregado por el estudiante (para que el docente vea sus resultados). */
    record QuizEstudiante(Long id, String tema, int correctas, int total, LocalDateTime iniciadoEn,
                          LocalDateTime finalizadoEn, boolean fueraDeTiempo) {}

    /** Intentos de un estudiante en un tema. */
    record FilaTemaEstudiante(Long usuarioId, String tema, long intentos, long correctas, long resueltas) {}

    /** Participación del estudiante en el foro y logros obtenidos. */
    record ParticipacionEstudiante(long publicaciones, long respuestas, long oficiales, long logros) {}

    /** Estudiantes activos e intentos en los últimos 7 días. */
    record ResumenSemana(long activos, long intentos) {}

    List<FilaEstudiante> listarEstudiantes();

    Optional<FilaEstudiante> buscarEstudiante(Long usuarioId);

    /** Progreso por tema de todos los estudiantes, o de uno solo si usuarioId no es null. */
    List<FilaTemaEstudiante> progresoPorTema(Long usuarioId);

    /** Cantidad de preguntas activas por tema. */
    Map<String, Long> preguntasActivasPorTema();

    ResumenSemana resumenSemana();

    List<TemaEstadistica> estadisticasPorTema();

    List<PreguntaDificil> preguntasDificiles(int limite);

    /** Intentos por día de los últimos {@code dias} días (todo el grupo si usuarioId es null). */
    List<ActividadDia> actividad(int dias, Long usuarioId);

    List<IntentoReciente> ultimosIntentos(Long usuarioId, int limite);

    List<PreguntaConFallos> preguntasConMasFallos(Long usuarioId, int limite);

    ParticipacionEstudiante participacion(Long usuarioId);

    /** Últimos quices entregados por el estudiante, del más reciente al más antiguo. */
    List<QuizEstudiante> ultimosQuices(Long usuarioId, int limite);
}
