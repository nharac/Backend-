package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.adapters.output.jdbc.LogrosJdbcRepository;
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

    @Mock private LogrosJdbcRepository repo;
    @InjectMocks private LogrosService servicio;

    /** Estudiante nuevo: no cumple nada. Cada prueba cambia solo lo que necesita. */
    private void estudianteNuevo(Map<String, OffsetDateTime> yaTiene) {
        lenient().when(repo.obtenidos(U)).thenReturn(yaTiene);
        lenient().when(repo.contarAciertos(U)).thenReturn(0L);
        lenient().when(repo.contarAcertadasAlPrimerIntento(U)).thenReturn(0L);
        lenient().when(repo.contarPreguntasDelTema(anyString())).thenReturn(5L);
        lenient().when(repo.contarResueltasDelTema(eq(U), anyString())).thenReturn(0L);
        lenient().when(repo.rachaActual(U)).thenReturn(0L);
        lenient().when(repo.contarPublicaciones(U)).thenReturn(0L);
        lenient().when(repo.contarRespuestasForo(U)).thenReturn(0L);
        lenient().when(repo.contarRespuestasOficiales(U)).thenReturn(0L);
        lenient().when(repo.puntosTotales(U)).thenReturn(0L);
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
        when(repo.contarAciertos(U)).thenReturn(1L);

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
        lenient().when(repo.contarAciertos(U)).thenReturn(30L);

        assertThat(servicio.evaluar(U)).isEmpty();
        verify(repo, never()).guardar(U, "PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Dominio de tema: hay que acertar todas las preguntas del tema")
    void dominioDeTema() {
        estudianteNuevo(new HashMap<>());
        when(repo.contarResueltasDelTema(U, "limites")).thenReturn(5L);   // 5 de 5
        when(repo.contarResueltasDelTema(U, "derivadas")).thenReturn(4L); // 4 de 5

        assertThat(servicio.evaluar(U)).extracting(LogroResponse::getCodigo)
                .contains("DOMINIO_LIMITES")
                .doesNotContain("DOMINIO_DERIVADAS");
    }

    @Test
    @DisplayName("Un tema sin preguntas no se puede 'dominar'")
    void temaVacio() {
        estudianteNuevo(new HashMap<>());
        when(repo.contarPreguntasDelTema("integrales")).thenReturn(0L);

        assertThat(servicio.evaluar(U)).extracting(LogroResponse::getCodigo).doesNotContain("DOMINIO_INTEGRALES");
    }

    @Test
    @DisplayName("Rachas, foro y puntos usan sus umbrales (3, 7, 10, 500)")
    void umbrales() {
        estudianteNuevo(new HashMap<>());
        when(repo.rachaActual(U)).thenReturn(7L);
        when(repo.contarPublicaciones(U)).thenReturn(1L);
        when(repo.contarRespuestasForo(U)).thenReturn(9L);   // le falta 1 para Colaborador
        when(repo.contarRespuestasOficiales(U)).thenReturn(1L);
        when(repo.puntosTotales(U)).thenReturn(500L);
        when(repo.contarAcertadasAlPrimerIntento(U)).thenReturn(10L);

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
}
