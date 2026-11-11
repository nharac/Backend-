package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ActividadResponse.DiaActividad;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse.Recomendacion;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.AnalisisResponse.Rendimiento;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.MetaSemanalResponse;
import co.edu.ucundinamarca.saesu.practica.ports.input.MiAvanceUseCase;
import co.edu.ucundinamarca.saesu.practica.ports.output.MiAvancePort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Estadísticas personales del perfil del estudiante:
 * calendario de actividad, fortalezas y debilidades por tema y dificultad, y meta semanal.
 */
@Service
public class MiAvanceService implements MiAvanceUseCase {

    static final int DIAS_MIN = 7;
    static final int DIAS_MAX = 371;
    /** Con menos respuestas que esto, un tema y dificultad todavía no dice nada. */
    static final int MIN_RESPUESTAS = 3;
    static final int UMBRAL_FORTALEZA = 70;
    static final int UMBRAL_DEBILIDAD = 60;
    private static final int MAX_LISTA = 3;

    private static final List<String> TEMAS = List.of("limites", "derivadas", "integrales");
    private static final List<String> DIFICULTADES = List.of("basico", "intermedio", "avanzado");
    private static final Map<String, String> NOMBRE_TEMA = Map.of(
            "limites", "Límites", "derivadas", "Derivadas", "integrales", "Integrales");
    private static final Map<String, String> NOMBRE_DIFICULTAD = Map.of(
            "basico", "básico", "intermedio", "intermedio", "avanzado", "avanzado");

    private final MiAvancePort port;
    private final UsuarioRepositoryPort usuarios;
    private final Clock clock;
    /** Zona del negocio para saber qué día es (SAESU_ZONA_HORARIA). */
    private final ZoneId zona;

    public MiAvanceService(MiAvancePort port, UsuarioRepositoryPort usuarios, Clock clock, ZoneId zona) {
        this.port = port;
        this.usuarios = usuarios;
        this.clock = clock;
        this.zona = zona;
    }

    // ============================================
    // Calendario de actividad
    // ============================================

    @Override
    public ActividadResponse actividad(String correo, int dias) {
        Long id = idDe(correo);
        int total = Math.max(DIAS_MIN, Math.min(DIAS_MAX, dias));
        LocalDate hoy = hoy();
        LocalDate desde = hoy.minusDays(total - 1L);

        Map<LocalDate, MiAvancePort.Dia> porFecha = new HashMap<>();
        for (MiAvancePort.Dia d : port.actividadDesde(id, desde)) {
            porFecha.put(d.fecha(), d);
        }

        List<DiaActividad> lista = new ArrayList<>(total);
        long activos = 0;
        long respondidas = 0;
        long aciertos = 0;
        DiaActividad mejor = null;
        for (LocalDate f = desde; !f.isAfter(hoy); f = f.plusDays(1)) {
            MiAvancePort.Dia d = porFecha.get(f);
            DiaActividad dia = d == null ? new DiaActividad(f, 0, 0) : new DiaActividad(f, d.respondidas(), d.aciertos());
            lista.add(dia);
            if (dia.respondidas() > 0) {
                activos++;
                respondidas += dia.respondidas();
                aciertos += dia.aciertos();
                if (mejor == null || dia.respondidas() > mejor.respondidas()) {
                    mejor = dia;
                }
            }
        }
        return new ActividadResponse(lista, activos, respondidas, aciertos, mejor);
    }

    // ============================================
    // Fortalezas y debilidades
    // ============================================

    @Override
    public AnalisisResponse analisis(String correo) {
        Map<String, MiAvancePort.Rendimiento> datos = new HashMap<>();
        for (MiAvancePort.Rendimiento r : port.rendimiento(idDe(correo))) {
            datos.put(r.tema() + "/" + r.dificultad(), r);
        }

        List<Rendimiento> detalle = new ArrayList<>();
        for (String tema : TEMAS) {
            for (String dificultad : DIFICULTADES) {
                MiAvancePort.Rendimiento r = datos.get(tema + "/" + dificultad);
                long respondidas = r == null ? 0 : r.respondidas();
                long correctas = r == null ? 0 : r.correctas();
                detalle.add(new Rendimiento(tema, dificultad, respondidas, correctas, porcentaje(correctas, respondidas)));
            }
        }

        List<Rendimiento> conDatos = detalle.stream().filter(r -> r.respondidas() >= MIN_RESPUESTAS).toList();
        List<Rendimiento> fortalezas = conDatos.stream()
                .filter(r -> r.porcentaje() >= UMBRAL_FORTALEZA)
                .sorted(Comparator.comparingInt(Rendimiento::porcentaje).reversed()
                        .thenComparing(Comparator.comparingLong(Rendimiento::respondidas).reversed()))
                .limit(MAX_LISTA).toList();
        List<Rendimiento> debilidades = conDatos.stream()
                .filter(r -> r.porcentaje() < UMBRAL_DEBILIDAD)
                .sorted(Comparator.comparingInt(Rendimiento::porcentaje)
                        .thenComparing(Comparator.comparingLong(Rendimiento::respondidas).reversed()))
                .limit(MAX_LISTA).toList();

        return new AnalisisResponse(detalle, fortalezas, debilidades, recomendar(detalle, debilidades));
    }

    /**
     * Qué practicar ahora:
     * 1) la mayor debilidad; 2) si no hay, el siguiente nivel sin dominar del tema menos practicado;
     * 3) si ya domina todo, avanzado del tema con menor porcentaje.
     */
    private Recomendacion recomendar(List<Rendimiento> detalle, List<Rendimiento> debilidades) {
        if (detalle.stream().allMatch(r -> r.respondidas() == 0)) {
            return null;
        }
        if (!debilidades.isEmpty()) {
            Rendimiento d = debilidades.get(0);
            return new Recomendacion(d.tema(), d.dificultad(), "En " + nombre(d) + " llevas " + d.porcentaje()
                    + " % de acierto. Repasemos eso juntos: con las pistas y el chat lo vas a dominar.");
        }
        List<String> temasPorPractica = TEMAS.stream()
                .sorted(Comparator.comparingLong(t -> respondidasDelTema(detalle, t)))
                .toList();
        for (String tema : temasPorPractica) {
            for (String dificultad : DIFICULTADES) {
                Rendimiento r = buscar(detalle, tema, dificultad);
                if (r.respondidas() < MIN_RESPUESTAS) {
                    return new Recomendacion(tema, dificultad, r.respondidas() == 0
                            ? "Todavía no has probado " + nombre(r) + ". ¡Es un buen momento para empezar!"
                            : "Llevas pocas preguntas de " + nombre(r) + ". Unas cuantas más y sabremos cómo vas.");
                }
                if (r.porcentaje() < UMBRAL_FORTALEZA) {
                    return new Recomendacion(tema, dificultad, "Vas bien en " + nombre(r) + " (" + r.porcentaje()
                            + " %). Un poco más de práctica y se vuelve una de tus fortalezas.");
                }
            }
        }
        Rendimiento reto = detalle.stream()
                .filter(r -> r.dificultad().equals("avanzado"))
                .min(Comparator.comparingInt(Rendimiento::porcentaje))
                .orElseThrow();
        return new Recomendacion(reto.tema(), reto.dificultad(),
                "¡Dominas todos los temas! Sigue con " + nombre(reto) + " para no perder el ritmo.");
    }

    private static long respondidasDelTema(List<Rendimiento> detalle, String tema) {
        return detalle.stream().filter(r -> r.tema().equals(tema)).mapToLong(Rendimiento::respondidas).sum();
    }

    private static Rendimiento buscar(List<Rendimiento> detalle, String tema, String dificultad) {
        return detalle.stream().filter(r -> r.tema().equals(tema) && r.dificultad().equals(dificultad))
                .findFirst().orElseThrow();
    }

    private static String nombre(Rendimiento r) {
        return NOMBRE_TEMA.get(r.tema()) + " " + NOMBRE_DIFICULTAD.get(r.dificultad());
    }

    private static int porcentaje(long correctas, long respondidas) {
        return respondidas == 0 ? 0 : (int) Math.round(correctas * 100.0 / respondidas);
    }

    // ============================================
    // Meta semanal
    // ============================================

    @Override
    public MetaSemanalResponse meta(String correo) {
        Long id = idDe(correo);
        return metaDe(id, port.metaSemanal(id));
    }

    @Override
    @Transactional
    public MetaSemanalResponse cambiarMeta(String correo, int meta) {
        if (meta < 5 || meta > 300) {
            throw new IllegalArgumentException("La meta debe estar entre 5 y 300 preguntas");
        }
        Long id = idDe(correo);
        port.guardarMetaSemanal(id, meta);
        return metaDe(id, meta);
    }

    private MetaSemanalResponse metaDe(Long id, int meta) {
        LocalDate hoy = hoy();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);
        long respondidas = 0;
        long aciertos = 0;
        for (MiAvancePort.Dia d : port.actividadDesde(id, lunes)) {
            respondidas += d.respondidas();
            aciertos += d.aciertos();
        }
        int restantes = (int) ChronoUnit.DAYS.between(hoy, domingo) + 1;
        return new MetaSemanalResponse(meta, respondidas, aciertos, lunes, domingo, restantes,
                meta > 0 && respondidas >= meta);
    }

    // ============================================

    private LocalDate hoy() {
        return LocalDate.now(clock.withZone(zona));
    }

    private Long idDe(String correo) {
        return usuarios.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"))
                .getId();
    }
}
