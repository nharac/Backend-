package co.edu.ucundinamarca.saesu.gamificacion.ports.output;

import java.util.Map;
import java.util.Set;

/** Puerto de salida: catálogo, compras y estilo del vestidor de Sumi (tablas sumi_*, V25). */
public interface VestidorPort {

    /** Id y rol del usuario por su correo. */
    record UsuarioBasico(Long id, String rol) {
        public boolean esDocente() {
            return rol != null && rol.replace("ROLE_", "").equalsIgnoreCase("DOCENTE");
        }
    }

    UsuarioBasico usuario(String correo);

    /** Precio en Sumi Monedas, o null si la prenda no existe en el catálogo. */
    Integer precio(String categoria, String prenda);

    /** Prendas compradas como "categoria:prenda". */
    Set<String> inventario(Long usuarioId);

    /** @return false si ya la tenía */
    boolean agregar(Long usuarioId, String categoria, String prenda);

    /** Lo que Sumi lleva puesto (categoría → prenda); vacío si nunca la ha vestido. */
    Map<String, String> estilo(Long usuarioId);

    void guardarEstilo(Long usuarioId, Map<String, String> estilo);
}
