package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.AlertaDocenteResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaEstudiante;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaTemaEstudiante;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Alertas automáticas del panel docente: se calculan cada vez que se consultan
 * a partir de la actividad real, así que nunca quedan desactualizadas.
 */
@Service
public class AlertasDocenteService {

    static final int DIAS_INACTIVO = 7;
    static final int DIAS_PARA_EMPEZAR = 7;
    static final int MIN_INTENTOS_TEMA = 10;
    static final double ACIERTO_MINIMO_TEMA = 50.0;
    static final int MIN_QUICES = 2;
    static final double PROMEDIO_QUIZ_MINIMO = 60.0;
    static final int MIN_INTENTOS_GRUPO = 20;

    private final EstadisticasDocentePort port;
    private final Clock clock;
    /** Zona del negocio (SAESU_ZONA_HORARIA): las diferencias de tiempo se calculan con zona. */
    private final ZoneId zona;

    public AlertasDocenteService(EstadisticasDocentePort port, Clock clock, ZoneId zona) {
        this.port = port;
        this.clock = clock;
        this.zona = zona;
    }

    public List<AlertaDocenteResponse> alertas() {
        LocalDateTime ahora = LocalDateTime.now(clock);
        List<FilaEstudiante> estudiantes = port.listarEstudiantes();
        Map<Long, List<FilaTemaEstudiante>> porEstudiante = port.progresoPorTema(null).stream()
                .collect(Collectors.groupingBy(FilaTemaEstudiante::usuarioId));

        List<AlertaDocenteResponse> alertas = new ArrayList<>();
        for (FilaEstudiante e : estudiantes) {
            alertasDe(e, porEstudiante.getOrDefault(e.id(), List.of()), ahora, alertas);
        }
        for (TemaEstadistica t : port.estadisticasPorTema()) {
            if (t.intentos() >= MIN_INTENTOS_GRUPO && t.porcentaje() < ACIERTO_MINIMO_TEMA) {
                alertas.add(new AlertaDocenteResponse("GRUPO_TEMA", "media", null, null, t.tema(),
                        "El grupo acierta solo el " + t.porcentaje() + " % en " + Tema.nombreDe(t.tema()) + ".",
                        "Repasa el tema en clase o publica una explicación en el foro."));
            }
        }
        alertas.sort(Comparator.comparing((AlertaDocenteResponse a) -> !"alta".equals(a.gravedad()))
                .thenComparing(a -> a.estudiante() == null ? "" : a.estudiante().toLowerCase()));
        return alertas;
    }

    private void alertasDe(FilaEstudiante e, List<FilaTemaEstudiante> temas, LocalDateTime ahora,
                           List<AlertaDocenteResponse> alertas) {
        LocalDateTime ultima = ultimaActividad(e);
        if (ultima == null) {
            if (e.fechaRegistro() != null && dias(e.fechaRegistro(), ahora) >= DIAS_PARA_EMPEZAR) {
                alertas.add(new AlertaDocenteResponse("SIN_EMPEZAR", "alta", e.id(), e.nombre(), null,
                        e.nombre() + " se registró hace " + dias(e.fechaRegistro(), ahora) + " días y no ha practicado.",
                        "Escríbele para invitarlo a hacer el diagnóstico inicial."));
            }
        } else if (dias(ultima, ahora) >= DIAS_INACTIVO) {
            alertas.add(new AlertaDocenteResponse("INACTIVO", "alta", e.id(), e.nombre(), null,
                    e.nombre() + " lleva " + dias(ultima, ahora) + " días sin practicar.",
                    "Asígnale una tarea corta para que retome."));
        }
        for (FilaTemaEstudiante t : temas) {
            double acierto = porcentaje(t.correctas(), t.intentos());
            if (t.intentos() >= MIN_INTENTOS_TEMA && acierto < ACIERTO_MINIMO_TEMA) {
                alertas.add(new AlertaDocenteResponse("TEMA_DIFICIL", "media", e.id(), e.nombre(), t.tema(),
                        e.nombre() + " acierta el " + acierto + " % en " + Tema.nombreDe(t.tema()) + ".",
                        "Recomiéndale los ejercicios resueltos de " + Tema.nombreDe(t.tema()) + "."));
            }
        }
        if (e.quices() >= MIN_QUICES && e.promedioQuiz() != null && e.promedioQuiz() < PROMEDIO_QUIZ_MINIMO) {
            alertas.add(new AlertaDocenteResponse("QUIZ_BAJO", "media", e.id(), e.nombre(), null,
                    e.nombre() + " tiene un promedio de " + Math.round(e.promedioQuiz()) + " % en sus quices.",
                    "Revisa sus quices para ver dónde se equivoca."));
        }
    }

    private static LocalDateTime ultimaActividad(FilaEstudiante e) {
        if (e.ultimaActividad() == null) return e.ultimoQuiz();
        if (e.ultimoQuiz() == null) return e.ultimaActividad();
        return e.ultimaActividad().isAfter(e.ultimoQuiz()) ? e.ultimaActividad() : e.ultimoQuiz();
    }

    private long dias(LocalDateTime desde, LocalDateTime hasta) {
        return Duration.between(desde.atZone(zona), hasta.atZone(zona)).toDays();
    }

    /** Porcentaje con un decimal (0 si no hay base). */
    static double porcentaje(long parte, long total) {
        return total == 0 ? 0.0 : Math.round(parte * 1000.0 / total) / 10.0;
    }
}
