package co.edu.ucundinamarca.saesu.practica.domain.valueobject;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Tema al que pertenece una pregunta de práctica, en el orden en que se aprenden:
 * primero las bases (fundamentos, álgebra y trigonometría) y luego el cálculo.
 */

@Getter
public enum Tema {
    FUNDAMENTOS("fundamentos", "Fundamentos"),
    ALGEBRA("algebra", "Álgebra"),
    TRIGONOMETRIA("trigonometria", "Trigonometría"),
    LIMITES("limites", "Límites"),
    DERIVADAS("derivadas", "Derivadas"),
    INTEGRALES("integrales", "Integrales");

    /** Expresión regular con todos los temas válidos, para validar peticiones. */
    public static final String PATRON = "fundamentos|algebra|trigonometria|limites|derivadas|integrales";

    private final String valor;
    private final String nombre;

    Tema(String valor, String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }

    /** Valores de todos los temas, en orden de aprendizaje. */
    public static List<String> valores() {
        return Arrays.stream(values()).map(Tema::getValor).toList();
    }

    /** Nombre para mostrar de un tema (o el mismo valor si no se reconoce). */
    public static String nombreDe(String valor) {
        return Arrays.stream(values()).filter(t -> t.valor.equals(valor)).map(Tema::getNombre)
                .findFirst().orElse(valor);
    }

    /**
     * Convierte un string a Tema. Útil para parsear query params.
     */
    public static Tema desdeValor(String valor) {
        for (Tema tema : values()) {
            if (tema.valor.equalsIgnoreCase(valor)) {
                return tema;
            }
        }
        throw new IllegalArgumentException("Tema inválido: " + valor);
    }
}