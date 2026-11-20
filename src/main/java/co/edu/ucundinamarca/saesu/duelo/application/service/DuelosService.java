package co.edu.ucundinamarca.saesu.duelo.application.service;

import co.edu.ucundinamarca.saesu.duelo.application.dto.request.EntregaDueloRequest;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.CompaneroResponse;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.DueloResponse;
import co.edu.ucundinamarca.saesu.duelo.application.dto.response.PreguntaDueloResponse;
import co.edu.ucundinamarca.saesu.duelo.domain.ModoDuelo;
import co.edu.ucundinamarca.saesu.duelo.domain.exception.DueloNoEncontradoException;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort;
import co.edu.ucundinamarca.saesu.duelo.ports.output.DueloPort.FilaDuelo;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;
import co.edu.ucundinamarca.saesu.quiz.ports.output.QuizRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retos entre compañeros: el retador elige rival, tema y modo ({@link ModoDuelo}); los dos responden
 * las mismas preguntas y gana quien tenga más puntaje (si empatan, quien tarde menos).
 */
@Service
public class DuelosService {

    static final int MIN_PREGUNTAS = 3;
    static final int HISTORIAL = 30;

    private final DueloPort port;
    private final QuizRepositoryPort banco;
    private final Clock clock;

    public DuelosService(DueloPort port, QuizRepositoryPort banco, Clock clock) {
        this.port = port;
        this.banco = banco;
        this.clock = clock;
    }

    private Long idDe(String correo) {
        return port.idPorCorreo(correo).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    public List<CompaneroResponse> companeros(String correo) {
        return port.companeros(idDe(correo));
    }

    public DueloResponse retar(String correo, Long rivalId, String tema) {
        return retar(correo, rivalId, tema, null);
    }

    public DueloResponse retar(String correo, Long rivalId, String tema, String modoTexto) {
        ModoDuelo modo = ModoDuelo.de(modoTexto);
        Long yo = idDe(correo);
        if (yo.equals(rivalId)) throw new IllegalArgumentException("No puedes retarte a ti mismo");
        if (!port.esEstudiante(rivalId)) throw new IllegalArgumentException("Ese compañero no existe");
        List<PreguntaQuiz> elegidas = banco.preguntasAlAzar(tema, modo.preguntas());
        if (elegidas.size() < MIN_PREGUNTAS) throw new IllegalArgumentException("Todavía no hay suficientes preguntas de ese tema");
        List<Long> ids = elegidas.stream().map(PreguntaQuiz::id).toList();
        Long id = port.crear(yo, rivalId, tema, modo.name(), modo.puntajeMaximo(elegidas), ids, LocalDateTime.now(clock));
        return port.buscar(id, yo).map(d -> vista(d, yo)).orElseThrow(DueloNoEncontradoException::new);
    }

    public List<DueloResponse> misDuelos(String correo) {
        Long yo = idDe(correo);
        return port.duelosDe(yo, HISTORIAL).stream().map(d -> vista(d, yo)).toList();
    }

    public List<PreguntaDueloResponse> preguntas(String correo, Long dueloId) {
        Long yo = idDe(correo);
        FilaDuelo d = port.buscar(dueloId, yo).orElseThrow(DueloNoEncontradoException::new);
        if (misCorrectas(d, yo) != null) throw new IllegalArgumentException("Ya respondiste este reto");
        ModoDuelo modo = ModoDuelo.de(d.modo());
        return port.preguntas(dueloId).stream()
                .map(p -> new PreguntaDueloResponse(p.id(), p.enunciado(), modo.valor(p.dificultad()),
                        p.opciones().stream().map(o -> new PreguntaDueloResponse.Opcion(o.letra(), o.texto())).toList()))
                .toList();
    }

    public DueloResponse entregar(String correo, Long dueloId, EntregaDueloRequest entrega) {
        Long yo = idDe(correo);
        FilaDuelo duelo = port.buscar(dueloId, yo).orElseThrow(DueloNoEncontradoException::new);
        ModoDuelo modo = ModoDuelo.de(duelo.modo());
        List<PreguntaQuiz> preguntas = port.preguntas(dueloId);
        Map<Long, String> correctas = new HashMap<>();
        for (PreguntaQuiz p : preguntas) {
            p.opciones().stream().filter(PreguntaQuiz.Opcion::correcta).findFirst()
                    .ifPresent(o -> correctas.put(p.id(), o.letra()));
        }
        // Una sola respuesta por pregunta (la primera que llegue)
        Map<Long, String> elegidas = new HashMap<>();
        for (EntregaDueloRequest.Eleccion e : entrega.respuestas()) {
            if (e.letra() != null) elegidas.putIfAbsent(e.preguntaId(), e.letra());
        }
        int aciertos = modo.calificar(preguntas, elegidas, correctas);
        if (!port.entregar(dueloId, yo, aciertos, entrega.segundos(), LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("Ya respondiste este reto");
        }
        return port.buscar(dueloId, yo).map(d -> vista(d, yo)).orElseThrow(DueloNoEncontradoException::new);
    }

    // ---------------------------------------------------------------

    private static Integer misCorrectas(FilaDuelo d, Long yo) {
        return yo.equals(d.retadorId()) ? d.correctasRetador() : d.correctasRival();
    }

    static DueloResponse vista(FilaDuelo d, Long yo) {
        boolean soyRetador = yo.equals(d.retadorId());
        Integer mias = soyRetador ? d.correctasRetador() : d.correctasRival();
        Integer misSeg = soyRetador ? d.segundosRetador() : d.segundosRival();
        Integer suyas = soyRetador ? d.correctasRival() : d.correctasRetador();
        Integer suyosSeg = soyRetador ? d.segundosRival() : d.segundosRetador();

        String estado;
        String resultado = null;
        if (mias == null) {
            estado = "POR_JUGAR";
            suyas = null; // no se revela el puntaje del otro antes de jugar
            suyosSeg = null;
        } else if (suyas == null) {
            estado = "ESPERANDO";
        } else {
            estado = "TERMINADO";
            resultado = resultado(mias, misSeg, suyas, suyosSeg);
        }
        ModoDuelo modo = ModoDuelo.de(d.modo());
        return new DueloResponse(d.id(), d.tema(), Tema.nombreDe(d.tema()), modo.name(), modo.nombre(), d.puntajeMaximo(),
                modo.limiteSegundos(), soyRetador ? d.rival() : d.retador(),
                soyRetador, estado, resultado, mias, misSeg, suyas, suyosSeg, d.totalPreguntas(), d.creadoEn());
    }

    private static String resultado(int mias, int misSeg, int suyas, int suyosSeg) {
        if (mias != suyas) return mias > suyas ? "GANASTE" : "PERDISTE";
        if (misSeg != suyosSeg) return misSeg < suyosSeg ? "GANASTE" : "PERDISTE";
        return "EMPATE";
    }
}
