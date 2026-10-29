package co.edu.ucundinamarca.saesu.quiz.application.service;

import co.edu.ucundinamarca.saesu.quiz.application.dto.request.FinalizarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.request.IniciarQuizRequest;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizHistorialResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizIniciadoResponse;
import co.edu.ucundinamarca.saesu.quiz.application.dto.response.QuizResultadoResponse;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizNoEncontradoException;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizYaFinalizadoException;
import co.edu.ucundinamarca.saesu.quiz.domain.model.IntentoQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.input.QuizUseCase;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort.Eleccion;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Quiz cronometrado con preguntas del banco de práctica.
 *
 * Reglas:
 *  - De 5 a 20 preguntas al azar de un tema o mezcladas; 90 segundos por pregunta.
 *  - El navegador nunca recibe cuál es la respuesta correcta: se califica aquí al entregar.
 *  - Se puede entregar una sola vez. Si llega después del tiempo (con 10 s de gracia por la red)
 *    se califica igual pero queda marcado "fuera de tiempo".
 *  - No da puntos del ranking: así nadie repite quices solo para subir.
 */
@Service
public class QuizService implements QuizUseCase {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    static final int SEGUNDOS_POR_PREGUNTA = 90;
    static final Duration GRACIA = Duration.ofSeconds(10);
    static final int HISTORIAL = 10;
    static final Set<String> TEMAS = Set.of("limites", "derivadas", "integrales");

    private final QuizRepositoryPort port;
    private final UsuarioRepositoryPort usuarios;
    private final Clock clock;

    @Autowired
    public QuizService(QuizRepositoryPort port, UsuarioRepositoryPort usuarios) {
        this(port, usuarios, Clock.systemUTC());
    }

    QuizService(QuizRepositoryPort port, UsuarioRepositoryPort usuarios, Clock clock) {
        this.port = port;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    @Override
    public QuizIniciadoResponse iniciar(String correo, IniciarQuizRequest request) {
        Long usuarioId = usuarioId(correo);
        String tema = tema(request.tema());
        int cantidad = Math.max(5, Math.min(20, request.cantidad()));

        List<PreguntaQuiz> preguntas = port.preguntasAlAzar(tema, cantidad);
        if (preguntas.isEmpty()) {
            throw new IllegalArgumentException("Todavía no hay preguntas de ese tema para armar un quiz");
        }

        int segundos = preguntas.size() * SEGUNDOS_POR_PREGUNTA;
        Instant inicio = clock.instant();
        Instant limite = inicio.plusSeconds(segundos);
        Long id = port.crearIntento(usuarioId, tema, inicio, limite,
                preguntas.stream().map(PreguntaQuiz::id).toList());

        List<QuizIniciadoResponse.Pregunta> lista = new ArrayList<>();
        for (int i = 0; i < preguntas.size(); i++) {
            PreguntaQuiz p = preguntas.get(i);
            lista.add(new QuizIniciadoResponse.Pregunta(i + 1, p.id(), p.tema(), p.dificultad(), p.enunciado(),
                    opcionesSinRespuesta(p)));
        }
        log.info("Quiz {} iniciado: tema {}, {} preguntas", id, tema == null ? "mezclado" : tema, lista.size());
        return new QuizIniciadoResponse(id, tema, lista.size(), segundos, limite, lista);
    }

    @Override
    public QuizResultadoResponse finalizar(String correo, Long intentoId, FinalizarQuizRequest request) {
        IntentoQuiz intento = intentoPropio(correo, intentoId);
        if (intento.finalizado()) {
            throw new QuizYaFinalizadoException("Este quiz ya fue entregado");
        }

        Map<Long, String> elegidas = new HashMap<>();
        for (FinalizarQuizRequest.Respuesta r : request.respuestas()) {
            if (r.letra() != null && !r.letra().isBlank()) {
                elegidas.put(r.preguntaId(), r.letra().trim().toUpperCase(Locale.ROOT));
            }
        }

        List<PreguntaQuiz> preguntas = port.preguntasDelIntento(intentoId);
        List<Eleccion> elecciones = new ArrayList<>();
        int correctas = 0;
        for (PreguntaQuiz p : preguntas) {
            String letra = elegidas.get(p.id());
            boolean existe = letra != null && p.opciones().stream().anyMatch(o -> o.letra().equals(letra));
            String valida = existe ? letra : null;
            boolean acerto = valida != null && p.letraCorrecta().map(valida::equals).orElse(false);
            if (acerto) correctas++;
            elecciones.add(new Eleccion(p.id(), valida, acerto));
        }

        Instant ahora = clock.instant();
        boolean tarde = ahora.isAfter(intento.limiteEn().plus(GRACIA));
        QuizRepositoryPort.Totales antes = port.totales(intento.usuarioId());

        if (!port.finalizar(intentoId, ahora, correctas, tarde, elecciones)) {
            throw new QuizYaFinalizadoException("Este quiz ya fue entregado");
        }

        int porcentaje = porcentaje(correctas, preguntas.size());
        log.info("Quiz {} entregado: {}/{} correctas ({}%){}", intentoId, correctas, preguntas.size(), porcentaje,
                tarde ? " fuera de tiempo" : "");
        boolean mejorMarca = antes.intentos() > 0 && porcentaje > antes.mejorPorcentaje();
        return resultado(intento, preguntas, elecciones, correctas, ahora, tarde, mejorMarca);
    }

    @Override
    public QuizResultadoResponse resultado(String correo, Long intentoId) {
        return revision(intentoPropio(correo, intentoId));
    }

    @Override
    public QuizResultadoResponse resultadoDeEstudiante(Long estudianteId, Long intentoId) {
        IntentoQuiz intento = port.buscarIntento(intentoId)
                .filter(i -> i.usuarioId().equals(estudianteId))
                .orElseThrow(() -> new QuizNoEncontradoException("Quiz no encontrado"));
        return revision(intento);
    }

    private QuizResultadoResponse revision(IntentoQuiz intento) {
        Long intentoId = intento.id();
        if (!intento.finalizado()) {
            throw new IllegalArgumentException("Este quiz todavía no se ha entregado");
        }
        List<Eleccion> elecciones = port.eleccionesDe(intentoId);
        return resultado(intento, port.preguntasDelIntento(intentoId), elecciones,
                intento.correctas() == null ? 0 : intento.correctas(), intento.finalizadoEn(),
                intento.fueraDeTiempo(), false);
    }

    @Override
    public QuizHistorialResponse historial(String correo) {
        Long usuarioId = usuarioId(correo);
        QuizRepositoryPort.Totales t = port.totales(usuarioId);
        List<QuizHistorialResponse.Intento> ultimos = port.historial(usuarioId, HISTORIAL).stream()
                .map(i -> {
                    int correctas = i.correctas() == null ? 0 : i.correctas();
                    return new QuizHistorialResponse.Intento(i.id(), i.tema(), correctas, i.cantidad(),
                            porcentaje(correctas, i.cantidad()), duracion(i, i.finalizadoEn()),
                            i.fueraDeTiempo(), i.finalizadoEn());
                })
                .toList();
        return new QuizHistorialResponse(t.intentos(), (int) Math.round(t.promedioPorcentaje()),
                (int) Math.round(t.mejorPorcentaje()), ultimos);
    }

    // ---------------------------------------------------------------

    /** null = mixto. */
    static String tema(String tema) {
        if (tema == null || tema.isBlank() || "mixto".equalsIgnoreCase(tema.trim())) {
            return null;
        }
        String t = tema.trim().toLowerCase(Locale.ROOT);
        if (!TEMAS.contains(t)) {
            throw new IllegalArgumentException("El tema debe ser limites, derivadas, integrales o mixto");
        }
        return t;
    }

    static int porcentaje(int correctas, int total) {
        return total == 0 ? 0 : (int) Math.round(100.0 * correctas / total);
    }

    /** Tiempo usado; si se pasó del límite cuenta solo hasta el límite. */
    private static long duracion(IntentoQuiz i, Instant fin) {
        if (fin == null) return 0;
        Instant hasta = fin.isAfter(i.limiteEn()) ? i.limiteEn() : fin;
        return Math.max(0, Duration.between(i.iniciadoEn(), hasta).toSeconds());
    }

    private Long usuarioId(String correo) {
        return usuarios.buscarPorCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"))
                .getId();
    }

    /** El intento existe y es de quien pregunta; si no, 404 (no se revela que existe). */
    private IntentoQuiz intentoPropio(String correo, Long intentoId) {
        Long usuarioId = usuarioId(correo);
        return port.buscarIntento(intentoId)
                .filter(i -> i.usuarioId().equals(usuarioId))
                .orElseThrow(() -> new QuizNoEncontradoException("Quiz no encontrado"));
    }

    private static List<QuizIniciadoResponse.Opcion> opcionesSinRespuesta(PreguntaQuiz p) {
        return p.opciones().stream().map(o -> new QuizIniciadoResponse.Opcion(o.letra(), o.texto())).toList();
    }

    private static QuizResultadoResponse resultado(IntentoQuiz intento, List<PreguntaQuiz> preguntas,
                                                   List<Eleccion> elecciones, int correctas, Instant fin,
                                                   boolean tarde, boolean mejorMarca) {
        Map<Long, Eleccion> porPregunta = new HashMap<>();
        elecciones.forEach(e -> porPregunta.put(e.preguntaId(), e));

        List<QuizResultadoResponse.Detalle> detalle = new ArrayList<>();
        for (int i = 0; i < preguntas.size(); i++) {
            PreguntaQuiz p = preguntas.get(i);
            Eleccion e = porPregunta.get(p.id());
            String letra = e == null ? null : e.letra();
            boolean correcta = e != null && e.correcta();
            detalle.add(new QuizResultadoResponse.Detalle(i + 1, p.id(), p.tema(), p.enunciado(),
                    opcionesSinRespuesta(p), letra, p.letraCorrecta().orElse(null), correcta, p.pista()));
        }
        return new QuizResultadoResponse(intento.id(), intento.tema(), correctas, preguntas.size(),
                porcentaje(correctas, preguntas.size()), duracion(intento, fin), tarde, fin, mejorMarca, detalle);
    }
}
