package co.edu.ucundinamarca.saesu.practica.adapters.output.ia;

import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Gemini sin red: el WebClient usa una función de intercambio falsa que devuelve las respuestas
 * en el orden en que se registran y guarda las peticiones que recibió.
 */
class GeminiAdapterTest {

    private final Deque<ClientResponse> respuestas = new ConcurrentLinkedDeque<>();
    private final List<ClientRequest> peticiones = new ArrayList<>();

    private GeminiAdapter adapter() {
        WebClient.Builder builder = WebClient.builder().exchangeFunction(req -> {
            peticiones.add(req);
            ClientResponse r = respuestas.poll();
            return r == null ? Mono.error(new IllegalStateException("sin red")) : Mono.just(r);
        });
        GeminiAdapter a = new GeminiAdapter(builder, new ObjectMapper());
        ReflectionTestUtils.setField(a, "apiKey", "clave-prueba");
        ReflectionTestUtils.setField(a, "modelo", "gemini-test");
        ReflectionTestUtils.setField(a, "baseUrl", "https://gemini.test/v1beta/models");
        return a;
    }

    private void responder(HttpStatus estado, String json) {
        respuestas.add(ClientResponse.create(estado)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(json).build());
    }

    @Test
    void uneLasPartesDeLaRespuestaYOmiteElRazonamientoInterno() {
        responder(HttpStatus.OK, """
                {"candidates":[{"finishReason":"STOP","content":{"parts":[
                  {"text":"pensando...","thought":true},{"text":"¿Qué pasa si "},{"text":"factorizas?"}]}}]}""");

        String r = adapter().generarRespuesta("Eres un tutor", "usuario: hola", "No entiendo el límite");

        assertThat(r).isEqualTo("¿Qué pasa si factorizas?");
        assertThat(peticiones).singleElement().satisfies(p ->
                assertThat(p.url().toString()).startsWith("https://gemini.test/v1beta/models/gemini-test:generateContent"));
    }

    @Test
    void avisaCuandoLaRespuestaSeCortaPorTokensPeroLaDevuelve() {
        responder(HttpStatus.OK, """
                {"candidates":[{"finishReason":"MAX_TOKENS","content":{"parts":[{"text":"Respuesta larga"}]}}]}""");
        assertThat(adapter().generarRespuesta("s", null, "m")).isEqualTo("Respuesta larga");
    }

    @Test
    void reintentaCuandoGeminiEstaSaturado() {
        responder(HttpStatus.SERVICE_UNAVAILABLE, "{\"error\":\"overloaded\"}");
        responder(HttpStatus.OK, "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Listo\"}]}}]}");

        assertThat(adapter().generarRespuesta("s", "", "m")).isEqualTo("Listo");
        assertThat(peticiones).hasSize(2);
    }

    @Test
    void modeloInexistenteOErrorDelServidorNoSeReintentan() {
        responder(HttpStatus.NOT_FOUND, "{}");
        GeminiAdapter a = adapter();
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m"))
                .isInstanceOf(IaNoDisponibleException.class).hasMessageContaining("modelo");

        responder(HttpStatus.BAD_REQUEST, "{}");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m"))
                .isInstanceOf(IaNoDisponibleException.class).hasMessageContaining("saturado");
        assertThat(peticiones).hasSize(2);
    }

    @Test
    void respuestasBloqueadasVaciasOInvalidas() {
        GeminiAdapter a = adapter();
        responder(HttpStatus.OK, "{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m")).hasMessageContaining("SAFETY");

        responder(HttpStatus.OK, "{\"candidates\":[]}");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m")).hasMessage("La IA no generó respuesta");

        responder(HttpStatus.OK, "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"   \"}]}}]}");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m")).hasMessageContaining("vacía");

        responder(HttpStatus.OK, "esto no es json");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m")).hasMessageContaining("inválida");

        responder(HttpStatus.OK, "");
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m")).hasMessageContaining("no devolvió");
    }

    @Test
    void sinConexionSeRindeDespuesDeTresIntentos() {
        GeminiAdapter a = adapter();
        assertThatThrownBy(() -> a.generarRespuesta("s", "", "m"))
                .isInstanceOf(IaNoDisponibleException.class).hasMessageContaining("3 intentos");
        assertThat(peticiones).hasSize(3);
    }
}
