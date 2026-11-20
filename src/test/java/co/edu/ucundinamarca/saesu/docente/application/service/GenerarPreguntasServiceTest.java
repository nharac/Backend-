package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.GenerarPreguntasRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerarPreguntasServiceTest {

    private final IaServicePort ia = mock(IaServicePort.class);
    private final GenerarPreguntasService servicio = new GenerarPreguntasService(ia, new ObjectMapper());

    private static String op(String l, String t, boolean c) {
        return "{\"letra\":\"" + l + "\",\"texto\":\"" + t + "\",\"correcta\":" + c + "}";
    }

    @Test
    void interpretaElJsonAunqueVengaConTextoAlrededorYDescartaLosMalos() {
        String buena = "{\"enunciado\":\"¿Cuánto es det [1 2; 3 4]?\",\"pista\":\"ad − bc\",\"opciones\":["
                + op("A", "−2", true) + "," + op("B", "2", false) + "," + op("C", "10", false) + "," + op("D", "−10", false) + "]}";
        String dosCorrectas = "{\"enunciado\":\"x\",\"opciones\":[" + op("A", "1", true) + "," + op("B", "2", true) + ","
                + op("C", "3", false) + "," + op("D", "4", false) + "]}";
        String tresOpciones = "{\"enunciado\":\"y\",\"opciones\":[" + op("A", "1", true) + "," + op("B", "2", false) + "," + op("C", "3", false) + "]}";
        when(ia.generarRespuesta(anyString(), eq(""), contains("Álgebra lineal")))
                .thenReturn("```json\n[" + buena + "," + dosCorrectas + "," + tresOpciones + "]\n```");

        List<PreguntaDocenteRequest> r = servicio.generar(new GenerarPreguntasRequest("algebra_lineal", "basico", 3, "determinantes"));

        assertThat(r).hasSize(1);
        assertThat(r.get(0).tema()).isEqualTo("algebra_lineal");
        assertThat(r.get(0).opciones()).extracting(PreguntaDocenteRequest.OpcionRequest::letra).containsExactly("A", "B", "C", "D");
        assertThat(r.get(0).pista()).isEqualTo("ad − bc");
    }

    @Test
    void siNadaSirveAvisa() {
        when(ia.generarRespuesta(anyString(), anyString(), anyString())).thenReturn("Lo siento, no puedo.");
        assertThatThrownBy(() -> servicio.generar(new GenerarPreguntasRequest("limites", "basico", 2, null)))
                .isInstanceOf(IaNoDisponibleException.class);
    }
}
