package co.edu.ucundinamarca.saesu.core.web;

import co.edu.ucundinamarca.saesu.core.exception.GlobalExceptionHandler;
import co.edu.ucundinamarca.saesu.core.storage.StorageService;
import co.edu.ucundinamarca.saesu.docente.adapters.input.rest.DocenteController;
import co.edu.ucundinamarca.saesu.docente.adapters.input.rest.DocentePanelController;
import co.edu.ucundinamarca.saesu.docente.adapters.input.rest.DocentePreguntasController;
import co.edu.ucundinamarca.saesu.docente.application.service.BancoPreguntasService;
import co.edu.ucundinamarca.saesu.docente.application.service.EstadisticasDocenteService;
import co.edu.ucundinamarca.saesu.docente.application.service.ExportarEstudiantesService;
import co.edu.ucundinamarca.saesu.docente.application.service.ModeracionForoService;
import co.edu.ucundinamarca.saesu.docente.ports.input.GestionDocenteUseCase;
import co.edu.ucundinamarca.saesu.foro.adapters.input.rest.ForoImagenController;
import co.edu.ucundinamarca.saesu.quiz.domain.exception.QuizNoEncontradoException;
import co.edu.ucundinamarca.saesu.quiz.ports.input.QuizUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Adaptadores REST del panel docente y de la subida de imágenes del foro. */
class ControladoresDocenteTest {

    private static final String PROFE = "profe@ucundinamarca.edu.co";
    private static final UsernamePasswordAuthenticationToken AUTH =
            new UsernamePasswordAuthenticationToken(PROFE, null, List.of(new SimpleGrantedAuthority("ROLE_DOCENTE")));

    private static MockMvc mvc(Object controlador) {
        return MockMvcBuilders.standaloneSetup(controlador).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("Exportar: xlsx por defecto con nombre de archivo y tipo de Excel; csv si se pide")
    void exportar() throws Exception {
        ExportarEstudiantesService exportar = mock(ExportarEstudiantesService.class);
        MockMvc mvc = mvc(new DocentePanelController(mock(EstadisticasDocenteService.class), exportar, mock(QuizUseCase.class)));
        when(exportar.excel()).thenReturn(new byte[]{80, 75, 3, 4});
        when(exportar.csv()).thenReturn("nombre;correo\n");

        mvc.perform(get("/api/docente/estudiantes/exportar"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(".xlsx")))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        mvc.perform(get("/api/docente/estudiantes/exportar").param("formato", "csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(".csv")));
    }

    @Test
    @DisplayName("Quiz de otro estudiante: 404")
    void quizAjeno() throws Exception {
        QuizUseCase quiz = mock(QuizUseCase.class);
        when(quiz.resultadoDeEstudiante(3L, 99L)).thenThrow(new QuizNoEncontradoException("Quiz no encontrado"));

        mvc(new DocentePanelController(mock(EstadisticasDocenteService.class), mock(ExportarEstudiantesService.class), quiz))
                .perform(get("/api/docente/estudiantes/3/quices/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Respuesta oficial y moderación usan el docente del JWT")
    void oficialYModeracion() throws Exception {
        GestionDocenteUseCase gestion = mock(GestionDocenteUseCase.class);
        ModeracionForoService moderacion = mock(ModeracionForoService.class);
        MockMvc mvc = mvc(new DocenteController(gestion, moderacion));

        mvc.perform(post("/api/docente/respuestas/50/oficial").principal(AUTH)).andExpect(status().isOk());
        verify(gestion).marcarRespuestaOficial(50L, PROFE);
        mvc.perform(delete("/api/docente/respuestas/50/oficial")).andExpect(status().isOk());
        verify(gestion).quitarRespuestaOficial(50L);

        mvc.perform(post("/api/docente/respuestas/51/ocultar").principal(AUTH)).andExpect(status().isNoContent());
        verify(moderacion).ocultarRespuesta(51L, PROFE);
        mvc.perform(delete("/api/docente/respuestas/51/ocultar")).andExpect(status().isNoContent());
        verify(moderacion).restaurarRespuesta(51L);
        mvc.perform(post("/api/docente/publicaciones/10/ocultar").principal(AUTH)).andExpect(status().isNoContent());
        verify(moderacion).ocultarPublicacion(10L, PROFE);
        mvc.perform(delete("/api/docente/publicaciones/10/ocultar")).andExpect(status().isNoContent());
        verify(moderacion).restaurarPublicacion(10L);
    }

    @Test
    @DisplayName("Cambiar estado de una pregunta sin el campo 'activa': 400")
    void estadoSinCampo() throws Exception {
        BancoPreguntasService banco = mock(BancoPreguntasService.class);
        mvc(new DocentePreguntasController(banco))
                .perform(patch("/api/docente/preguntas/7/estado").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("activa")));
        verify(banco, never()).cambiarEstado(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Imagen del foro: devuelve la URL del bucket")
    void imagenForo() throws Exception {
        StorageService storage = mock(StorageService.class);
        when(storage.subirImagen(any(), eq("foro"))).thenReturn("https://saesu.s3/foro/a.png");

        mvc(new ForoImagenController(storage))
                .perform(multipart("/api/foro/imagenes").file(new MockMultipartFile("archivo", "a.png", "image/png", new byte[]{1})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://saesu.s3/foro/a.png"));
    }
}
