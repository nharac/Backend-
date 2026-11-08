package co.edu.ucundinamarca.saesu.gamificacion.ports.output;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse;

import java.util.List;

/** Puerto de salida: saldo e historial de Sumi Monedas (usuarios.monedas y monedas_historial, V25). */
public interface MonedasPort {

    /** Suma monedas al saldo y deja el movimiento en el historial. */
    void sumar(Long usuarioId, int cantidad, String motivo);

    /**
     * Descuenta monedas solo si alcanza el saldo (en una sola sentencia, así dos compras
     * al mismo tiempo no pueden dejar el saldo en negativo).
     * @return false si no alcanzaba
     */
    boolean gastar(Long usuarioId, int cantidad, String motivo);

    int saldo(Long usuarioId);

    /** Últimos movimientos, del más reciente al más antiguo. */
    List<MovimientoMonedaResponse> historial(Long usuarioId, int limite);
}
