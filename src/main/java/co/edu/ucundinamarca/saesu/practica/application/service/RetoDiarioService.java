package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.ports.input.OtorgarPuntosUseCase;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.OpcionResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PreguntaResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.RetoDiarioResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.OpcionInvalidaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.exception.RetoYaRespondidoException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RetoDiarioPort.Intento;
import co.edu.ucundinamarca.saesu.practica.ports.output.UsuarioConsultaPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Reto del día: la misma pregunta para todos durante el día (zona del negocio, por defecto Colombia) y una sola
 * oportunidad por estudiante. Acertar da 30 Puntos Sumi (y sus Sumi Monedas); fallar lo pierde.
 */
@Service
public class RetoDiarioService {

    public static final int PUNTOS_RETO = 30;

    private final RetoDiarioPort retos;
    private final PreguntaRepositoryPort preguntas;
    private final OpcionRepositoryPort opciones;
    private final UsuarioConsultaPort usuarios;
    private final OtorgarPuntosUseCase puntosService;
    private final Clock reloj;
    private final ZoneId zona;

    /** Reloj y zona llegan como beans (TiempoConfig); en las pruebas se pasa un reloj fijo. */
    public RetoDiarioService(RetoDiarioPort retos, PreguntaRepositoryPort preguntas, OpcionRepositoryPort opciones,
                             UsuarioConsultaPort usuarios, OtorgarPuntosUseCase puntosService, Clock reloj, ZoneId zona) {
        this.retos = retos;
        this.preguntas = preguntas;
        this.opciones = opciones;
        this.usuarios = usuarios;
        this.puntosService = puntosService;
        this.reloj = reloj;
        this.zona = zona;
    }

    /** Hoy en la zona del negocio: el reto cambia a medianoche de esa zona. */
    private LocalDate hoy() {
        return LocalDate.now(reloj.withZone(zona));
    }

    public RetoDiarioResponse hoy(String correo) {
        Long usuarioId = usuarios.obtenerIdPorCorreo(correo);
        LocalDate hoy = hoy();
        Optional<Intento> intento = retos.intento(usuarioId, hoy);
        // Si ya respondió, se muestra la pregunta que respondió (aunque el banco haya cambiado)
        Long preguntaId = intento.map(Intento::preguntaId).orElseGet(() -> preguntaDeHoy(hoy));
        return armar(hoy, preguntaId, intento.orElse(null));
    }

    /** Una sola respuesta por día: si ya respondió, 409 y no cambia nada. */
    @Transactional
    public RetoDiarioResponse responder(String correo, Long opcionId) {
        Long usuarioId = usuarios.obtenerIdPorCorreo(correo);
        LocalDate hoy = hoy();
        if (retos.intento(usuarioId, hoy).isPresent()) {
            throw new RetoYaRespondidoException();
        }
        Long preguntaId = preguntaDeHoy(hoy);
        Opcion opcion = opciones.buscarPorId(opcionId)
                .filter(o -> preguntaId.equals(o.getPreguntaId()))
                .orElseThrow(() -> new OpcionInvalidaException("La opción no es del reto de hoy"));
        Intento intento = new Intento(preguntaId, opcionId, Boolean.TRUE.equals(opcion.getEsCorrecta()));
        if (!retos.registrar(usuarioId, hoy, intento)) {
            throw new RetoYaRespondidoException();
        }
        if (intento.correcta()) {
            puntosService.otorgarPuntos(usuarioId, PUNTOS_RETO, "Reto del día " + hoy);
        } else {
            // Perdió el reto, pero el día sí cuenta para la racha
            puntosService.registrarActividad(usuarioId);
        }
        return armar(hoy, preguntaId, intento);
    }

    private Long preguntaDeHoy(LocalDate hoy) {
        return retos.preguntaDelDia(hoy.toEpochDay())
                .orElseThrow(() -> new PreguntaNoEncontradaException("Todavía no hay preguntas para el reto"));
    }

    private RetoDiarioResponse armar(LocalDate hoy, Long preguntaId, Intento intento) {
        Pregunta pregunta = preguntas.buscarPorId(preguntaId)
                .orElseThrow(() -> new PreguntaNoEncontradaException("Pregunta no encontrada con id: " + preguntaId));
        List<Opcion> lista = opciones.listarPorPreguntaId(preguntaId);
        PreguntaResponse dto = new PreguntaResponse(pregunta.getId(), pregunta.getTema().getValor(), pregunta.getEnunciado(),
                pregunta.getDificultad().getValor(),
                lista.stream().map(o -> new OpcionResponse(o.getId(), o.getLetra(), o.getTexto())).toList());
        if (intento == null) {
            return new RetoDiarioResponse(hoy, "pendiente", dto, null, null, null, PUNTOS_RETO);
        }
        Long correcta = lista.stream().filter(o -> Boolean.TRUE.equals(o.getEsCorrecta())).map(Opcion::getId).findFirst().orElse(null);
        return new RetoDiarioResponse(hoy, intento.correcta() ? "ganado" : "perdido", dto, intento.opcionId(), correcta,
                pregunta.getPista(), PUNTOS_RETO);
    }
}
