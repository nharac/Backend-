package co.edu.ucundinamarca.saesu.duelo.vivo;

import co.edu.ucundinamarca.saesu.duelo.domain.exception.DueloNoEncontradoException;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.EstadoSala;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.Jugador;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.MiRespuesta;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.Opcion;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoDtos.PreguntaVivo;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoPort.Respuesta;
import co.edu.ucundinamarca.saesu.duelo.vivo.VivoPort.Sala;
import co.edu.ucundinamarca.saesu.notificacion.ports.input.NotificarUseCase;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Retos en vivo: el reloj lo lleva el servidor. Con {@code inicia_en} y los segundos por pregunta
 * se sabe qué pregunta va en cada momento, así ambos jugadores ven lo mismo aunque consulten a destiempo.
 * Puntaje estilo concurso: 100 por acertar más un bono de hasta 50 por responder rápido.
 */
@Service
public class VivoService {

    static final int PREGUNTAS = 5;
    static final int SEGUNDOS_POR_PREGUNTA = 20;
    static final int CUENTA_REGRESIVA = 5;
    static final int PUNTOS_ACIERTO = 100;
    static final int BONO_MAXIMO = 50;
    private static final String LETRAS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final VivoPort port;
    private final QuizRepositoryPort banco;
    private final Clock clock;
    private final SecureRandom azar = new SecureRandom();
    private NotificarUseCase avisos;

    public VivoService(VivoPort port, QuizRepositoryPort banco, Clock clock) {
        this.port = port;
        this.banco = banco;
        this.clock = clock;
    }

    @Autowired(required = false)
    public void setAvisos(NotificarUseCase avisos) {
        this.avisos = avisos;
    }

    private Long idDe(String correo) {
        return port.idPorCorreo(correo).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(clock);
    }

    String nuevoCodigo() {
        for (int intento = 0; intento < 20; intento++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) sb.append(LETRAS.charAt(azar.nextInt(LETRAS.length())));
            if (!port.codigoExiste(sb.toString())) return sb.toString();
        }
        throw new IllegalStateException("No se pudo generar un código de sala");
    }

    public EstadoSala crear(String correo, Long invitadoId, String tema) {
        Long yo = idDe(correo);
        if (invitadoId != null) {
            if (invitadoId.equals(yo)) throw new IllegalArgumentException("No puedes retarte a ti mismo");
            if (!port.esEstudiante(invitadoId)) throw new IllegalArgumentException("Ese compañero no existe");
        }
        List<Long> ids = banco.preguntasAlAzar(tema, PREGUNTAS).stream().map(PreguntaQuiz::id).toList();
        if (ids.size() < 3) throw new IllegalArgumentException("Todavía no hay suficientes preguntas de ese tema");
        String codigo = nuevoCodigo();
        port.crear(codigo, yo, invitadoId, tema, SEGUNDOS_POR_PREGUNTA, ids, ahora());
        Sala sala = port.buscar(codigo).orElseThrow(DueloNoEncontradoException::new);
        if (avisos != null && invitadoId != null) {
            avisos.notificar(invitadoId, "EN_VIVO", sala.anfitrion() + " te invita a un reto en vivo",
                    Tema.nombreDe(tema) + " · código " + codigo + ". ¡Entra ya!", "/retos/vivo/" + codigo);
        }
        return estado(sala, yo);
    }

    public EstadoSala unirse(String correo, String codigo) {
        Long yo = idDe(correo);
        Sala sala = buscar(codigo);
        boolean yaEsta = yo.equals(sala.anfitrionId()) || (yo.equals(sala.invitadoId()) && !"ESPERANDO".equals(sala.estado()));
        if (yaEsta) {
            return estado(sala, yo);
        }
        if (!port.unir(sala.id(), yo, ahora().plusSeconds(CUENTA_REGRESIVA))) {
            throw new IllegalArgumentException("Esta sala ya no está disponible");
        }
        return estado(buscar(codigo), yo);
    }

    public EstadoSala ver(String correo, String codigo) {
        Long yo = idDe(correo);
        Sala sala = buscar(codigo);
        boolean participa = yo.equals(sala.anfitrionId()) || yo.equals(sala.invitadoId());
        boolean puedeEntrar = "ESPERANDO".equals(sala.estado()) && sala.invitadoId() == null;
        if (!participa && !puedeEntrar) throw new DueloNoEncontradoException();
        return estado(sala, yo);
    }

    public EstadoSala cancelar(String correo, String codigo) {
        Long yo = idDe(correo);
        Sala sala = buscar(codigo);
        if (!yo.equals(sala.anfitrionId())) throw new DueloNoEncontradoException();
        if ("ESPERANDO".equals(sala.estado())) port.cambiarEstado(sala.id(), "CANCELADA");
        return estado(buscar(codigo), yo);
    }

    public MiRespuesta responder(String correo, String codigo, int orden, String letra) {
        Long yo = idDe(correo);
        Sala sala = buscar(codigo);
        if (!yo.equals(sala.anfitrionId()) && !yo.equals(sala.invitadoId())) throw new DueloNoEncontradoException();
        if (!"JUGANDO".equals(sala.estado()) || sala.iniciaEn() == null) throw new IllegalArgumentException("El reto no está en curso");
        long transcurrido = Duration.between(sala.iniciaEn(), ahora()).toMillis();
        int indice = transcurrido < 0 ? -1 : (int) (transcurrido / 1000 / sala.segundosPorPregunta());
        if (orden != indice) throw new IllegalArgumentException("Esa pregunta ya pasó");
        List<PreguntaQuiz> preguntas = port.preguntas(sala.id());
        PreguntaQuiz p = preguntas.get(orden);
        String correcta = letraCorrecta(p);
        boolean acerto = letra.equalsIgnoreCase(correcta);
        long msEnPregunta = transcurrido - (long) indice * sala.segundosPorPregunta() * 1000;
        double fraccionRestante = Math.max(0, 1 - msEnPregunta / (sala.segundosPorPregunta() * 1000.0));
        int puntos = acerto ? PUNTOS_ACIERTO + (int) Math.round(BONO_MAXIMO * fraccionRestante) : 0;
        if (!port.responder(sala.id(), yo, orden, letra.toUpperCase(Locale.ROOT), acerto, puntos, ahora())) {
            throw new IllegalArgumentException("Ya respondiste esta pregunta");
        }
        return new MiRespuesta(letra.toUpperCase(Locale.ROOT), acerto, correcta, puntos);
    }

    // ---------------------------------------------------------------

    private Sala buscar(String codigo) {
        return port.buscar(codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT)).orElseThrow(DueloNoEncontradoException::new);
    }

    private static String letraCorrecta(PreguntaQuiz p) {
        return p.opciones().stream().filter(PreguntaQuiz.Opcion::correcta).map(PreguntaQuiz.Opcion::letra).findFirst().orElse("");
    }

    EstadoSala estado(Sala sala, Long yo) {
        String estado = sala.estado();
        Integer cuenta = null;
        int indice = 0;
        Integer restantes = null;
        PreguntaVivo pregunta = null;
        MiRespuesta mia = null;
        List<PreguntaQuiz> preguntas = List.of();
        if ("JUGANDO".equals(estado) && sala.iniciaEn() != null) {
            long ms = Duration.between(sala.iniciaEn(), ahora()).toMillis();
            if (ms < 0) {
                cuenta = (int) Math.ceil(-ms / 1000.0);
            } else {
                indice = (int) (ms / 1000 / sala.segundosPorPregunta());
                if (indice >= sala.total()) {
                    estado = "TERMINADA";
                    port.cambiarEstado(sala.id(), estado);
                } else {
                    restantes = sala.segundosPorPregunta() - (int) ((ms / 1000) % sala.segundosPorPregunta());
                }
            }
        }
        List<Respuesta> respuestas = "ESPERANDO".equals(estado) || "CANCELADA".equals(estado) ? List.of() : port.respuestas(sala.id());
        if ("JUGANDO".equals(estado) && cuenta == null) {
            preguntas = port.preguntas(sala.id());
            PreguntaQuiz p = preguntas.get(indice);
            pregunta = new PreguntaVivo(indice, p.id(), p.enunciado(),
                    p.opciones().stream().map(o -> new Opcion(o.letra(), o.texto())).toList());
            final int actual = indice;
            Optional<Respuesta> r = respuestas.stream().filter(x -> x.usuarioId().equals(yo) && x.orden() == actual).findFirst();
            if (r.isPresent()) mia = new MiRespuesta(r.get().letra(), r.get().correcta(), letraCorrecta(p), r.get().puntos());
        }
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugador(sala.anfitrion(), sala.anfitrionId(), yo, respuestas, indice));
        if (sala.invitadoId() != null) jugadores.add(jugador(sala.invitado(), sala.invitadoId(), yo, respuestas, indice));
        String resultado = null;
        if ("TERMINADA".equals(estado) && jugadores.size() == 2) {
            Jugador mio = jugadores.stream().filter(Jugador::soyYo).findFirst().orElse(null);
            Jugador otro = jugadores.stream().filter(j -> !j.soyYo()).findFirst().orElse(null);
            if (mio != null && otro != null) {
                resultado = mio.puntos() == otro.puntos() ? "EMPATE" : mio.puntos() > otro.puntos() ? "GANASTE" : "PERDISTE";
            }
        }
        boolean soyAnfitrion = yo.equals(sala.anfitrionId());
        String rival = soyAnfitrion ? sala.invitado() : sala.anfitrion();
        return new EstadoSala(sala.codigo(), sala.tema(), Tema.nombreDe(sala.tema()), estado, soyAnfitrion, sala.invitadoId() == null,
                rival, cuenta, indice, sala.total(), sala.segundosPorPregunta(), restantes, pregunta, mia, jugadores, resultado);
    }

    private static Jugador jugador(String nombre, Long id, Long yo, List<Respuesta> respuestas, int indice) {
        int puntos = 0;
        int aciertos = 0;
        boolean respondio = false;
        for (Respuesta r : respuestas) {
            if (!r.usuarioId().equals(id)) continue;
            puntos += r.puntos();
            if (r.correcta()) aciertos++;
            if (r.orden() == indice) respondio = true;
        }
        return new Jugador(nombre, id.equals(yo), puntos, aciertos, respondio);
    }
}
