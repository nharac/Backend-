package co.edu.ucundinamarca.saesu.foro.adapters.output.jpa;

import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.adapter.PublicacionRepositoryAdapter;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.adapter.RespuestaRepositoryAdapter;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.PublicacionEntity;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.entity.RespuestaEntity;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper.PublicacionMapper;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.mapper.RespuestaMapper;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.PublicacionJpaRepository;
import co.edu.ucundinamarca.saesu.foro.adapters.output.jpa.repository.RespuestaJpaRepository;
import co.edu.ucundinamarca.saesu.foro.domain.model.Publicacion;
import co.edu.ucundinamarca.saesu.foro.domain.model.Respuesta;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.entity.UsuarioEntity;
import co.edu.ucundinamarca.saesu.usuario.adapters.output.jpa.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ForoJpaAdaptersTest {

    private final PublicacionJpaRepository publicaciones = mock(PublicacionJpaRepository.class);
    private final RespuestaJpaRepository respuestas = mock(RespuestaJpaRepository.class);
    private final UsuarioJpaRepository usuarios = mock(UsuarioJpaRepository.class);
    private final PublicacionRepositoryAdapter publicacionAdapter =
            new PublicacionRepositoryAdapter(publicaciones, usuarios, new PublicacionMapper());
    private final RespuestaRepositoryAdapter respuestaAdapter =
            new RespuestaRepositoryAdapter(respuestas, usuarios, new RespuestaMapper());

    private static UsuarioEntity usuario(long id) {
        UsuarioEntity u = new UsuarioEntity();
        u.setId(id);
        u.setNombre("Laura");
        return u;
    }

    private static PublicacionEntity publicacionEntity() {
        PublicacionEntity e = new PublicacionEntity();
        e.setId(1L);
        e.setAutor(usuario(4L));
        e.setTitulo("¿Cómo derivo x²?");
        e.setContenido("No entiendo la regla de la potencia");
        e.setTema("derivadas");
        e.setFechaCreacion(LocalDateTime.of(2026, 10, 1, 8, 0));
        e.setActiva(true);
        e.setImagenUrl("https://img/1.png");
        return e;
    }

    @Test
    void listarConvierteLaPaginaYOrdenaPorFecha() {
        when(publicaciones.buscarConFiltros(eq("derivadas"), eq("x"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(publicacionEntity())));

        List<Publicacion> lista = publicacionAdapter.listar("derivadas", "x", "antiguo", 0, 10);
        publicacionAdapter.listar("derivadas", "x", "reciente", 0, 10);

        assertThat(lista).singleElement().satisfies(p -> {
            assertThat(p.getAutorId()).isEqualTo(4L);
            assertThat(p.getTitulo()).isEqualTo("¿Cómo derivo x²?");
            assertThat(p.getImagenUrl()).isEqualTo("https://img/1.png");
        });
        ArgumentCaptor<Pageable> pagina = ArgumentCaptor.forClass(Pageable.class);
        verify(publicaciones, org.mockito.Mockito.times(2)).buscarConFiltros(eq("derivadas"), eq("x"), pagina.capture());
        assertThat(pagina.getAllValues().get(0).getSort().getOrderFor("fechaCreacion").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pagina.getAllValues().get(1).getSort().getOrderFor("fechaCreacion").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void contarYBuscarPorId() {
        when(publicaciones.contarConFiltros(null, null)).thenReturn(12L);
        PublicacionEntity sinAutor = publicacionEntity();
        sinAutor.setAutor(null);
        when(publicaciones.findById(1L)).thenReturn(Optional.of(sinAutor));

        assertThat(publicacionAdapter.contar(null, null)).isEqualTo(12L);
        assertThat(publicacionAdapter.buscarPorId(1L)).get().extracting(Publicacion::getAutorId).isNull();
        assertThat(publicacionAdapter.buscarPorId(2L)).isEmpty();
    }

    @Test
    void guardarCargaElAutorYFallaSiNoExiste() {
        Publicacion nueva = new Publicacion(null, 4L, "Título", "Contenido", "limites",
                LocalDateTime.now(), null, true, null);
        when(usuarios.findById(4L)).thenReturn(Optional.of(usuario(4L)));
        when(publicaciones.save(any(PublicacionEntity.class))).thenAnswer(inv -> {
            PublicacionEntity e = inv.getArgument(0);
            e.setId(50L);
            return e;
        });

        Publicacion guardada = publicacionAdapter.guardar(nueva);

        assertThat(guardada.getId()).isEqualTo(50L);
        assertThat(guardada.getAutorId()).isEqualTo(4L);

        Publicacion deOtro = new Publicacion(null, 99L, "T", "C", null, null, null, true, null);
        assertThatThrownBy(() -> publicacionAdapter.guardar(deOtro)).hasMessageContaining("99");

        Publicacion sinAutor = new Publicacion(null, null, "T", "C", null, null, null, true, null);
        assertThat(publicacionAdapter.guardar(sinAutor).getAutorId()).isNull();
    }

    @Test
    void eliminarSoloOcultaLaPublicacion() {
        PublicacionEntity e = publicacionEntity();
        when(publicaciones.findById(1L)).thenReturn(Optional.of(e));

        publicacionAdapter.eliminar(1L);
        publicacionAdapter.eliminar(2L);

        assertThat(e.getActiva()).isFalse();
        verify(publicaciones).save(e);
    }

    @Test
    void respuestasListarBuscarContarYGuardar() {
        RespuestaEntity r = new RespuestaEntity(8L, 1L, usuario(4L), null, "Usa la regla de la cadena", true,
                LocalDateTime.now(), null);
        when(respuestas.listarPorPublicacion(1L)).thenReturn(List.of(r));
        when(respuestas.findById(8L)).thenReturn(Optional.of(r));
        when(respuestas.countByPublicacionId(1L)).thenReturn(3L);
        when(usuarios.findById(4L)).thenReturn(Optional.of(usuario(4L)));
        when(respuestas.save(any(RespuestaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(respuestaAdapter.listarPorPublicacion(1L)).singleElement()
                .satisfies(x -> {
                    assertThat(x.getEsOficial()).isTrue();
                    assertThat(x.isOculta()).isFalse();
                    assertThat(x.getAutorId()).isEqualTo(4L);
                });
        assertThat(respuestaAdapter.buscarPorId(8L)).isPresent();
        assertThat(respuestaAdapter.contarPorPublicacion(1L)).isEqualTo(3L);

        Respuesta nueva = new Respuesta(null, 1L, 4L, 8L, "Gracias", false, LocalDateTime.now());
        assertThat(respuestaAdapter.guardar(nueva).getRespuestaPadreId()).isEqualTo(8L);

        Respuesta anonima = new Respuesta(null, 1L, null, null, "Hola", false, LocalDateTime.now());
        assertThat(respuestaAdapter.guardar(anonima).getAutorId()).isNull();

        Respuesta fantasma = new Respuesta(null, 1L, 77L, null, "x", false, LocalDateTime.now());
        assertThatThrownBy(() -> respuestaAdapter.guardar(fantasma)).hasMessageContaining("77");
        verify(usuarios, never()).findById(null);
    }
}
