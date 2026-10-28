package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RestablecerPasswordRequest;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.CodigoRecuperacionPort.CodigoActivo;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Recuperación de contraseña con código de 6 dígitos por correo. */
@ExtendWith(MockitoExtension.class)
class RecuperarPasswordServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 26, 12, 0);
    private static final String CORREO = "laura@ucundinamarca.edu.co";

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private CodigoRecuperacionPort codigoPort;
    @Mock private EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4); // costo bajo: pruebas rápidas
    private RecuperarPasswordService servicio;

    private final Usuario laura = new Usuario(7L, "Laura Gómez", CORREO, "hash", "ESTUDIANTE", AHORA.minusMonths(1), null);

    @BeforeEach
    void preparar() {
        servicio = new RecuperarPasswordService(usuarioRepository, codigoPort, encoder, emailService,
                Clock.fixed(AHORA.atZone(BOGOTA).toInstant(), BOGOTA));
    }

    private RestablecerPasswordRequest pedido(String codigo) {
        return new RestablecerPasswordRequest(CORREO, codigo, "NuevaClave#2026");
    }

    @Test
    @DisplayName("Solicitar: guarda el código cifrado y lo envía por correo")
    void solicitarEnvia() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.fechaUltimoCodigo(7L)).thenReturn(Optional.empty());

        servicio.solicitar("  " + CORREO + " ");

        ArgumentCaptor<String> hash = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codigo = ArgumentCaptor.forClass(String.class);
        verify(codigoPort).invalidarActivos(7L);
        verify(codigoPort).guardar(eq(7L), hash.capture(), eq(15));
        verify(emailService).enviarCodigoRecuperacion(eq(CORREO), eq("Laura Gómez"), codigo.capture(), eq(15));

        assertThat(codigo.getValue()).matches("\\d{6}");
        assertThat(hash.getValue()).isNotEqualTo(codigo.getValue());       // nunca se guarda en texto plano
        assertThat(encoder.matches(codigo.getValue(), hash.getValue())).isTrue();
    }

    @Test
    @DisplayName("Solicitar con un correo que no existe: no hace nada y no revela que no existe")
    void solicitarCorreoInexistente() {
        when(usuarioRepository.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        servicio.solicitar("nadie@ucundinamarca.edu.co");
        verifyNoInteractions(codigoPort, emailService);
    }

    @Test
    @DisplayName("No reenvía otro código si pidió uno hace menos de un minuto")
    void solicitarEnRafaga() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.fechaUltimoCodigo(7L)).thenReturn(Optional.of(AHORA.minusSeconds(20)));

        servicio.solicitar(CORREO);

        verify(codigoPort, never()).guardar(anyLong(), anyString(), anyInt());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Si falla Gmail, la solicitud no lanza error (el código ya quedó guardado)")
    void solicitarCorreoFalla() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.fechaUltimoCodigo(7L)).thenReturn(Optional.of(AHORA.minusMinutes(5)));
        org.mockito.Mockito.doThrow(new RuntimeException("SMTP caído"))
                .when(emailService).enviarCodigoRecuperacion(anyString(), anyString(), anyString(), anyInt());

        servicio.solicitar(CORREO);

        verify(codigoPort).guardar(eq(7L), anyString(), eq(15));
    }

    @Test
    @DisplayName("Restablecer con el código correcto: cambia la contraseña, gasta el código y avisa por correo")
    void restablecerOk() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.of(new CodigoActivo(30L, encoder.encode("482917"), 0)));

        servicio.restablecer(pedido("482917"));

        ArgumentCaptor<String> nuevaHash = ArgumentCaptor.forClass(String.class);
        verify(codigoPort).actualizarPassword(eq(7L), nuevaHash.capture());
        assertThat(encoder.matches("NuevaClave#2026", nuevaHash.getValue())).isTrue();
        verify(codigoPort).marcarUsado(30L);
        verify(codigoPort).invalidarActivos(7L);
        verify(emailService).enviarAvisoCambioPassword(CORREO, "Laura Gómez");
    }

    @Test
    @DisplayName("Código incorrecto: suma un intento y dice cuántos quedan")
    void restablecerCodigoIncorrecto() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.of(new CodigoActivo(30L, encoder.encode("482917"), 2)));

        assertThatThrownBy(() -> servicio.restablecer(pedido("111111")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código incorrecto. Te quedan 2 intentos.");
        verify(codigoPort).sumarIntento(30L);
        verify(codigoPort, never()).actualizarPassword(anyLong(), anyString());
    }

    @Test
    @DisplayName("Al quinto fallo pide solicitar un código nuevo")
    void restablecerUltimoIntento() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.of(new CodigoActivo(30L, encoder.encode("482917"), 4)));

        assertThatThrownBy(() -> servicio.restablecer(pedido("000000")))
                .hasMessage("Demasiados intentos fallidos. Solicita un código nuevo.");
    }

    @Test
    @DisplayName("Sin código vigente (vencido, usado o bloqueado) o correo inexistente: el mismo mensaje")
    void restablecerSinCodigo() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.restablecer(pedido("482917")))
                .hasMessage(RecuperarPasswordService.MENSAJE_CODIGO_INVALIDO);

        when(usuarioRepository.buscarPorCorreo("otro@ucundinamarca.edu.co")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> servicio.restablecer(
                new RestablecerPasswordRequest("otro@ucundinamarca.edu.co", "482917", "NuevaClave#2026")))
                .hasMessage(RecuperarPasswordService.MENSAJE_CODIGO_INVALIDO);
    }

    @Test
    @DisplayName("El código siempre tiene 6 dígitos (con ceros a la izquierda)")
    void formatoCodigo() {
        for (int i = 0; i < 200; i++) {
            assertThat(RecuperarPasswordService.generarCodigo()).matches("\\d{6}");
        }
    }

    @Test
    @DisplayName("Validar código: con el correcto no lo gasta ni cambia la contraseña")
    void validarCodigoCorrecto() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.of(new CodigoActivo(3L, encoder.encode("123456"), 0)));

        servicio.validarCodigo(CORREO, "123456");

        verify(codigoPort, never()).marcarUsado(anyLong());
        verify(codigoPort, never()).actualizarPassword(anyLong(), anyString());
        verify(codigoPort, never()).sumarIntento(anyLong());
    }

    @Test
    @DisplayName("Validar código: uno incorrecto cuenta como intento fallido")
    void validarCodigoIncorrecto() {
        when(usuarioRepository.buscarPorCorreo(CORREO)).thenReturn(Optional.of(laura));
        when(codigoPort.buscarActivo(7L, 5)).thenReturn(Optional.of(new CodigoActivo(3L, encoder.encode("123456"), 2)));

        assertThatThrownBy(() -> servicio.validarCodigo(CORREO, "000000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Código incorrecto. Te quedan 2 intentos.");
        verify(codigoPort).sumarIntento(3L);
    }
}
