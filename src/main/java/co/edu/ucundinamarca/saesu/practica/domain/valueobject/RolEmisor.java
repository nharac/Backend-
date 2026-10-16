package co.edu.ucundinamarca.saesu.practica.domain.valueobject;

import lombok.Getter;

/**
 * Rol de quien envía un mensaje en el chat.
 * - USUARIO: mensaje enviado por el estudiante.
 * - IA: respuesta generada por el modelo de IA.
 */
@Getter
public enum RolEmisor {
    USUARIO("usuario"),
    IA("ia");

    private final String valor;

    RolEmisor(String valor) {
        this.valor = valor;
    }

    public static RolEmisor desdeValor(String valor) {
        for (RolEmisor rol : values()) {
            if (rol.valor.equalsIgnoreCase(valor)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("RolEmisor inválido: " + valor);
    }
}