package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort.Estadisticas;
import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Logro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Reglas de los 11 logros. Sin base de datos: el repositorio es un mock. */
@ExtendWith(MockitoExtension.class)
class LogrosServiceTest {

    private static final Long U = 5L;

    @Mock private LogrosPort repo;
    @InjectMocks private LogrosService servicio;

    /** Cifras del estudiante; cada prueba cambia solo lo que necesita. */
    private long aciertos, primera, racha, publicaciones, respuestasForo, oficiales, puntos;
    private final Map<String, long[]> temas = new HashMap<>(Map.of(
            "limites", new long[]{5, 0}, "derivadas", new long[]{5, 0}, "integrales", new long[]{5, 0}));

    /** Estudiante nuevo: no cumple nada. */
    private void estudianteNuevo(Map<String, OffsetDateTime> yaTiene) {
        lenient().when(repo.obtenidos(U)).thenReturn(yaTiene);
        lenient().when(repo.estadisticas(U)).thenAnswer(inv -> new Estadisticas(
                aciertos, primera, racha, publicaciones, respuestasForo, oficiales, puntos, temas));
        lenient().when(repo.guardar(eq(U), anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("Un estudiante sin actividad no desbloquea nada")
    void sinActividad() {
        estudianteNuevo(new HashMap<>());

        assertThat(servicio.evaluar(U)).isEmpty();
        verify(repo, never()).guardar(anyLong(), anyString());
    }

    @Test
    @DisplayName("La primera respuesta correcta desbloquea 'Primer paso'")
    void primerAcierto() {
        estudianteNuevo(new HashMap<>());
        aciertos = 1;

        List<LogroResponse> nuevos = servicio.evaluar(U);

        assertThat(nuevos).extracting(LogroResponse::getCodigo).containsExactly("PRIMER_ACIERTO");
        assertThat(nuevos.get(0).isObtenido()).isTrue();
        verify(repo).guardar(U, "PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Un logro que ya tiene no se vuelve a guardar ni a anunciar")
    void yaObtenido() {
        estudianteNuevo(new HashMap<>(Map.of("PRIMER_ACIERTO", OffsetDateTime.now())));
        // aunque cumpla la condición, como ya lo tiene ni se vuelve a evaluar
        aciertos = 30;

        assertThat(servicio.evaluar(U)).isEmpty();
        verify(repo, never()).guardar(U, "PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Dominio de tema: hay que acertar todas las preguntas del tema")
    void dominioDeTema() {
        estudianteNuevo(new HashMap<>());
        temas.put("limites", new long[]{5, 5});   // 5 de 5
        temas.put("derivadas", new long[]{5, 4}); // 4 de 5

        assertThat(servicio.evaluar(U)).extracting(LogroResponse::getCodigo)
                .contains("DOMINIO_LIMITES")
                .doesNotContain("DOMINIO_DERIVADAS");
    }

    @Test
    @DisplayName("Un tema sin preguntas no se puede 'dominar'")
    void temaVacio() {
        estudianteNuevo(new HashMap<>());
        temas.put("integrales", new long[]{0, 0});

        assertThat(servicio.evaluar(U)).extracting(LogroResponse::getCodigo).doesNotContain("DOMINIO_INTEGRALES");
    }

    @Test
    @DisplayName("Rachas, foro y puntos usan sus umbrales (3, 7, 10, 500)")
    void umbrales() {
        estudianteNuevo(new HashMap<>());
        racha = 7;
        publicaciones = 1;
        respuestasForo = 9;   // le falta 1 para Colaborador
        oficiales = 1;
        puntos = 500;
        primera = 10;

        assertThat(servicio.evaluar(U)).extracting(LogroResponse::getCodigo)
                .contains("RACHA_3", "RACHA_7", "CURIOSO", "RESPUESTA_DE_ORO", "PUNTOS_500", "A_LA_PRIMERA")
                .doesNotContain("COLABORADOR");
    }

    @Test
    @DisplayName("listar devuelve los 11 logros marcando cuáles tiene")
    void listar() {
        estudianteNuevo(new HashMap<>(Map.of("CURIOSO", OffsetDateTime.now())));

        List<LogroResponse> todos = servicio.listar(U);

        assertThat(todos).hasSize(Logro.values().length).hasSize(11);
        assertThat(todos).filteredOn(LogroResponse::isObtenido)
                .extracting(LogroResponse::getCodigo).containsExactly("CURIOSO");
    }

    @Test
    @DisplayName("Si ya tiene los 11 logros no vuelve a calcular nada")
    void todosObtenidos() {
        Map<String, OffsetDateTime> todos = new HashMap<>();
        for (Logro l : Logro.values()) todos.put(l.name(), OffsetDateTime.now());
        estudianteNuevo(todos);

        assertThat(servicio.listar(U)).allMatch(LogroResponse::isObtenido);
        verify(repo, never()).estadisticas(anyLong());
    }
}
