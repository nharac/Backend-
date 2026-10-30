package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.core.notificacion.EmailService;
import co.edu.ucundinamarca.saesu.core.storage.StorageService;
import co.edu.ucundinamarca.saesu.foro.application.dto.request.CrearPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.request.ResponderPublicacionRequest;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.exception.RespuestaPadreInvalidaException;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.gamificacion.application.service.PuntosService;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Casos de uso del foro: publicar, responder (un nivel) y eliminar. */
@ExtendWith(MockitoExtension.class)
class PublicarResponderEliminarTest {

    @Mock private PublicacionRepositoryPort publicaciones;
    @Mock private RespuestaRepositoryPort respuestas;
    @Mock private UsuarioRepositoryPort usuarios;
    @Mock private StorageService storage;
    @Mock private EmailService email;
    @Mock private PuntosService puntos;

    private static Usuario usuario(Long id, String nombre, String rol) {
        return new Usuario(id, nombre, nombre.split(" ")[0].toLowerCase() + "@ucundinamarca.edu.co", "h", rol, LocalDateTime.now(), null);
    }
    private static Publicacion publicacion(Long id, Long autor) {
        return new Publicacion(id, autor, "¿Cómo derivo x²?", "No entiendo la regla", "derivadas", LocalDateTime.now(), LocalDateTime.now(), true, null);
    }

    @Nested
    class Publicar {
        private CrearPublicacionService service() { return new CrearPublicacionService(publicaciones, usuarios, storage); }

        @Test
        @DisplayName("Publica con título y contenido limpios y calcula las iniciales del autor")
        void publica() {
            when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(usuario(1L, "Laura Gómez Díaz", "ESTUDIANTE")));
            when(publicaciones.guardar(any())).thenAnswer(inv -> { Publicacion p = inv.getArgument(0); p.setId(10L); return p; });

            PublicacionDetalleResponse r = service().crear(new CrearPublicacionRequest("  ¿Cómo derivo x²?  ", " No entiendo ", "derivadas", "  "), "laura@ucundinamarca.edu.co");

            assertThat(r.getId()).isEqualTo(10L);
            assertThat(r.getTitulo()).isEqualTo("¿Cómo derivo x²?");
            assertThat(r.getContenido()).isEqualTo("No entiendo");
            assertThat(r.getImagenUrl()).isNull();
            assertThat(r.getAutor().getIniciales()).isEqualTo("LD");
            assertThat(r.getRespuestas()).isEmpty();
        }

        @Test
        @DisplayName("Acepta una imagen que ya está en el bucket de SAESU")
        void imagenPropia() {
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));
            when(storage.esUrlPropia("https://saesu.s3/foro/a.png")).thenReturn(true);
            when(publicaciones.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

            PublicacionDetalleResponse r = service().crear(new CrearPublicacionRequest("T", "C", "limites", "https://saesu.s3/foro/a.png"), "laura@ucundinamarca.edu.co");

            assertThat(r.getImagenUrl()).isEqualTo("https://saesu.s3/foro/a.png");
            assertThat(r.getAutor().getIniciales()).isEqualTo("L");
        }

        @Test
        @DisplayName("Rechaza imágenes de otros sitios (deben subirse primero a S3)")
        void imagenAjena() {
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));
            when(storage.esUrlPropia("https://otro.com/x.png")).thenReturn(false);

            assertThatThrownBy(() -> service().crear(new CrearPublicacionRequest("T", "C", "limites", "https://otro.com/x.png"), "laura@ucundinamarca.edu.co"))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(publicaciones, never()).guardar(any());
        }
    }

    @Nested
    class Responder {
        private ResponderPublicacionService service() { return new ResponderPublicacionService(publicaciones, respuestas, usuarios, email, puntos); }

        @Test
        @DisplayName("Responde, avisa por correo al autor de la publicación y da puntos")
        void responde() {
            Usuario laura = usuario(1L, "Laura Gómez", "ESTUDIANTE");
            Usuario camila = usuario(2L, "Camila Torres", "ESTUDIANTE");
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 2L)));
            when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura));
            when(usuarios.buscarPorId(2L)).thenReturn(Optional.of(camila));
            when(respuestas.guardar(any())).thenAnswer(inv -> { Respuesta r = inv.getArgument(0); r.setId(50L); return r; });

            RespuestaResponse r = service().responder(10L, new ResponderPublicacionRequest(" Usa la regla de la potencia ", null), "laura@ucundinamarca.edu.co");

            assertThat(r.getId()).isEqualTo(50L);
            assertThat(r.getContenido()).isEqualTo("Usa la regla de la potencia");
            assertThat(r.isEsOficial()).isFalse();
            verify(email).enviarCorreoNuevaRespuesta(camila.getCorreo(), "Camila Torres", "¿Cómo derivo x²?");
            verify(puntos).otorgarPuntosForo(1L, 2L);
        }

        @Test
        @DisplayName("Responder en una publicación propia no envía correo")
        void publicacionPropia() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));
            when(respuestas.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

            service().responder(10L, new ResponderPublicacionRequest("Ya entendí", null), "laura@ucundinamarca.edu.co");

            verify(email, never()).enviarCorreoNuevaRespuesta(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Si el correo falla, la respuesta se guarda igual")
        void correoFalla() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 2L)));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));
            when(usuarios.buscarPorId(2L)).thenReturn(Optional.of(usuario(2L, "Camila", "ESTUDIANTE")));
            when(respuestas.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
            doThrow(new RuntimeException("SMTP caído")).when(email).enviarCorreoNuevaRespuesta(anyString(), anyString(), anyString());

            assertThat(service().responder(10L, new ResponderPublicacionRequest("Hola", null), "laura@ucundinamarca.edu.co")).isNotNull();
            verify(puntos).otorgarPuntosForo(1L, 2L);
        }

        @Test
        @DisplayName("Respuesta a otra respuesta (un nivel) guarda el padre")
        void respuestaAnidada() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(respuestas.buscarPorId(50L)).thenReturn(Optional.of(new Respuesta(50L, 10L, 2L, null, "Padre", false, LocalDateTime.now())));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));
            when(respuestas.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

            service().responder(10L, new ResponderPublicacionRequest("Gracias", 50L), "laura@ucundinamarca.edu.co");

            ArgumentCaptor<Respuesta> guardada = ArgumentCaptor.forClass(Respuesta.class);
            verify(respuestas).guardar(guardada.capture());
            assertThat(guardada.getValue().getRespuestaPadreId()).isEqualTo(50L);
        }

        @Test
        @DisplayName("No permite un segundo nivel de anidamiento")
        void segundoNivel() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(respuestas.buscarPorId(51L)).thenReturn(Optional.of(new Respuesta(51L, 10L, 2L, 50L, "Hija", false, LocalDateTime.now())));

            assertThatThrownBy(() -> service().responder(10L, new ResponderPublicacionRequest("x", 51L), "laura@ucundinamarca.edu.co"))
                    .isInstanceOf(RespuestaPadreInvalidaException.class)
                    .hasMessageContaining("1 nivel");
            verify(respuestas, never()).guardar(any());
        }

        @Test
        @DisplayName("La respuesta padre debe ser de la misma publicación")
        void padreDeOtraPublicacion() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(respuestas.buscarPorId(60L)).thenReturn(Optional.of(new Respuesta(60L, 99L, 2L, null, "Otra", false, LocalDateTime.now())));

            assertThatThrownBy(() -> service().responder(10L, new ResponderPublicacionRequest("x", 60L), "laura@ucundinamarca.edu.co"))
                    .isInstanceOf(RespuestaPadreInvalidaException.class);
        }

        @Test
        @DisplayName("Publicación o respuesta padre inexistentes: 404")
        void inexistentes() {
            when(publicaciones.buscarPorId(404L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service().responder(404L, new ResponderPublicacionRequest("x", null), "a@ucundinamarca.edu.co"))
                    .isInstanceOf(PublicacionNoEncontradaException.class);

            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(respuestas.buscarPorId(77L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service().responder(10L, new ResponderPublicacionRequest("x", 77L), "a@ucundinamarca.edu.co"))
                    .isInstanceOf(RespuestaNoEncontradaException.class);
            verify(puntos, never()).otorgarPuntosForo(anyLong(), anyLong());
        }
    }

    @Nested
    class Eliminar {
        private EliminarPublicacionService service() { return new EliminarPublicacionService(publicaciones, usuarios); }

        @Test
        @DisplayName("El autor puede eliminar su publicación")
        void autor() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(1L, "Laura", "ESTUDIANTE")));

            service().ejecutar(10L, "laura@ucundinamarca.edu.co");

            verify(publicaciones).eliminar(10L);
        }

        @Test
        @DisplayName("Un docente puede eliminar cualquier publicación")
        void docente() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(9L, "Profe Ortiz", "DOCENTE")));

            service().ejecutar(10L, "profe@ucundinamarca.edu.co");

            verify(publicaciones).eliminar(10L);
        }

        @Test
        @DisplayName("Otro estudiante no puede eliminarla (403)")
        void otroEstudiante() {
            when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(publicacion(10L, 1L)));
            when(usuarios.buscarPorCorreo(anyString())).thenReturn(Optional.of(usuario(2L, "Camila", "ESTUDIANTE")));

            assertThatThrownBy(() -> service().ejecutar(10L, "camila@ucundinamarca.edu.co")).isInstanceOf(AccessDeniedException.class);
            verify(publicaciones, never()).eliminar(anyLong());
        }
    }
}
