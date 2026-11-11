package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.request.CompraRequest;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.VestidorResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.exception.CompraNoValidaException;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.ParteSumi;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.MonedasPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort.UsuarioBasico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vestidor de Sumi: las prendas se compran con Sumi Monedas y lo que Sumi lleva puesto
 * se guarda en el servidor. Las prendas de precio 0 son de todos y los docentes tienen todo gratis.
 */
@Service
public class VestidorService {

    /** Partes del vestidor y lo que lleva Sumi si nunca se ha vestido (fuente única: ParteSumi). */
    static final Map<String, String> POR_DEFECTO = Map.copyOf(ParteSumi.estiloClasico());
    private static final List<String> PARTES = ParteSumi.ids();
    private static final int MOVIMIENTOS = 30;

    private final VestidorPort vestidor;
    private final MonedasPort monedas;

    public VestidorService(VestidorPort vestidor, MonedasPort monedas) {
        this.vestidor = vestidor;
        this.monedas = monedas;
    }

    public VestidorResponse estado(String correo) {
        return estado(usuario(correo));
    }

    /**
     * Compra una prenda: primero la agrega al inventario y luego descuenta las monedas;
     * si no alcanzan, la transacción se deshace y no queda nada a medias.
     */
    @Transactional
    public VestidorResponse comprar(String correo, CompraRequest compra) {
        UsuarioBasico u = usuario(correo);
        int precio = precioDe(compra.categoria(), compra.prenda());
        if (u.esDocente() || precio == 0) {
            return estado(u);
        }
        if (!vestidor.agregar(u.id(), compra.categoria(), compra.prenda())) {
            throw new CompraNoValidaException("Ya tienes esta prenda en tu colección");
        }
        if (!monedas.gastar(u.id(), precio, "Compra en el vestidor: " + compra.categoria() + " " + compra.prenda())) {
            int faltan = precio - monedas.saldo(u.id());
            throw new CompraNoValidaException("Te falta" + (faltan == 1 ? "" : "n") + " " + faltan
                    + " Sumi Moneda" + (faltan == 1 ? "" : "s") + " para comprarla");
        }
        return estado(u);
    }

    /** Guarda lo que Sumi lleva puesto: solo prendas gratis o compradas (los docentes, cualquiera). */
    @Transactional
    public VestidorResponse vestir(String correo, Map<String, String> pedido) {
        UsuarioBasico u = usuario(correo);
        Set<String> inventario = vestidor.inventario(u.id());
        Map<String, String> estilo = new LinkedHashMap<>();
        for (String parte : PARTES) {
            String prenda = pedido.getOrDefault(parte, POR_DEFECTO.get(parte));
            int precio = precioDe(parte, prenda);
            if (precio > 0 && !u.esDocente() && !inventario.contains(parte + ":" + prenda)) {
                throw new CompraNoValidaException("Primero tienes que comprar esa prenda");
            }
            estilo.put(parte, prenda);
        }
        vestidor.guardarEstilo(u.id(), estilo);
        return estado(u);
    }

    public List<MovimientoMonedaResponse> historial(String correo) {
        return monedas.historial(usuario(correo).id(), MOVIMIENTOS);
    }

    private VestidorResponse estado(UsuarioBasico u) {
        Map<String, String> estilo = new LinkedHashMap<>(POR_DEFECTO);
        estilo.putAll(vestidor.estilo(u.id()));
        List<String> inventario = vestidor.inventario(u.id()).stream().sorted().toList();
        return new VestidorResponse(monedas.saldo(u.id()), u.esDocente(), inventario, estilo);
    }

    private UsuarioBasico usuario(String correo) {
        UsuarioBasico u = vestidor.usuario(correo);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return u;
    }

    private int precioDe(String categoria, String prenda) {
        Integer precio = vestidor.precio(categoria, prenda);
        if (precio == null) {
            throw new IllegalArgumentException("Esa prenda no existe en el vestidor");
        }
        return precio;
    }
}
