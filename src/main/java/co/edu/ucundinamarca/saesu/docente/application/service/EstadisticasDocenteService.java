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
 * Un estudiante queda "en riesgo" si:
 *  - nunca ha practicado o lleva más de {@value #DIAS_SIN_PRACTICAR} días sin practicar, o
 *  - tiene al menos {@value #MIN_INTENTOS} intentos y menos del {@value #PORCENTAJE_MINIMO} % de aciertos.
 */
@Service
public class EstadisticasDocenteService {

    static final int DIAS_SIN_PRACTICAR = 7;
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
                port.actividad(14, usuarioId));
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
        List<String> motivos = motivosRiesgo(f.ultimaActividad(), f.intentos(), pct);

        return new EstudianteResumenResponse(
                f.id(), f.nombre(), f.correo(), f.fotoPerfilUrl(), f.puntos(), f.racha(),
                f.intentos(), f.correctas(), pct, f.ultimaActividad(),
                !motivos.isEmpty(), motivos, porTema);
    }

    List<String> motivosRiesgo(LocalDateTime ultimaActividad, long intentos, double porcentaje) {
        List<String> motivos = new ArrayList<>();
        if (ultimaActividad == null) {
            motivos.add("Aún no ha practicado");
        } else {
            long dias = Duration.between(ultimaActividad, LocalDateTime.now(clock)).toDays();
            if (dias > DIAS_SIN_PRACTICAR) {
                motivos.add("Sin practicar hace " + dias + " días");
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
            celdas.add(e.enRiesgo() ? "En riesgo" : "Al día");
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
