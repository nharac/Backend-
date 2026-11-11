package co.edu.ucundinamarca.saesu.gamificacion.domain.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Partes del vestidor de Sumi y lo que lleva si nunca se ha vestido.
 * Es la única lista de partes del backend: el servicio la usa para validar y el adaptador
 * para las columnas de sumi_estilo. Agregar una parte = agregarla aquí (y su columna en una migración).
 */
public enum ParteSumi {
    SOMBRERO("sombrero", "ninguno"),
    CARA("cara", "ninguno"),
    ROPA("ropa", "ninguno"),
    OBJETO("objeto", "ninguno"),
    ESPALDA("espalda", "ninguno"),
    BUFANDA("bufanda", "saesu"),
    FONDO("fondo", "ninguno");

    private final String id;
    private final String porDefecto;

    ParteSumi(String id, String porDefecto) {
        this.id = id;
        this.porDefecto = porDefecto;
    }

    public String id() {
        return id;
    }

    public String porDefecto() {
        return porDefecto;
    }

    /** Ids en orden (también son los nombres de las columnas de sumi_estilo). */
    public static List<String> ids() {
        return Arrays.stream(values()).map(ParteSumi::id).toList();
    }

    /** Sumi clásica: cada parte con su valor por defecto, en orden. */
    public static Map<String, String> estiloClasico() {
        Map<String, String> m = new LinkedHashMap<>();
        for (ParteSumi p : values()) {
            m.put(p.id, p.porDefecto);
        }
        return m;
    }
}
