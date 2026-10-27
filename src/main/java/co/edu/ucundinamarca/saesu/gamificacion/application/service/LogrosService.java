package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc.LogrosJdbcRepository;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Logro;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Evalúa y entrega los logros de un usuario.
 * Las condiciones se calculan con los datos que ya existen (práctica, foro, puntos),
 * así que basta con evaluar al consultar los logros o después de responder en práctica.
 */
@Service
public class LogrosService {

    private static final int ACERTADAS_A_LA_PRIMERA = 10;
    private static final int RESPUESTAS_COLABORADOR = 10;
    private static final int PUNTOS_CONSTANTE = 500;

    private final LogrosJdbcRepository repositorio;

    public LogrosService(LogrosJdbcRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Revisa todas las condiciones y guarda los logros que se acaban de cumplir.
     * @return los logros recién desbloqueados (vacío si no hay nuevos)
     */
    @Transactional
    public List<LogroResponse> evaluar(Long usuarioId) {
        Map<String, OffsetDateTime> yaTiene = repositorio.obtenidos(usuarioId);
        List<LogroResponse> nuevos = new ArrayList<>();
        for (Logro logro : Logro.values()) {
            if (!yaTiene.containsKey(logro.name()) && cumple(logro, usuarioId) && repositorio.guardar(usuarioId, logro.name())) {
                nuevos.add(aRespuesta(logro, true, OffsetDateTime.now()));
            }
        }
        return nuevos;
    }

    /** Todos los logros con su estado (primero evalúa, por si alguno se cumplió en el foro). */
    @Transactional
    public List<LogroResponse> listar(Long usuarioId) {
        evaluar(usuarioId);
        Map<String, OffsetDateTime> obtenidos = repositorio.obtenidos(usuarioId);
        return Arrays.stream(Logro.values())
                .map(l -> aRespuesta(l, obtenidos.containsKey(l.name()), obtenidos.get(l.name())))
                .toList();
    }

    private boolean cumple(Logro logro, Long u) {
        return switch (logro) {
            case PRIMER_ACIERTO -> repositorio.contarAciertos(u) >= 1;
            case A_LA_PRIMERA -> repositorio.contarAcertadasAlPrimerIntento(u) >= ACERTADAS_A_LA_PRIMERA;
            case DOMINIO_LIMITES -> dominaTema(u, "limites");
            case DOMINIO_DERIVADAS -> dominaTema(u, "derivadas");
            case DOMINIO_INTEGRALES -> dominaTema(u, "integrales");
            case RACHA_3 -> repositorio.rachaActual(u) >= 3;
            case RACHA_7 -> repositorio.rachaActual(u) >= 7;
            case CURIOSO -> repositorio.contarPublicaciones(u) >= 1;
            case COLABORADOR -> repositorio.contarRespuestasForo(u) >= RESPUESTAS_COLABORADOR;
            case RESPUESTA_DE_ORO -> repositorio.contarRespuestasOficiales(u) >= 1;
            case PUNTOS_500 -> repositorio.puntosTotales(u) >= PUNTOS_CONSTANTE;
        };
    }

    /** Acertó (alguna vez) todas las preguntas que hay del tema. */
    private boolean dominaTema(Long u, String tema) {
        long total = repositorio.contarPreguntasDelTema(tema);
        return total > 0 && repositorio.contarResueltasDelTema(u, tema) >= total;
    }

    private LogroResponse aRespuesta(Logro l, boolean obtenido, OffsetDateTime fecha) {
        return new LogroResponse(l.name(), l.getNombre(), l.getDescripcion(), l.getIcono(), obtenido, fecha);
    }
}
