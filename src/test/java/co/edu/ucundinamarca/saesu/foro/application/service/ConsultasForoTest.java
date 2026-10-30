package co.edu.ucundinamarca.saesu.foro.application.service;

import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionDetalleResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.PublicacionResumenResponse;
import co.edu.ucundinamarca.saesu.foro.application.dto.response.RespuestaResponse;
import co.edu.ucundinamarca.saesu.foro.domain.exception.PublicacionNoEncontradaException;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.domain.model.PublicacionResumen;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionRepositoryPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.PublicacionResumenPort;
import co.edu.ucundinamarca.saesu.foro.ports.output.RespuestaRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

/** Listado del foro (una sola consulta) y detalle con respuestas anidadas y moderación. */
@ExtendWith(MockitoExtension.class)
class ConsultasForoTest {

    @Mock private PublicacionResumenPort resumen;
    @Mock private PublicacionRepositoryPort publicaciones;
    @Mock private RespuestaRepositoryPort respuestas;
    @Mock private UsuarioRepositoryPort usuarios;

    private static final LocalDateTime HACE_UN_RATO = LocalDateTime.of(2026, 10, 28, 9, 0);

    private static Usuario u(Long id, String nombre, String rol) {
        return new Usuario(id, nombre, id + "@ucundinamarca.edu.co", "h", rol, HACE_UN_RATO, null);
    }

    @Test
    @DisplayName("El listado arma la vista previa (150 caracteres) y las iniciales del autor")
    void listado() {
        String largo = "x".repeat(200);
        when(resumen.listar("derivadas", "regla", "reciente", 0, 20)).thenReturn(List.of(
                new PublicacionResumen(1L, "¿Regla de la cadena?", largo, "derivadas", HACE_UN_RATO, null, 5L, "Camila Torres", "ESTUDIANTE", 3)));

        List<PublicacionResumenResponse> r = new ListarPublicacionesService(resumen, publicaciones).listar("derivadas", "regla", "reciente", 0, 20);

        assertThat(r).hasSize(1);
        assertThat(r.get(0).getContenidoPreview()).hasSize(153).endsWith("...");
        assertThat(r.get(0).getAutor().getIniciales()).isEqualTo("CT");
        assertThat(r.get(0).getRespuestasCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Contar publicaciones delega al puerto")
    void contar() {
        when(publicaciones.contar(null, "límite")).thenReturn(12L);
        assertThat(new ListarPublicacionesService(resumen, publicaciones).contar(null, "límite")).isEqualTo(12L);
    }

    @Test
    @DisplayName("Utilidades del resumen: vista previa corta, nula e iniciales")
    void utilidades() {
        assertThat(ResumenForo.preview("corto")).isEqualTo("corto");
        assertThat(ResumenForo.preview(null)).isEmpty();
        assertThat(ResumenForo.iniciales("  ana  ")).isEqualTo("A");
        assertThat(ResumenForo.iniciales("")).isEqualTo("?");
        assertThat(ResumenForo.iniciales(null)).isEqualTo("?");
        assertThat(ResumenForo.iniciales("luiferney ortiz parra")).isEqualTo("LP");
    }

    @Test
    @DisplayName("El detalle arma el árbol de respuestas y oculta el contenido moderado")
    void detalleConArbol() {
        Publicacion p = new Publicacion(10L, 1L, "¿Cómo derivo x²?", "No entiendo", "derivadas", HACE_UN_RATO, HACE_UN_RATO, true, "https://s3/a.png");
        Respuesta directa = new Respuesta(50L, 10L, 2L, null, "Usa la regla de la potencia", true, HACE_UN_RATO);
        Respuesta hija = new Respuesta(51L, 10L, 1L, 50L, "¡Gracias!", false, HACE_UN_RATO);
        Respuesta oculta = new Respuesta(52L, 10L, 3L, null, "spam", true, HACE_UN_RATO, true);
        Respuesta huerfana = new Respuesta(53L, 10L, 2L, 999L, "Padre borrado", false, HACE_UN_RATO);
        when(publicaciones.buscarPorId(10L)).thenReturn(Optional.of(p));
        when(respuestas.listarPorPublicacion(10L)).thenReturn(List.of(directa, hija, oculta, huerfana));
        when(usuarios.buscarPorIds(anyCollection())).thenReturn(Map.of(
                1L, u(1L, "Laura Gómez", "ESTUDIANTE"), 2L, u(2L, "Profe Ortiz", "DOCENTE"), 3L, u(3L, "Mario Díaz", "ESTUDIANTE")));

        PublicacionDetalleResponse r = new VerDetallePublicacionService(publicaciones, respuestas, usuarios).verDetalle(10L);

        assertThat(r.getImagenUrl()).isEqualTo("https://s3/a.png");
        assertThat(r.getAutor().getIniciales()).isEqualTo("LG");
        assertThat(r.getRespuestas()).extracting(RespuestaResponse::getId).containsExactly(50L, 52L, 53L);
        RespuestaResponse primera = r.getRespuestas().get(0);
        assertThat(primera.isEsOficial()).isTrue();
        assertThat(primera.getRespuestasHijas()).extracting(RespuestaResponse::getContenido).containsExactly("¡Gracias!");
        RespuestaResponse moderada = r.getRespuestas().get(1);
        assertThat(moderada.isOculta()).isTrue();
        assertThat(moderada.getContenido()).isEqualTo(VerDetallePublicacionService.CONTENIDO_OCULTO);
        assertThat(moderada.isEsOficial()).as("una respuesta oculta no se muestra como oficial").isFalse();
    }

    @Test
    @DisplayName("Publicación eliminada o inexistente: 404")
    void detalleNoDisponible() {
        VerDetallePublicacionService service = new VerDetallePublicacionService(publicaciones, respuestas, usuarios);
        when(publicaciones.buscarPorId(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.verDetalle(404L)).isInstanceOf(PublicacionNoEncontradaException.class);

        when(publicaciones.buscarPorId(11L)).thenReturn(Optional.of(
                new Publicacion(11L, 1L, "t", "c", "limites", HACE_UN_RATO, HACE_UN_RATO, false, null)));
        assertThatThrownBy(() -> service.verDetalle(11L)).isInstanceOf(PublicacionNoEncontradaException.class);
    }
}
