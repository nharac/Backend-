package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Cifra;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Logro;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.LogrosUseCase;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort.Estadisticas;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evalúa y entrega los logros de un usuario, en 7 categorías; los RETOS son las insignias especiales.
 * Cada logro se cumple cuando su cifra llega a la meta. Las cifras salen de los datos que ya existen
 * (práctica, quiz, foro, chat, puntos), así que basta con evaluar al consultar los logros
 * o después de responder en práctica. Además del estado, se devuelve el avance hacia la meta.
 * Los logros EXCLUSIVOS (desarrolladores) no se ganan jugando y solo los ve quien los tiene.
 */
@Service
public class LogrosService implements LogrosUseCase {

    private static final List<String> TEMAS = List.of("limites", "derivadas", "integrales");
    /** Los que se pueden ganar jugando (todos menos los exclusivos). */
    static final List<Logro> GANABLES = Arrays.stream(Logro.values()).filter(l -> !l.esExclusivo()).toList();
    private static final Set<String> CODIGOS_GANABLES = GANABLES.stream().map(Logro::name).collect(Collectors.toSet());

    private final LogrosPort repositorio;
    private final UsuarioRepositoryPort usuarios;
    /** Correos con la insignia de desarrollador (saesu.desarrolladores, separados por coma). */
    private final Set<String> desarrolladores;

    public LogrosService(LogrosPort repositorio, UsuarioRepositoryPort usuarios,
                         @Value("${saesu.desarrolladores:}") String desarrolladores) {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.desarrolladores = Arrays.stream(desarrolladores == null ? new String[0] : desarrolladores.split(","))
                .map(c -> c.trim().toLowerCase(Locale.ROOT))
                .filter(c -> !c.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional
    public List<LogroResponse> listar(String correo) {
        Long usuarioId = usuarios.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"))
                .getId();
        return listar(usuarioId);
    }

    /**
     * Revisa todas las condiciones y guarda los logros que se acaban de cumplir.
     * @return los logros recién desbloqueados (vacío si no hay nuevos)
     */
    @Transactional
    public List<LogroResponse> evaluar(Long usuarioId) {
        Map<String, OffsetDateTime> yaTiene = repositorio.obtenidos(usuarioId);
        return tieneTodos(yaTiene)
                ? new ArrayList<>() // ya los tiene todos: no hace falta consultar nada más
                : evaluar(usuarioId, yaTiene, repositorio.estadisticas(usuarioId));
    }

    /** Los logros con su estado (primero evalúa, por si alguno se cumplió en el foro). Los exclusivos solo si los tiene. */
    @Transactional
    public List<LogroResponse> listar(Long usuarioId) {
        Map<String, OffsetDateTime> obtenidos = new HashMap<>(repositorio.obtenidos(usuarioId));
        // Con todos obtenidos no hace falta consultar las cifras (e queda null y el avance va completo)
        Estadisticas e = tieneTodos(obtenidos) ? null : repositorio.estadisticas(usuarioId);
        if (e != null) {
            for (LogroResponse nuevo : evaluar(usuarioId, obtenidos, e)) {
                obtenidos.put(nuevo.getCodigo(), nuevo.getFechaObtenido());
            }
        }
        int tiene = ganablesQueTiene(obtenidos);
        return Arrays.stream(Logro.values())
                .filter(l -> !l.esExclusivo() || obtenidos.containsKey(l.name()))
                .map(l -> aRespuesta(l, e, tiene, obtenidos.get(l.name())))
                .toList();
    }

    private List<LogroResponse> evaluar(Long usuarioId, Map<String, OffsetDateTime> yaTiene, Estadisticas e) {
        List<LogroResponse> nuevos = new ArrayList<>();
        int tiene = ganablesQueTiene(yaTiene);
        for (Logro logro : Logro.values()) {
            if (!yaTiene.containsKey(logro.name()) && cumple(logro, e, tiene)
                    && repositorio.guardar(usuarioId, logro.name())) {
                if (!logro.esExclusivo()) {
                    tiene++;
                }
                nuevos.add(aRespuesta(logro, e, tiene, OffsetDateTime.now()));
            }
        }
        return nuevos;
    }

    private static boolean tieneTodos(Map<String, OffsetDateTime> obtenidos) {
        return ganablesQueTiene(obtenidos) >= GANABLES.size();
    }

    /** Cuántos logros ganables tiene (no cuenta exclusivos ni códigos viejos que ya no existan). */
    private static int ganablesQueTiene(Map<String, OffsetDateTime> obtenidos) {
        return (int) obtenidos.keySet().stream().filter(CODIGOS_GANABLES::contains).count();
    }

    private boolean cumple(Logro logro, Estadisticas e, int logrosQueTiene) {
        long meta = meta(logro, e);
        if (meta <= 0 || valor(logro.getCifra(), e, logrosQueTiene) < meta) {
            return false;
        }
        // Precisión: además de las respuestas, hay que tener el porcentaje mínimo de acierto
        int precision = logro.precisionMinima();
        return precision == 0 || e.get(Cifra.ACIERTOS) * 100 >= e.get(Cifra.RESPONDIDAS) * precision;
    }

    /** La meta del logro; las de meta 0 dependen del banco de preguntas o del catálogo. */
    private long meta(Logro logro, Estadisticas e) {
        if (logro.getMeta() > 0) {
            return logro.getMeta();
        }
        if (logro.getCifra() == Cifra.LOGROS) {
            return GANABLES.size() - 1L; // todos los demás
        }
        if (e == null) {
            return 0;
        }
        return switch (logro.getCifra()) {
            case PREGUNTAS_RESUELTAS -> TEMAS.stream().mapToLong(e::preguntasDelTema).sum();
            case AVANZADAS -> e.get(Cifra.TOTAL_AVANZADAS);
            default -> e.preguntasDelTema(temaDe(logro.getCifra()));
        };
    }

    /** El valor actual de una cifra, incluidas las que se calculan a partir de otras. */
    private long valor(Cifra cifra, Estadisticas e, int logrosQueTiene) {
        return switch (cifra) {
            case RESUELTAS_LIMITES, RESUELTAS_DERIVADAS, RESUELTAS_INTEGRALES -> e.resueltasDelTema(temaDe(cifra));
            case TEMAS_EXPLORADOS -> TEMAS.stream().filter(t -> e.resueltasDelTema(t) > 0).count();
            case TEMAS_DOMINADOS -> TEMAS.stream().filter(t -> domina(e, t)).count();
            case PREGUNTAS_RESUELTAS -> TEMAS.stream().mapToLong(e::resueltasDelTema).sum();
            case LOGROS -> logrosQueTiene;
            case DESARROLLADOR -> esDesarrollador(e.correo()) ? 1 : 0;
            default -> e.get(cifra);
        };
    }

    private boolean esDesarrollador(String correo) {
        return correo != null && desarrolladores.contains(correo.trim().toLowerCase(Locale.ROOT));
    }

    /** Acertó (alguna vez) todas las preguntas que hay del tema. */
    private boolean domina(Estadisticas e, String tema) {
        long total = e.preguntasDelTema(tema);
        return total > 0 && e.resueltasDelTema(tema) >= total;
    }

    private static String temaDe(Cifra cifra) {
        return switch (cifra) {
            case RESUELTAS_LIMITES -> "limites";
            case RESUELTAS_DERIVADAS -> "derivadas";
            default -> "integrales";
        };
    }

    /**
     * Arma la respuesta. Con estadísticas null (ya tiene todo) el avance queda completo.
     * Un secreto sin obtener se muestra con su pista en lugar del nombre y la descripción.
     */
    private LogroResponse aRespuesta(Logro l, Estadisticas e, int logrosQueTiene, OffsetDateTime fecha) {
        boolean obtenido = fecha != null;
        long meta = Math.max(meta(l, e), 1);
        long progreso = obtenido || e == null ? meta : Math.min(valor(l.getCifra(), e, logrosQueTiene), meta);
        boolean oculto = l.esSecreto() && !obtenido;
        return LogroResponse.builder()
                .codigo(l.name())
                .nombre(oculto ? "???" : l.getNombre())
                .descripcion(oculto ? l.getPistaSecreta() : l.getDescripcion())
                .icono(oculto ? "lock" : l.getIcono())
                .categoria(l.getCategoria().name())
                .rareza(l.getRareza().name())
                .secreto(l.esSecreto())
                .obtenido(obtenido)
                .fechaObtenido(fecha)
                .progreso(progreso)
                .meta(meta)
                .build();
    }
}
