package co.edu.ucundinamarca.saesu.gamificacion.application.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Estado del vestidor de Sumi de quien consulta.
 * @param monedas    saldo de Sumi Monedas
 * @param docente    los docentes tienen todo el vestidor gratis
 * @param inventario prendas compradas como "categoria:prenda"
 * @param estilo     lo que Sumi lleva puesto (categoría → prenda)
 */
public record VestidorResponse(int monedas, boolean docente, List<String> inventario, Map<String, String> estilo) {
}
