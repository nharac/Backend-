package co.edu.ucundinamarca.saesu.duelo.domain;

import co.edu.ucundinamarca.saesu.quiz.domain.model.PreguntaQuiz;

import java.util.List;
import java.util.Map;

/**
 * Modos de reto y cómo se califica cada uno. Todos se califican en el servidor.
 */
public enum ModoDuelo {

    CLASICO("Clásico", 5, null),
    MUERTE_SUBITA("A muerte", 10, null),
    PUNTOS("A puntos", 8, null),
    RELAMPAGO("Relámpago", 10, 60);

    public static final String PATRON = "CLASICO|MUERTE_SUBITA|PUNTOS|RELAMPAGO";

    private final String nombre;
    private final int preguntas;
    private final Integer limiteSegundos;

    ModoDuelo(String nombre, int preguntas, Integer limiteSegundos) {
        this.nombre = nombre;
        this.preguntas = preguntas;
        this.limiteSegundos = limiteSegundos;
    }

    public String nombre() { return nombre; }
    public int preguntas() { return preguntas; }
    public Integer limiteSegundos() { return limiteSegundos; }

    public static ModoDuelo de(String valor) {
        if (valor == null || valor.isBlank()) return CLASICO;
        return valueOf(valor);
    }

    /** Cuánto vale una pregunta (solo en el modo a puntos depende de la dificultad). */
    public int valor(String dificultad) {
        if (this != PUNTOS) return 1;
        if ("avanzado".equalsIgnoreCase(dificultad)) return 3;
        if ("intermedio".equalsIgnoreCase(dificultad)) return 2;
        return 1;
    }

    public int puntajeMaximo(List<PreguntaQuiz> preguntasDelReto) {
        return preguntasDelReto.stream().mapToInt(p -> valor(p.dificultad())).sum();
    }

    /**
     * Puntaje de una entrega.
     *
     * @param preguntasEnOrden preguntas del reto en su orden
     * @param elegidas         letra elegida por pregunta (puede faltar)
     * @param correctas        letra correcta por pregunta
     */
    public int calificar(List<PreguntaQuiz> preguntasEnOrden, Map<Long, String> elegidas, Map<Long, String> correctas) {
        int puntaje = 0;
        for (PreguntaQuiz p : preguntasEnOrden) {
            String elegida = elegidas.get(p.id());
            boolean acerto = elegida != null && elegida.equalsIgnoreCase(correctas.get(p.id()));
            if (acerto) {
                puntaje += valor(p.dificultad());
            } else if (this == MUERTE_SUBITA) {
                break; // a muerte: el primer error termina la racha
            }
        }
        return puntaje;
    }
}
