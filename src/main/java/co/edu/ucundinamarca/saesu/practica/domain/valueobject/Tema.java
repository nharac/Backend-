package co.edu.ucundinamarca.saesu.practica.domain.valueobject;

import lombok.Getter;

/**
 * Tema al que pertenece una pregunta de práctica.
 */

@Getter
public enum Tema {
    LIMITES("limites"),
    DERIVADAS("derivadas"),
    INTEGRALES("integrales");

    private final String valor;

    Tema(String valor) {
        this.valor = valor;
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