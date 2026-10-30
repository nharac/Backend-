package co.edu.ucundinamarca.saesu.practica.application.service;

import co.edu.ucundinamarca.saesu.practica.application.dto.response.PistaResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.PreguntaResponse;
import co.edu.ucundinamarca.saesu.practica.application.dto.response.ProgresoResponse;
import co.edu.ucundinamarca.saesu.practica.domain.exception.PreguntaNoEncontradaException;
import co.edu.ucundinamarca.saesu.practica.domain.model.Opcion;
import co.edu.ucundinamarca.saesu.practica.domain.model.Pregunta;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Dificultad;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.practica.ports.output.OpcionRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.PreguntaRepositoryPort;
import co.edu.ucundinamarca.saesu.practica.ports.output.RespuestaEstudianteRepositoryPort;
import co.edu.ucundinamarca.saesu.usuario.domain.model.Usuario;
import co.edu.ucundinamarca.saesu.usuario.ports.output.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

/** Consultas del módulo de práctica: listar preguntas, pista y progreso por tema. */
@ExtendWith(MockitoExtension.class)
class ConsultasPracticaServicesTest {

    @Mock private PreguntaRepositoryPort preguntas;
    @Mock private OpcionRepositoryPort opciones;
    @Mock private RespuestaEstudianteRepositoryPort respuestas;
    @Mock private UsuarioRepositoryPort usuarios;

    private Pregunta pregunta(Long id, String pista) {
        return new Pregunta(id, Tema.LIMITES, "¿Cuál es el límite de x² cuando x → 2?", Dificultad.BASICO, pista, null, null);
    }

    @Test
    @DisplayName("Listar preguntas: trae las opciones sin revelar cuál es la correcta")
    void listarSinRespuesta() {
        when(preguntas.listar("limites", "basico", 0, 10)).thenReturn(List.of(pregunta(7L, "Reemplaza x")));
        when(opciones.listarPorPreguntaId(7L)).thenReturn(List.of(
                new Opcion(1L, 7L, "A", "2", false), new Opcion(2L, 7L, "B", "4", true)));

        List<PreguntaResponse> r = new ListarPreguntasService(preguntas, opciones).listar("limites", "basico", 0, 10);

        assertThat(r).hasSize(1);
        assertThat(r.get(0).getTema()).isEqualTo("limites");
        assertThat(r.get(0).getDificultad()).isEqualTo("basico");
        assertThat(r.get(0).getOpciones()).extracting("letra").containsExactly("A", "B");
        // OpcionResponse no tiene el campo es_correcta: el navegador nunca sabe cuál es
        assertThat(r.get(0).getOpciones().get(0).getClass().getDeclaredFields())
                .extracting("name").doesNotContain("esCorrecta");
    }

    @Test
    @DisplayName("Contar preguntas delega al puerto")
    void contar() {
        when(preguntas.contar("derivadas", null)).thenReturn(15L);
        assertThat(new ListarPreguntasService(preguntas, opciones).contar("derivadas", null)).isEqualTo(15L);
    }

    @Test
    @DisplayName("Pista disponible")
    void pistaDisponible() {
        when(preguntas.buscarPorId(7L)).thenReturn(Optional.of(pregunta(7L, "Reemplaza x por 2")));

        PistaResponse r = new ObtenerPistaService(preguntas).obtenerPista(7L);

        assertThat(r.getPista()).isEqualTo("Reemplaza x por 2");
        assertThat(r.getMensaje()).isEqualTo("Pista cargada correctamente");
    }

    @Test
    @DisplayName("Pregunta sin pista: lo dice en el mensaje")
    void sinPista() {
        when(preguntas.buscarPorId(8L)).thenReturn(Optional.of(pregunta(8L, null)));

        assertThat(new ObtenerPistaService(preguntas).obtenerPista(8L).getMensaje()).contains("No hay pista");
    }

    @Test
    @DisplayName("Pista de una pregunta que no existe: excepción de pregunta no encontrada")
    void pistaPreguntaInexistente() {
        when(preguntas.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new ObtenerPistaService(preguntas).obtenerPista(99L))
                .isInstanceOf(PreguntaNoEncontradaException.class);
    }

    @Test
    @DisplayName("Progreso: calcula el porcentaje de cada tema y deja en cero los que no ha practicado")
    void progresoPorTema() {
        Usuario laura = new Usuario();
        laura.setId(1L);
        when(usuarios.buscarPorCorreo("laura@ucundinamarca.edu.co")).thenReturn(Optional.of(laura));
        when(respuestas.contarPorTema(1L)).thenReturn(List.of(
                new Object[]{"limites", 8L, 6L}, new Object[]{"derivadas", 3, 3}));

        ProgresoResponse r = new ObtenerProgresoService(respuestas, usuarios).obtenerProgreso("laura@ucundinamarca.edu.co");

        assertThat(r.getLimites().getRespondidas()).isEqualTo(8);
        assertThat(r.getLimites().getPorcentaje()).isCloseTo(75.0, within(0.01));
        assertThat(r.getDerivadas().getPorcentaje()).isCloseTo(100.0, within(0.01));
        assertThat(r.getIntegrales().getRespondidas()).isZero();
        assertThat(r.getIntegrales().getPorcentaje()).isZero();
    }
}
