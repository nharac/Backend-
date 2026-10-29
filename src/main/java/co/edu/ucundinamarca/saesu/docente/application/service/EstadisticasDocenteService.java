package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteDetalleResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse.TemaProgreso;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaEstudiante;
import co.edu.ucundinamarca.saesu.docente.ports.output.EstadisticasDocentePort.FilaTemaEstudiante;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Estadísticas del grupo, progreso por estudiante y exportación a CSV (módulo docente).
 *
 * Estado de cada estudiante:
 *  - "nuevo": se registró hace menos de {@value #DIAS_COMO_NUEVO} días y todavía no ha practicado ni hecho
 *    quices. No se marca en riesgo: tiene ese tiempo para empezar.
 *  - "en-riesgo": pasado ese tiempo, si nunca ha tenido actividad (práctica o quiz), si lleva más de
 *    {@value #DIAS_SIN_PRACTICAR} días sin actividad, o si tiene al menos {@value #MIN_INTENTOS}
 *    intentos y menos del {@value #PORCENTAJE_MINIMO} % de aciertos.
 *  - "al-dia": en cualquier otro caso.
 */
@Service
public class EstadisticasDocenteService {

    static final int DIAS_SIN_PRACTICAR = 7;
    static final int DIAS_COMO_NUEVO = 7;
    static final int MIN_INTENTOS = 5;
    static final double PORCENTAJE_MINIMO = 50.0;

    static final List<String> TEMAS = List.of("limites", "derivadas", "integrales");
    private static final Map<String, String> NOMBRE_TEMA = Map.of(
            "limites", "Límites", "derivadas", "Derivadas", "integrales", "Integrales");

    private final EstadisticasDocentePort port;
    private final Clock clock;

    @Autowired
    public EstadisticasDocenteService(EstadisticasDocentePort port) {
        this(port, Clock.systemDefaultZone());
    }

    /** Constructor para pruebas (reloj fijo). */
    EstadisticasDocenteService(EstadisticasDocentePort port, Clock clock) {
        this.port = port;
        this.clock = clock;
    }

    // ============================================
    // Panel del grupo
    // ============================================

    public EstadisticasGrupoResponse estadisticasGrupo() {
        List<EstudianteResumenResponse> estudiantes = listarEstudiantes();
        EstadisticasDocentePort.ResumenSemana semana = port.resumenSemana();
        List<TemaEstadistica> porTema = port.estadisticasPorTema();

        long intentos = porTema.stream().mapToLong(TemaEstadistica::intentos).sum();
        long correctas = porTema.stream().mapToLong(TemaEstadistica::correctas).sum();
        long preguntasActivas = port.preguntasActivasPorTema().values().stream().mapToLong(Long::longValue).sum();

        return new EstadisticasGrupoResponse(
                estudiantes.size(),
                semana.activos(),
                semana.intentos(),
                porcentaje(correctas, intentos),
                estudiantes.stream().filter(EstudianteResumenResponse::enRiesgo).count(),
                preguntasActivas,
                porTema,
                port.preguntasDificiles(5),
                port.actividad(14, null));
    }

    // ============================================
    // Estudiantes
    // ============================================

    public List<EstudianteResumenResponse> listarEstudiantes() {
        Map<String, Long> totalPorTema = port.preguntasActivasPorTema();
        Map<Long, List<FilaTemaEstudiante>> temasPorUsuario = port.progresoPorTema(null).stream()
                .collect(Collectors.groupingBy(FilaTemaEstudiante::usuarioId));

        return port.listarEstudiantes().stream()
                .map(f -> construirResumen(f, temasPorUsuario.getOrDefault(f.id(), List.of()), totalPorTema))
                .toList();
    }

    public EstudianteDetalleResponse detalleEstudiante(Long usuarioId) {
        FilaEstudiante fila = port.buscarEstudiante(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Estudiante no encontrado con id: " + usuarioId));

        EstudianteResumenResponse resumen = construirResumen(
                fila, port.progresoPorTema(usuarioId), port.preguntasActivasPorTema());
        EstadisticasDocentePort.ParticipacionEstudiante p = port.participacion(usuarioId);

        return new EstudianteDetalleResponse(
                resumen,
                fila.fechaRegistro(),
                p.logros(),
                p.publicaciones(),
                p.respuestas(),
                p.oficiales(),
                port.ultimosIntentos(usuarioId, 10),
                port.preguntasConMasFallos(usuarioId, 5),
                port.actividad(14, usuarioId),
                port.ultimosQuices(usuarioId, 10).stream().map(EstadisticasDocenteService::quiz).toList());
    }

    private static EstudianteDetalleResponse.QuizRealizado quiz(EstadisticasDocentePort.QuizEstudiante q) {
        long segundos = q.iniciadoEn() == null || q.finalizadoEn() == null ? 0
                : Math.max(0, Duration.between(q.iniciadoEn(), q.finalizadoEn()).toSeconds());
        int pct = q.total() == 0 ? 0 : (int) Math.round(100.0 * q.correctas() / q.total());
        return new EstudianteDetalleResponse.QuizRealizado(q.id(), q.tema(), q.correctas(), q.total(), pct,
                segundos, q.fueraDeTiempo(), q.finalizadoEn());
    }

    EstudianteResumenResponse construirResumen(FilaEstudiante f, List<FilaTemaEstudiante> temas,
                                               Map<String, Long> totalPorTema) {
        List<TemaProgreso> porTema = new ArrayList<>();
        for (String tema : TEMAS) {
            FilaTemaEstudiante t = temas.stream().filter(x -> tema.equals(x.tema())).findFirst().orElse(null);
            long intentos = t == null ? 0 : t.intentos();
            long correctas = t == null ? 0 : t.correctas();
            porTema.add(new TemaProgreso(tema, intentos, correctas, porcentaje(correctas, intentos),
                    t == null ? 0 : t.resueltas(), totalPorTema.getOrDefault(tema, 0L)));
        }

        double pct = porcentaje(f.correctas(), f.intentos());
        LocalDateTime ultimaActividad = masReciente(f.ultimaActividad(), f.ultimoQuiz());
        // El tiempo de gracia es para empezar: en cuanto practica o hace un quiz se evalúa como los demás
        Integer diasDeGracia = ultimaActividad == null ? diasDeGracia(f.fechaRegistro()) : null;
        List<String> motivos = diasDeGracia != null ? List.of() : motivosRiesgo(ultimaActividad, f.intentos(), pct);
        String estado = diasDeGracia != null ? "nuevo" : motivos.isEmpty() ? "al-dia" : "en-riesgo";

        return new EstudianteResumenResponse(
                f.id(), f.nombre(), f.correo(), f.fotoPerfilUrl(), f.puntos(), f.racha(),
                f.intentos(), f.correctas(), pct, ultimaActividad,
                !motivos.isEmpty(), motivos, porTema,
                estado, diasDeGracia, f.quices(),
                f.promedioQuiz() == null ? null : Math.round(f.promedioQuiz() * 10) / 10.0);
    }

    /** Días que le quedan como "nuevo" (mínimo 1), o null si ya pasó ese tiempo. */
    Integer diasDeGracia(LocalDateTime fechaRegistro) {
        if (fechaRegistro == null) return null;
        long horas = Duration.between(fechaRegistro, LocalDateTime.now(clock)).toHours();
        long restantes = DIAS_COMO_NUEVO * 24L - horas;
        return restantes > 0 ? (int) Math.max(1, (restantes + 23) / 24) : null;
    }

    private static LocalDateTime masReciente(LocalDateTime a, LocalDateTime b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    List<String> motivosRiesgo(LocalDateTime ultimaActividad, long intentos, double porcentaje) {
        List<String> motivos = new ArrayList<>();
        if (ultimaActividad == null) {
            motivos.add("No ha practicado ni hecho quices");
        } else {
            long dias = Duration.between(ultimaActividad, LocalDateTime.now(clock)).toDays();
            if (dias > DIAS_SIN_PRACTICAR) {
                motivos.add("Sin actividad hace " + dias + " días");
            }
        }
        if (intentos >= MIN_INTENTOS && porcentaje < PORCENTAJE_MINIMO) {
            motivos.add("Aciertos bajos (" + formatoPorcentaje(porcentaje) + ")");
        }
        return motivos;
    }

    // ============================================
    // Exportar a CSV (se abre en Excel)
    // ============================================

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * CSV separado por ";" (Excel en español usa la coma como separador decimal),
     * con BOM para que Excel reconozca las tildes.
     */
    public String exportarCsv() {
        StringBuilder sb = new StringBuilder("﻿");
        List<String> encabezados = new ArrayList<>(List.of(
                "Nombre", "Correo", "Puntos", "Racha (días)", "Intentos", "Aciertos (%)"));
        TEMAS.forEach(t -> encabezados.add(NOMBRE_TEMA.get(t) + " (%)"));
        encabezados.addAll(List.of("Última actividad", "Estado", "Motivo"));
        sb.append(String.join(";", encabezados)).append("\r\n");

        for (EstudianteResumenResponse e : listarEstudiantes()) {
            List<String> celdas = new ArrayList<>(List.of(
                    e.nombre(), e.correo(), String.valueOf(e.puntos()), String.valueOf(e.racha()),
                    String.valueOf(e.intentos()), formatoNumero(e.porcentaje())));
            for (TemaProgreso t : e.porTema()) {
                celdas.add(t.intentos() == 0 ? "" : formatoNumero(t.porcentaje()));
            }
            celdas.add(e.ultimaActividad() == null ? "Nunca" : e.ultimaActividad().format(FORMATO_FECHA));
            celdas.add(switch (e.estado()) { case "nuevo" -> "Nuevo"; case "en-riesgo" -> "En riesgo"; default -> "Al día"; });
            celdas.add(String.join(", ", e.motivosRiesgo()));
            sb.append(celdas.stream().map(EstadisticasDocenteService::celdaCsv).collect(Collectors.joining(";")))
              .append("\r\n");
        }
        return sb.toString();
    }

    /**
     * Escapa una celda: comillas si trae ; " o saltos de línea, y un apóstrofo delante si empieza
     * con = + - @ (evita que Excel la ejecute como fórmula: "CSV injection").
     */
    static String celdaCsv(String valor) {
        String v = valor == null ? "" : valor;
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        if (v.contains(";") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    private static String formatoNumero(double n) {
        return String.format(java.util.Locale.forLanguageTag("es-CO"), "%.1f", n);
    }

    private static String formatoPorcentaje(double n) {
        return (n == Math.rint(n) ? String.valueOf((long) n) : formatoNumero(n)) + " %";
    }

    static double porcentaje(long parte, long total) {
        return total == 0 ? 0.0 : Math.round(parte * 1000.0 / total) / 10.0;
    }
}
