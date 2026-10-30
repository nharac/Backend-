package co.edu.ucundinamarca.saesu.practica.adapters.output.jpa;

import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter.ChatMensajeRepositoryAdapter;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter.OpcionRepositoryAdapter;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter.PreguntaRepositoryAdapter;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.adapter.RespuestaEstudianteRepositoryAdapter;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.ChatMensajeEntity;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.OpcionEntity;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.PreguntaEntity;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.entity.RespuestaEstudianteEntity;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.ChatMensajeMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.OpcionMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.PreguntaMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.mapper.RespuestaEstudianteMapper;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.ChatMensajeJpaRepository;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.OpcionJpaRepository;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.PreguntaJpaRepository;
import co.edu.ucundinamarca.saesu.practica.adapters.output.jpa.repository.RespuestaEstudianteJpaRepository;
import co.edu.ucundinamarca.saesu.practica.domain.model.ChatMensaje;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.model.RespuestaEstudiante;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.RolEmisor;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PracticaJpaAdaptersTest {

    private static final LocalDateTime HACE_UN_RATO = LocalDateTime.of(2026, 10, 29, 18, 30);

    @Test
    void chatGuardaYDevuelveElHistorialEnOrdenCronologico() {
        ChatMensajeJpaRepository repo = mock(ChatMensajeJpaRepository.class);
        ChatMensajeRepositoryAdapter adapter = new ChatMensajeRepositoryAdapter(repo, new ChatMensajeMapper());
        ChatMensajeEntity nuevo = new ChatMensajeEntity(2L, 3L, null, "ia", "¿Qué intentaste?", HACE_UN_RATO.plusMinutes(1));
        ChatMensajeEntity viejo = new ChatMensajeEntity(1L, 3L, null, "usuario", "Ayuda con límites", HACE_UN_RATO);
        when(repo.historialLibre(eq(3L), any(Pageable.class))).thenReturn(List.of(nuevo, viejo));
        when(repo.historialPorPregunta(eq(3L), eq(7L), any(Pageable.class))).thenReturn(List.of(nuevo, viejo));
        when(repo.contarMensajesUsuarioDesde(3L, HACE_UN_RATO)).thenReturn(4L);
        when(repo.save(any(ChatMensajeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        List<ChatMensaje> libre = adapter.listarHistorialLibre(3L, 20);
        assertThat(libre).extracting(ChatMensaje::getId).containsExactly(1L, 2L);
        assertThat(libre.get(0).getRolEmisor()).isEqualTo(RolEmisor.USUARIO);
        assertThat(adapter.listarHistorialPorPregunta(3L, 7L, 20)).extracting(ChatMensaje::getId).containsExactly(1L, 2L);
        assertThat(adapter.contarMensajesHoy(3L, HACE_UN_RATO)).isEqualTo(4L);

        ChatMensaje guardado = adapter.guardar(new ChatMensaje(null, 3L, 7L, RolEmisor.IA, "Bien", HACE_UN_RATO));
        assertThat(guardado.getRolEmisor()).isEqualTo(RolEmisor.IA);
        assertThat(guardado.getPreguntaId()).isEqualTo(7L);

        adapter.eliminarHistorialLibre(3L);
        adapter.eliminarHistorialPorPregunta(3L, 7L);
        verify(repo).eliminarHistorialLibre(3L);
        verify(repo).eliminarHistorialPorPregunta(3L, 7L);
    }

    @Test
    void opcionesPorPreguntaYCorrecta() {
        OpcionJpaRepository repo = mock(OpcionJpaRepository.class);
        OpcionRepositoryAdapter adapter = new OpcionRepositoryAdapter(repo, new OpcionMapper());
        OpcionEntity a = new OpcionEntity(1L, 7L, "A", "2x", true);
        OpcionEntity b = new OpcionEntity(2L, 7L, "B", "x²", false);
        when(repo.findByPreguntaIdOrderByLetra(7L)).thenReturn(List.of(a, b));
        when(repo.findById(2L)).thenReturn(Optional.of(b));
        when(repo.findByPreguntaIdAndEsCorrectaTrue(7L)).thenReturn(Optional.of(a));

        assertThat(adapter.listarPorPreguntaId(7L)).extracting(Opcion::getLetra).containsExactly("A", "B");
        assertThat(adapter.buscarPorId(2L)).get().extracting(Opcion::getEsCorrecta).isEqualTo(false);
        assertThat(adapter.buscarCorrectaPorPreguntaId(7L)).get().extracting(Opcion::getTexto).isEqualTo("2x");
        assertThat(new OpcionMapper().toEntity(new Opcion(3L, 7L, "C", "0", false)).getLetra()).isEqualTo("C");
    }

    @Test
    void preguntasListarContarBuscarYGuardar() {
        PreguntaJpaRepository repo = mock(PreguntaJpaRepository.class);
        PreguntaRepositoryAdapter adapter = new PreguntaRepositoryAdapter(repo, new PreguntaMapper());
        PreguntaEntity e = new PreguntaEntity(7L, "derivadas", "Deriva x²", "intermedio", "Regla de la potencia",
                HACE_UN_RATO, HACE_UN_RATO);
        when(repo.buscarConFiltros(eq("derivadas"), eq(null), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(e)));
        when(repo.findById(7L)).thenReturn(Optional.of(e));
        when(repo.save(any(PreguntaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Pregunta> lista = adapter.listar("derivadas", null, 0, 10);
        assertThat(lista).singleElement().satisfies(p -> {
            assertThat(p.getTema()).isEqualTo(Tema.DERIVADAS);
            assertThat(p.getDificultad()).isEqualTo(Dificultad.INTERMEDIO);
            assertThat(p.getOpciones()).isNull();
        });
        assertThat(adapter.contar("derivadas", null)).isEqualTo(1L);
        assertThat(adapter.buscarPorId(7L)).isPresent();

        Pregunta sinFecha = new Pregunta(null, Tema.LIMITES, "lim", Dificultad.BASICO, null, null, null);
        assertThat(adapter.guardar(sinFecha).getFechaCreacion()).isNotNull();
        Pregunta conFecha = new Pregunta(null, Tema.INTEGRALES, "∫", Dificultad.AVANZADO, null, HACE_UN_RATO, null);
        assertThat(adapter.guardar(conFecha).getFechaCreacion()).isEqualTo(HACE_UN_RATO);
    }

    @Test
    void respuestasDelEstudiante() {
        RespuestaEstudianteJpaRepository repo = mock(RespuestaEstudianteJpaRepository.class);
        RespuestaEstudianteRepositoryAdapter adapter = new RespuestaEstudianteRepositoryAdapter(repo, new RespuestaEstudianteMapper());
        RespuestaEstudianteEntity e = new RespuestaEstudianteEntity(1L, 3L, 7L, 2L, false, HACE_UN_RATO);
        when(repo.findByUsuarioId(3L)).thenReturn(List.of(e));
        when(repo.save(any(RespuestaEstudianteEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repo.contarPreguntasRespondidas(3L)).thenReturn(10L);
        when(repo.contarPreguntasCorrectas(3L)).thenReturn(6L);
        List<Object[]> porTema = List.<Object[]>of(new Object[]{"limites", 4L, 3L});
        when(repo.contarPorTema(3L)).thenReturn(porTema);

        assertThat(adapter.listarPorUsuario(3L)).singleElement().extracting(RespuestaEstudiante::getOpcionId).isEqualTo(2L);
        assertThat(adapter.guardar(new RespuestaEstudiante(null, 3L, 7L, 1L, true, HACE_UN_RATO)).getEsCorrecta()).isTrue();
        assertThat(adapter.contarPreguntasRespondidas(3L)).isEqualTo(10L);
        assertThat(adapter.contarPreguntasCorrectas(3L)).isEqualTo(6L);
        assertThat(adapter.contarPorTema(3L)).isSameAs(porTema);
    }

    @Test
    void valoresDesconocidosSeRechazan() {
        assertThatThrownBy(() -> Tema.desdeValor("geometria")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Dificultad.desdeValor("imposible")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RolEmisor.desdeValor("bot")).isInstanceOf(IllegalArgumentException.class);
        assertThat(Tema.desdeValor("LIMITES")).isEqualTo(Tema.LIMITES);
    }
}
