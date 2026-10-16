package co.edu.ucundinamarca.saesu.practica.domain.valueobject;

import lombok.Getter;

/**
 * Dificultad de una pregunta de práctica.
 */
@Getter
public enum Dificultad {
    BASICO("basico"),
    INTERMEDIO("intermedio"),
    AVANZADO("avanzado");

    private final String valor;

    Dificultad(String valor) {
        this.valor = valor;
    }

    public static Dificultad desdeValor(String valor) {
        for (Dificultad d : values()) {
            if (d.valor.equalsIgnoreCase(valor)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Dificultad inválida: " + valor);
    }
}