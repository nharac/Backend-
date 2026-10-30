package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.LoginRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.AuthResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.CredencialesInvalidasException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock private UsuarioRepositoryPort usuarios;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtService jwt;

    private LoginService service;
    private Usuario laura;

    @BeforeEach
    void setUp() {
        service = new LoginService(usuarios, encoder, jwt);
        laura = new Usuario(1L, "Laura Gómez", "laura@ucundinamarca.edu.co", "hash", "ESTUDIANTE", LocalDateTime.of(2026, 10, 1, 8, 0), null);
    }

    @Test
    @DisplayName("Credenciales correctas: devuelve el usuario, el JWT y el refresh token")
    void loginCorrecto() {
        when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura));
        when(encoder.matches("Clave#2026", "hash")).thenReturn(true);
        when(jwt.generarToken("laura@ucundinamarca.edu.co", "ESTUDIANTE")).thenReturn("jwt");
        when(jwt.generarRefreshToken("laura@ucundinamarca.edu.co")).thenReturn("refresh");

        AuthResponse r = service.login(new LoginRequest("laura@ucundinamarca.edu.co", "Clave#2026"));

        assertThat(r.getToken()).isEqualTo("jwt");
        assertThat(r.getRefreshToken()).isEqualTo("refresh");
        assertThat(r.getUsuario().getNombre()).isEqualTo("Laura Gómez");
        assertThat(r.getUsuario().getRol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    @DisplayName("Correo no registrado: mensaje genérico y sin generar token")
    void correoNoRegistrado() {
        when(usuarios.buscarPorCorreo("nadie@ucundinamarca.edu.co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("nadie@ucundinamarca.edu.co", "x")))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Credenciales inválidas");
        verify(jwt, never()).generarToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Contraseña incorrecta: el mismo mensaje genérico, para no revelar si el correo existe")
    void contrasenaIncorrecta() {
        when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura));
        when(encoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("laura@ucundinamarca.edu.co", "mala")))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage("Credenciales inválidas");
        verify(jwt, never()).generarToken(anyString(), anyString());
    }
}
