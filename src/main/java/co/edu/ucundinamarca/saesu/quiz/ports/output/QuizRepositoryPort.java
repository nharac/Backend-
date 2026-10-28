package co.edu.ucundinamarca.saesu.quiz.ports.output;

import co.edu.ucundinamarca.saesu.quiz.domain.model.IntentoQuiz;
import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Persistencia del quiz. Cada método es una o dos consultas como máximo. */
public interface QuizRepositoryPort {

    /** Preguntas activas al azar (de un tema o de todos si tema es null), con sus opciones. */
    List<PreguntaQuiz> preguntasAlAzar(String tema, int cantidad);

    /** Guarda el intento y el orden de sus preguntas; devuelve el id. */
    Long crearIntento(Long usuarioId, String tema, Instant iniciadoEn, Instant limiteEn, List<Long> preguntaIds);

    Optional<IntentoQuiz> buscarIntento(Long intentoId);

    /** Preguntas del intento en el orden en que se mostraron. */
    List<PreguntaQuiz> preguntasDelIntento(Long intentoId);

    /** Lo que eligió en cada pregunta (null = sin responder), en el mismo orden. */
    record Eleccion(Long preguntaId, String letra, boolean correcta) {}

    /**
     * Marca el intento como finalizado. Devuelve false si ya estaba finalizado
     * (así dos entregas a la vez no se cuentan dos veces).
     */
    boolean finalizar(Long intentoId, Instant finalizadoEn, int correctas, boolean fueraDeTiempo, List<Eleccion> elecciones);

    /** Letras elegidas por pregunta de un intento ya finalizado, en orden. */
    List<Eleccion> eleccionesDe(Long intentoId);

    /** Últimos intentos finalizados, del más reciente al más antiguo. */
    List<IntentoQuiz> historial(Long usuarioId, int limite);

    /** Totales de todos los intentos finalizados del usuario. */
    record Totales(long intentos, double promedioPorcentaje, double mejorPorcentaje) {}

    Totales totales(Long usuarioId);
}
