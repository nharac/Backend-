package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.request.CompraRequest;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.MovimientoMonedaResponse;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.VestidorResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.exception.CompraNoValidaException;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.MonedasPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.VestidorPort.UsuarioBasico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Vestidor de Sumi: compras con Sumi Monedas y estilo guardado en el servidor. */
@ExtendWith(MockitoExtension.class)
class VestidorServiceTest {

    private static final String ANA = "ana@ucundinamarca.edu.co";
    private static final String PROFE = "profe@ucundinamarca.edu.co";

    @Mock private VestidorPort vestidor;
    @Mock private MonedasPort monedas;
    @InjectMocks private VestidorService servicio;

    @BeforeEach
    void preparar() {
        lenient().when(vestidor.usuario(ANA)).thenReturn(new UsuarioBasico(4L, "ESTUDIANTE"));
        lenient().when(vestidor.usuario(PROFE)).thenReturn(new UsuarioBasico(9L, "DOCENTE"));
        lenient().when(vestidor.inventario(anyLong())).thenReturn(Set.of("sombrero:gorra"));
        lenient().when(vestidor.estilo(anyLong())).thenReturn(Map.of());
        lenient().when(monedas.saldo(4L)).thenReturn(50);
        // Catálogo: lo "ninguno" y la bufanda SAESU son gratis
        lenient().when(vestidor.precio(anyString(), anyString())).thenAnswer(inv -> switch ((String) inv.getArgument(1)) {
            case "ninguno", "saesu", "cafe" -> 0;
            case "gorra" -> 20;
            case "corona" -> 300;
            case "playa" -> 50;
            default -> null;
        });
    }

    @Test
    @DisplayName("Estado: saldo, colección y estilo por defecto")
    void estado() {
        VestidorResponse r = servicio.estado(ANA);

        assertThat(r.monedas()).isEqualTo(50);
        assertThat(r.docente()).isFalse();
        assertThat(r.inventario()).containsExactly("sombrero:gorra");
        assertThat(r.estilo()).containsEntry("bufanda", "saesu").containsEntry("fondo", "ninguno").containsEntry("ropa", "ninguno").hasSize(7);
    }

    @Test
    @DisplayName("Comprar agrega la prenda y descuenta las monedas")
    void comprar() {
        when(vestidor.agregar(4L, "fondo", "playa")).thenReturn(true);
        when(monedas.gastar(4L, 50, "Compra en el vestidor: fondo playa")).thenReturn(true);

        servicio.comprar(ANA, new CompraRequest("fondo", "playa"));

        verify(monedas).gastar(4L, 50, "Compra en el vestidor: fondo playa");
    }

    @Test
    @DisplayName("No se puede comprar dos veces lo mismo")
    void yaLaTiene() {
        when(vestidor.agregar(4L, "sombrero", "gorra")).thenReturn(false);

        assertThatThrownBy(() -> servicio.comprar(ANA, new CompraRequest("sombrero", "gorra")))
                .isInstanceOf(CompraNoValidaException.class).hasMessageContaining("Ya tienes");
        verify(monedas, never()).gastar(anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Si no alcanzan las monedas, dice cuántas faltan")
    void noAlcanza() {
        when(vestidor.agregar(4L, "sombrero", "corona")).thenReturn(true);
        when(monedas.gastar(eq(4L), eq(300), anyString())).thenReturn(false);

        assertThatThrownBy(() -> servicio.comprar(ANA, new CompraRequest("sombrero", "corona")))
                .isInstanceOf(CompraNoValidaException.class).hasMessage("Te faltan 250 Sumi Monedas para comprarla");

        when(monedas.saldo(4L)).thenReturn(299);
        assertThatThrownBy(() -> servicio.comprar(ANA, new CompraRequest("sombrero", "corona")))
                .hasMessage("Te falta 1 Sumi Moneda para comprarla");
    }

    @Test
    @DisplayName("Lo gratis y todo lo de los docentes no cuesta monedas")
    void gratis() {
        servicio.comprar(ANA, new CompraRequest("objeto", "cafe"));
        servicio.comprar(PROFE, new CompraRequest("sombrero", "corona"));

        verify(vestidor, never()).agregar(anyLong(), anyString(), anyString());
        verify(monedas, never()).gastar(anyLong(), anyInt(), anyString());
        assertThat(servicio.estado(PROFE).docente()).isTrue();
    }

    @Test
    @DisplayName("Prendas o usuarios que no existen")
    void noExiste() {
        assertThatThrownBy(() -> servicio.comprar(ANA, new CompraRequest("sombrero", "capa-invisible")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> servicio.estado("nadie@u.edu.co")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Vestir: se guarda lo gratis o comprado y se completa lo que falte")
    void vestir() {
        Map<String, String> pedido = new HashMap<>(Map.of("sombrero", "gorra", "objeto", "cafe"));

        servicio.vestir(ANA, pedido);

        verify(vestidor).guardarEstilo(4L, Map.of("sombrero", "gorra", "cara", "ninguno", "ropa", "ninguno", "objeto", "cafe",
                "espalda", "ninguno", "bufanda", "saesu", "fondo", "ninguno"));
    }

    @Test
    @DisplayName("Vestir con algo no comprado no se permite (salvo a docentes)")
    void vestirSinComprar() {
        assertThatThrownBy(() -> servicio.vestir(ANA, Map.of("sombrero", "corona")))
                .isInstanceOf(CompraNoValidaException.class);
        verify(vestidor, never()).guardarEstilo(anyLong(), anyMap());

        servicio.vestir(PROFE, Map.of("sombrero", "corona", "fondo", "playa"));
        verify(vestidor).guardarEstilo(eq(9L), any());
    }

    @Test
    @DisplayName("Historial de monedas")
    void historial() {
        List<MovimientoMonedaResponse> movs = List.of(new MovimientoMonedaResponse(3, "Respondió en el foro", LocalDateTime.now()));
        when(monedas.historial(4L, 30)).thenReturn(movs);

        assertThat(servicio.historial(ANA)).isEqualTo(movs);
    }
}
