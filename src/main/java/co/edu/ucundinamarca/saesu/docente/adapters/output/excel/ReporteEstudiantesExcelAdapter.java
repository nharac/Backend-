package co.edu.ucundinamarca.saesu.docente.adapters.output.excel;

import co.edu.ucundinamarca.saesu.core.excel.LibroExcel;
import co.edu.ucundinamarca.saesu.core.excel.LibroExcel.Celda;
import co.edu.ucundinamarca.saesu.core.excel.LibroExcel.Estilo;
import co.edu.ucundinamarca.saesu.core.excel.LibroExcel.Hoja;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse.TemaProgreso;
import co.edu.ucundinamarca.saesu.docente.ports.output.ReporteEstudiantesPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reporte de estudiantes en Excel (.xlsx) con formato: encabezado fijo, filtros,
 * porcentajes de verdad (no texto) coloreados según el nivel y una hoja de resumen del grupo.
 */
@Component
public class ReporteEstudiantesExcelAdapter implements ReporteEstudiantesPort {

    private static final List<String> TEMAS = List.of("limites", "derivadas", "integrales");
    private static final Map<String, String> NOMBRE_TEMA = Map.of(
            "limites", "Límites", "derivadas", "Derivadas", "integrales", "Integrales");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' h:mm a", Locale.forLanguageTag("es-CO"));
    private static final String[] ENCABEZADOS = {
            "Estudiante", "Correo", "Puntos", "Racha (días)", "Intentos", "Aciertos",
            "Límites", "Derivadas", "Integrales", "Quices", "Promedio quiz", "Última actividad", "Estado", "Por qué"};

    @Override
    public byte[] generar(List<EstudianteResumenResponse> estudiantes, EstadisticasGrupoResponse grupo,
                          LocalDateTime generadoEn) {
        LibroExcel libro = new LibroExcel();
        hojaEstudiantes(libro.hoja("Estudiantes"), estudiantes, generadoEn);
        hojaResumen(libro.hoja("Resumen del grupo"), grupo, generadoEn);
        return libro.generar();
    }

    private void hojaEstudiantes(Hoja h, List<EstudianteResumenResponse> estudiantes, LocalDateTime generadoEn) {
        int columnas = ENCABEZADOS.length;
        long enRiesgo = estudiantes.stream().filter(EstudianteResumenResponse::enRiesgo).count();
        long nuevos = estudiantes.stream().filter(e -> "nuevo".equals(e.estado())).count();

        h.anchos(30, 36, 10, 12, 10, 11, 11, 11, 11, 9, 13, 18, 13, 44);
        h.fila(32.0, Celda.texto("Progreso de los estudiantes", Estilo.TITULO));
        h.combinar(1, 1, columnas);
        h.fila(20.0, Celda.texto("SAESU · Generado el " + generadoEn.format(FECHA) + " · "
                + estudiantes.size() + (estudiantes.size() == 1 ? " estudiante" : " estudiantes")
                + (enRiesgo > 0 ? ", " + enRiesgo + " en riesgo" : "")
                + (nuevos > 0 ? ", " + nuevos + (nuevos == 1 ? " nuevo" : " nuevos") : ""), Estilo.SUBTITULO));
        h.combinar(2, 1, columnas);
        h.fila(8.0);

        List<Celda> encabezado = new ArrayList<>();
        for (String e : ENCABEZADOS) encabezado.add(Celda.texto(e, Estilo.ENCABEZADO));
        int filaEncabezado = h.fila(30.0, encabezado.toArray(Celda[]::new));

        // Primero los que necesitan atención; luego por nombre
        List<EstudianteResumenResponse> orden = estudiantes.stream()
                .sorted(Comparator.comparing((EstudianteResumenResponse e) -> !e.enRiesgo())
                        .thenComparing(e -> e.nombre() == null ? "" : e.nombre().toLowerCase()))
                .toList();

        for (EstudianteResumenResponse e : orden) {
            List<Celda> fila = new ArrayList<>();
            fila.add(Celda.texto(e.nombre(), Estilo.TEXTO));
            fila.add(Celda.texto(e.correo(), Estilo.TEXTO));
            fila.add(Celda.numero(e.puntos(), Estilo.ENTERO));
            fila.add(Celda.numero(e.racha(), Estilo.ENTERO));
            fila.add(Celda.numero(e.intentos(), Estilo.ENTERO));
            fila.add(e.intentos() == 0 ? Celda.texto("—", Estilo.VACIO) : porcentaje(e.porcentaje()));
            for (String tema : TEMAS) {
                TemaProgreso t = e.porTema().stream().filter(p -> tema.equals(p.tema())).findFirst().orElse(null);
                fila.add(t == null || t.intentos() == 0 ? Celda.texto("—", Estilo.VACIO) : porcentaje(t.porcentaje()));
            }
            fila.add(Celda.numero(e.quices(), Estilo.ENTERO));
            fila.add(e.promedioQuiz() == null ? Celda.texto("—", Estilo.VACIO) : porcentaje(e.promedioQuiz()));
            fila.add(e.ultimaActividad() == null
                    ? Celda.texto("Nunca", Estilo.VACIO)
                    : Celda.fecha(e.ultimaActividad(), Estilo.FECHA));
            fila.add(switch (e.estado()) {
                case "nuevo" -> Celda.texto("Nuevo", Estilo.NUEVO);
                case "en-riesgo" -> Celda.texto("En riesgo", Estilo.RIESGO);
                default -> Celda.texto("Al día", Estilo.AL_DIA);
            });
            fila.add(Celda.texto("nuevo".equals(e.estado())
                    ? "Recién registrado: tiene " + e.diasDeGracia() + (e.diasDeGracia() == 1 ? " día" : " días") + " para empezar"
                    : String.join("; ", e.motivosRiesgo()), Estilo.TEXTO_SUAVE));
            h.fila(22.0, fila.toArray(Celda[]::new));
        }

        h.inmovilizarFilas(filaEncabezado);
        h.filtro(filaEncabezado, columnas);
    }

    private void hojaResumen(Hoja h, EstadisticasGrupoResponse g, LocalDateTime generadoEn) {
        h.anchos(52, 14, 14, 30);
        h.fila(32.0, Celda.texto("Resumen del grupo", Estilo.TITULO));
        h.combinar(1, 1, 4);
        h.fila(20.0, Celda.texto("SAESU · Generado el " + generadoEn.format(FECHA), Estilo.SUBTITULO));
        h.combinar(2, 1, 4);
        h.fila(8.0);

        dato(h, "Estudiantes", Celda.numero(g.totalEstudiantes(), Estilo.NUMERO_GRANDE));
        dato(h, "Practicaron esta semana", Celda.numero(g.activosSemana(), Estilo.NUMERO_GRANDE));
        dato(h, "Respuestas esta semana", Celda.numero(g.intentosSemana(), Estilo.NUMERO_GRANDE));
        dato(h, "Aciertos del grupo", porcentaje(g.porcentajeAciertoGeneral()));
        dato(h, "Estudiantes en riesgo", Celda.numero(g.estudiantesEnRiesgo(),
                g.estudiantesEnRiesgo() > 0 ? Estilo.RIESGO : Estilo.AL_DIA));
        dato(h, "Preguntas activas en el banco", Celda.numero(g.preguntasActivas(), Estilo.NUMERO_GRANDE));
        h.fila(14.0);

        h.fila(28.0, Celda.texto("Tema", Estilo.ENCABEZADO), Celda.texto("Respuestas", Estilo.ENCABEZADO),
                Celda.texto("Aciertos", Estilo.ENCABEZADO), Celda.texto("Estudiantes que lo practican", Estilo.ENCABEZADO));
        for (EstadisticasGrupoResponse.TemaEstadistica t : g.porTema()) {
            h.fila(22.0, Celda.texto(NOMBRE_TEMA.getOrDefault(t.tema(), t.tema()), Estilo.TEXTO),
                    Celda.numero(t.intentos(), Estilo.ENTERO),
                    t.intentos() == 0 ? Celda.texto("—", Estilo.VACIO) : porcentaje(t.porcentaje()),
                    Celda.numero(t.estudiantes(), Estilo.ENTERO));
        }

        if (!g.preguntasDificiles().isEmpty()) {
            h.fila(14.0);
            h.fila(28.0, Celda.texto("Preguntas que más fallan", Estilo.ENCABEZADO), Celda.texto("Tema", Estilo.ENCABEZADO),
                    Celda.texto("Fallan", Estilo.ENCABEZADO), Celda.texto("Respuesta equivocada más común", Estilo.ENCABEZADO));
            for (EstadisticasGrupoResponse.PreguntaDificil p : g.preguntasDificiles()) {
                h.fila(30.0, Celda.texto(p.enunciado(), Estilo.TEXTO_SUAVE),
                        Celda.texto(NOMBRE_TEMA.getOrDefault(p.tema(), p.tema()), Estilo.TEXTO),
                        porcentajeInverso(p.porcentajeFallo()),
                        Celda.texto(p.errorFrecuente() == null ? "—" : p.errorFrecuente(), Estilo.TEXTO));
            }
        }
    }

    private static void dato(Hoja h, String etiqueta, Celda valor) {
        h.fila(26.0, Celda.texto(etiqueta, Estilo.ETIQUETA), valor);
    }

    /** 0-100 → celda de porcentaje: rojo bajo 50 %, ámbar bajo 80 %, verde desde 80 %. */
    private static Celda porcentaje(double valor0a100) {
        Estilo e = valor0a100 < 50 ? Estilo.PORCENTAJE_BAJO : valor0a100 < 80 ? Estilo.PORCENTAJE_MEDIO : Estilo.PORCENTAJE_ALTO;
        return Celda.numero(valor0a100 / 100.0, e);
    }

    /** Para el porcentaje de fallo, al revés: mucho fallo = rojo. */
    private static Celda porcentajeInverso(double valor0a100) {
        Estilo e = valor0a100 >= 50 ? Estilo.PORCENTAJE_BAJO : valor0a100 >= 20 ? Estilo.PORCENTAJE_MEDIO : Estilo.PORCENTAJE_ALTO;
        return Celda.numero(valor0a100 / 100.0, e);
    }
}
