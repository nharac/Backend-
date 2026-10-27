package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.usuario.application.dto.request.ActualizarPerfilRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.ContrasenaIncorrectaException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** PUT /api/auth/perfil: cambiar nombre y contraseña. Usa BCrypt real (sin base de datos). */
class ActualizarPerfilServiceTest {

    private static final String CORREO = "juan@ucundinamarca.edu.co";
    private static final String CLAVE = "Saesu2026!";

    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4); // costo bajo = prueba rápida
    private UsuarioRepositoryPort repo;
    private ActualizarPerfilService servicio;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        repo = mock(UsuarioRepositoryPort.class);
        servicio = new ActualizarPerfilService(repo, encoder);
        usuario = new Usuario(5L, "Juan", CORREO, encoder.encode(CLAVE), "ESTUDIANTE", LocalDateTime.now(), null);
        when(repo.buscarPorCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(repo.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static ActualizarPerfilRequest pedido(String nombre, String actual, String nueva) {
        ActualizarPerfilRequest r = new ActualizarPerfilRequest();
        r.setNombre(nombre);
        r.setPasswordActual(actual);
        r.setPasswordNueva(nueva);
        return r;
    }

    @Test
    @DisplayName("Cambia el nombre (quitando espacios) sin tocar la contraseña")
    void cambiaNombre() {
        String hashAntes = usuario.getPassword();

        UsuarioResponse r = servicio.actualizarPerfil(CORREO, pedido("  Juan Lozano  ", null, null));

        assertThat(r.getNombre()).isEqualTo("Juan Lozano");
        assertThat(usuario.getPassword()).isEqualTo(hashAntes);
    }

    @Test
    @DisplayName("Con la contraseña actual correcta se guarda la nueva cifrada")
    void cambiaContrasena() {
        servicio.actualizarPerfil(CORREO, pedido("Juan", CLAVE, "NuevaClave1!"));

        assertThat(encoder.matches("NuevaClave1!", usuario.getPassword())).isTrue();
        assertThat(usuario.getPassword()).doesNotContain("NuevaClave1!");
    }

    @Test
    @DisplayName("Con la contraseña actual incorrecta se rechaza y no se guarda")
    void contrasenaActualIncorrecta() {
        assertThatThrownBy(() -> servicio.actualizarPerfil(CORREO, pedido("Juan", "Mala1234!", "NuevaClave1!")))
                .isInstanceOf(ContrasenaIncorrectaException.class)
                .hasMessageContaining("no es correcta");
        verify(repo, never()).guardar(any());
    }

    @Test
    @DisplayName("Pedir una nueva sin dar la actual se rechaza")
    void faltaContrasenaActual() {
        assertThatThrownBy(() -> servicio.actualizarPerfil(CORREO, pedido("Juan", null, "NuevaClave1!")))
                .isInstanceOf(ContrasenaIncorrectaException.class);
    }

    @Test
    @DisplayName("La nueva contraseña no puede ser igual a la actual")
    void nuevaIgualALaActual() {
        assertThatThrownBy(() -> servicio.actualizarPerfil(CORREO, pedido("Juan", CLAVE, CLAVE)))
                .isInstanceOf(ContrasenaIncorrectaException.class)
                .hasMessageContaining("diferente");
    }
}
