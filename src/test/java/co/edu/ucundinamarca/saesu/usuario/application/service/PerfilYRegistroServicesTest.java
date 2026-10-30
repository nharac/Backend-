package co.edu.ucundinamarca.saesu.usuario.application.service;

import co.edu.ucundinamarca.saesu.core.storage.StorageService;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.usuario.application.dto.request.RegistroRequest;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.RegistroResponse;
import co.edu.ucundinamarca.saesu.usuario.application.dto.response.UsuarioResponse;
import co.edu.ucundinamarca.saesu.usuario.domain.exception.UsuarioNoEncontradoException;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Casos de uso pequeños del módulo usuario: registro, perfil y foto. */
@ExtendWith(MockitoExtension.class)
class PerfilYRegistroServicesTest {

    @Mock private UsuarioRepositoryPort usuarios;
    @Mock private LogrosPort logros;
    @Mock private StorageService storage;
    @Mock private VerificarCorreoService verificarCorreo;

    private Usuario laura() {
        return new Usuario(1L, "Laura Gómez", "laura@ucundinamarca.edu.co", "hash", "ESTUDIANTE", LocalDateTime.of(2026, 10, 1, 8, 0), "https://s3/foto.png");
    }

    @Nested
    class Registro {
        @Test
        @DisplayName("Registrar solo pide el código: responde el correo limpio y los 15 minutos de vigencia")
        void registrarPideCodigo() {
            RegistroRequest req = new RegistroRequest("Laura Gómez", "  laura@ucundinamarca.edu.co ", "Clave#2026");

            RegistroResponse r = new RegistrarUsuarioService(verificarCorreo).registrar(req);

            verify(verificarCorreo).solicitar(req);
            assertThat(r.correo()).isEqualTo("laura@ucundinamarca.edu.co");
            assertThat(r.minutosVigencia()).isEqualTo(VerificarCorreoService.MINUTOS_VIGENCIA);
            assertThat(r.mensaje()).contains("código de 6 dígitos");
        }
    }

    @Nested
    class Perfil {
        @Test
        @DisplayName("El perfil incluye foto, racha y puntos totales")
        void perfilCompleto() {
            when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura()));
            when(logros.rachaYPuntos(1L)).thenReturn(new long[]{4, 195});

            UsuarioResponse r = new ObtenerPerfilService(usuarios, logros).obtenerPerfil("laura@ucundinamarca.edu.co");

            assertThat(r.getNombre()).isEqualTo("Laura Gómez");
            assertThat(r.getFotoPerfilUrl()).isEqualTo("https://s3/foto.png");
            assertThat(r.getRachaActual()).isEqualTo(4);
            assertThat(r.getPuntosTotales()).isEqualTo(195L);
        }

        @Test
        @DisplayName("Usuario inexistente: 404")
        void perfilSinUsuario() {
            when(usuarios.buscarPorCorreo("x@ucundinamarca.edu.co")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> new ObtenerPerfilService(usuarios, logros).obtenerPerfil("x@ucundinamarca.edu.co"))
                    .isInstanceOf(UsuarioNoEncontradoException.class);
        }
    }

    @Nested
    class Foto {
        @Test
        @DisplayName("Sube la imagen a S3 y guarda la URL en el usuario")
        void subeFoto() throws Exception {
            Usuario u = laura();
            MockMultipartFile img = new MockMultipartFile("archivo", "yo.png", "image/png", new byte[]{1, 2, 3});
            when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(u));
            when(storage.subirArchivo(img)).thenReturn("https://s3/nueva.png");

            String url = new SubirFotoPerfilService(usuarios, storage).ejecutar("laura@ucundinamarca.edu.co", img);

            assertThat(url).isEqualTo("https://s3/nueva.png");
            assertThat(u.getFotoPerfilUrl()).isEqualTo("https://s3/nueva.png");
            verify(usuarios).guardar(u);
        }

        @Test
        @DisplayName("Rechaza archivos que no son imagen sin subir nada")
        void rechazaNoImagen() throws Exception {
            MockMultipartFile pdf = new MockMultipartFile("archivo", "tarea.pdf", "application/pdf", new byte[]{1});

            assertThatThrownBy(() -> new SubirFotoPerfilService(usuarios, storage).ejecutar("laura@ucundinamarca.edu.co", pdf))
                    .hasMessageContaining("imagen válida");
            verify(storage, never()).subirArchivo(any());
        }

        @Test
        @DisplayName("Rechaza archivos vacíos")
        void rechazaVacio() {
            MockMultipartFile vacio = new MockMultipartFile("archivo", "yo.png", "image/png", new byte[0]);

            assertThatThrownBy(() -> new SubirFotoPerfilService(usuarios, storage).ejecutar("laura@ucundinamarca.edu.co", vacio))
                    .hasMessageContaining("imagen válida");
        }
    }
}
