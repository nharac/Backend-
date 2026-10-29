package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Logro;
import co.edu.ucundinamarca.saesu.gamificacion.ports.input.LogrosUseCase;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort.Estadisticas;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
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
public class LogrosService implements LogrosUseCase {

    private static final int ACERTADAS_A_LA_PRIMERA = 10;
    private static final int RESPUESTAS_COLABORADOR = 10;
    private static final int PUNTOS_CONSTANTE = 500;

    private final LogrosPort repositorio;
    private final UsuarioRepositoryPort usuarios;

    public LogrosService(LogrosPort repositorio, UsuarioRepositoryPort usuarios) {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
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
        return evaluar(usuarioId, repositorio.obtenidos(usuarioId));
    }

    /** Todos los logros con su estado (primero evalúa, por si alguno se cumplió en el foro). */
    @Transactional
    public List<LogroResponse> listar(Long usuarioId) {
        Map<String, OffsetDateTime> obtenidos = repositorio.obtenidos(usuarioId);
        for (LogroResponse nuevo : evaluar(usuarioId, obtenidos)) {
            obtenidos.put(nuevo.getCodigo(), nuevo.getFechaObtenido());
        }
        return Arrays.stream(Logro.values())
                .map(l -> aRespuesta(l, obtenidos.containsKey(l.name()), obtenidos.get(l.name())))
                .toList();
    }

    private List<LogroResponse> evaluar(Long usuarioId, Map<String, OffsetDateTime> yaTiene) {
        List<LogroResponse> nuevos = new ArrayList<>();
        if (yaTiene.size() >= Logro.values().length) {
            return nuevos; // ya los tiene todos: no hace falta consultar nada más
        }
        Estadisticas e = repositorio.estadisticas(usuarioId);
        for (Logro logro : Logro.values()) {
            if (!yaTiene.containsKey(logro.name()) && cumple(logro, e) && repositorio.guardar(usuarioId, logro.name())) {
                nuevos.add(aRespuesta(logro, true, OffsetDateTime.now()));
            }
        }
        return nuevos;
    }

    private boolean cumple(Logro logro, Estadisticas e) {
        return switch (logro) {
            case PRIMER_ACIERTO -> e.aciertos() >= 1;
            case A_LA_PRIMERA -> e.acertadasAlPrimerIntento() >= ACERTADAS_A_LA_PRIMERA;
            case DOMINIO_LIMITES -> dominaTema(e, "limites");
            case DOMINIO_DERIVADAS -> dominaTema(e, "derivadas");
            case DOMINIO_INTEGRALES -> dominaTema(e, "integrales");
            case RACHA_3 -> e.racha() >= 3;
            case RACHA_7 -> e.racha() >= 7;
            case CURIOSO -> e.publicaciones() >= 1;
            case COLABORADOR -> e.respuestasForo() >= RESPUESTAS_COLABORADOR;
            case RESPUESTA_DE_ORO -> e.respuestasOficiales() >= 1;
            case PUNTOS_500 -> e.puntos() >= PUNTOS_CONSTANTE;
        };
    }

    /** Acertó (alguna vez) todas las preguntas que hay del tema. */
    private boolean dominaTema(Estadisticas e, String tema) {
        long total = e.preguntasDelTema(tema);
        return total > 0 && e.resueltasDelTema(tema) >= total;
    }

    private LogroResponse aRespuesta(Logro l, boolean obtenido, OffsetDateTime fecha) {
        return new LogroResponse(l.name(), l.getNombre(), l.getDescripcion(), l.getIcono(), obtenido, fecha);
    }
}
