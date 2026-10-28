package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.VerificarCorreoRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CorreoYaRegistradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort;
import co.edu.ucundinamarca.saesu.usuario.ports.output.RegistroPendientePort.Pendiente;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificarCorreoServiceTest {

    private static final String CORREO = "ana@ucundinamarca.edu.co";

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private RegistroPendientePort pendientes;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private VerificarCorreoService service;

    @BeforeEach
    void setUp() {
        service = new VerificarCorreoService(usuarioRepository, pendientes, encoder, jwtService, emailService);
    }

    private Pendiente pendiente(String codigo, int intentos, boolean vigente, long segundos) {
        return new Pendiente(9L, CORREO, "Ana Ruiz", encoder.encode("Clave#2026x"), encoder.encode(codigo),
                intentos, vigente, segundos);
    }

    @Test
    @DisplayName("Registrarse NO crea la cuenta: guarda el registro pendiente y envía el código")
    void solicitarNoCreaCuenta() {
        when(usuarioRepository.existePorCorreo(CORREO)).thenReturn(false);
        when(pendientes.buscar(CORREO)).thenReturn(Optional.empty());

        service.solicitar(new RegistroRequest("Ana Ruiz", CORREO, "Clave#2026x"));

        verify(pendientes).guardar(eq(CORREO), eq("Ana Ruiz"), argThat(h -> encoder.matches("Clave#2026x", h)),
                argThat(h -> h.startsWith("$2")), eq(15));
        verify(emailService).enviarCodigoVerificacion(eq(CORREO), eq("Ana Ruiz"), matches("\\d{6}"), eq(15));
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Si el correo ya tiene cuenta, el registro responde 409 y no envía nada")
    void solicitarCorreoExistente() {
        when(usuarioRepository.existePorCorreo(CORREO)).thenReturn(true);

        assertThatThrownBy(() -> service.solicitar(new RegistroRequest("Ana Ruiz", CORREO, "Clave#2026x")))
                .isInstanceOf(CorreoYaRegistradoException.class);
        verifyNoInteractions(pendientes, emailService);
    }

    @Test
    @DisplayName("Registrarse dos veces en menos de un minuto no cambia ni reenvía el código")
    void solicitarEnRafaga() {
        when(usuarioRepository.existePorCorreo(CORREO)).thenReturn(false);
        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 0, true, 20)));

        service.solicitar(new RegistroRequest("Ana Ruiz", CORREO, "Clave#2026x"));

        verify(pendientes, never()).guardar(any(), any(), any(), any(), anyInt());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Con el código correcto se crea la cuenta, se borra el pendiente y se abre la sesión")
    void verificarCreaCuenta() {
        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 0, true, 30)));
        when(usuarioRepository.existePorCorreo(CORREO)).thenReturn(false);
        when(usuarioRepository.guardar(any())).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(50L);
            return u;
        });
        when(jwtService.generarToken(CORREO, "ESTUDIANTE")).thenReturn("jwt");

        AuthResponse r = service.verificar(new VerificarCorreoRequest(CORREO, "123456"));

        ArgumentCaptor<Usuario> creado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).guardar(creado.capture());
        assertThat(creado.getValue().getRol()).isEqualTo("ESTUDIANTE");
        assertThat(encoder.matches("Clave#2026x", creado.getValue().getPassword())).isTrue();
        assertThat(r.getToken()).isEqualTo("jwt");
        verify(pendientes).eliminar(9L);
        verify(emailService).enviarCorreoBienvenida(CORREO, "Ana Ruiz");
    }

    @Test
    @DisplayName("Con un código incorrecto no se crea la cuenta y dice cuántos intentos quedan")
    void verificarIncorrecto() {
        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 1, true, 30)));

        assertThatThrownBy(() -> service.verificar(new VerificarCorreoRequest(CORREO, "999999")))
                .hasMessage("Código incorrecto. Te quedan 3 intentos.");
        verify(pendientes).sumarIntento(9L);
        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Un código vencido o con 5 intentos fallidos no crea la cuenta")
    void verificarVencidoOBloqueado() {
        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 0, false, 1000)));
        assertThatThrownBy(() -> service.verificar(new VerificarCorreoRequest(CORREO, "123456")))
                .hasMessageContaining("venció");

        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 5, true, 30)));
        assertThatThrownBy(() -> service.verificar(new VerificarCorreoRequest(CORREO, "123456")))
                .hasMessageContaining("Demasiados intentos");

        verify(usuarioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Reenviar cambia el código solo si pasó un minuto desde el último")
    void reenviar() {
        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 2, true, 30)));
        service.reenviar(CORREO);
        verifyNoInteractions(emailService);

        when(pendientes.buscar(CORREO)).thenReturn(Optional.of(pendiente("123456", 2, true, 90)));
        service.reenviar(CORREO);
        verify(pendientes).renovarCodigo(eq(9L), anyString(), eq(15));
        verify(emailService).enviarCodigoVerificacion(eq(CORREO), eq("Ana Ruiz"), matches("\\d{6}"), eq(15));
    }
}
