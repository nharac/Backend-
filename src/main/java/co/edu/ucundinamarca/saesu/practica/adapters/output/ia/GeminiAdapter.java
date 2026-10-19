package co.edu.ucundinamarca.saesu.practica.adapters.output.ia;

import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Adaptador que implementa IaServicePort usando la API de Google Gemini.
 * Con reintentos automáticos si Gemini devuelve 503 (sobrecarga) o 429 (cuota temporal).
 *
 * Las reglas del tutor se envían en "systemInstruction" (separadas del mensaje del
 * estudiante) y la respuesta se arma uniendo todas las partes de texto que devuelve Gemini.
 */
@Slf4j
@Component
public class GeminiAdapter implements IaServicePort {

    private static final int MAX_REINTENTOS = 3;
    private static final long ESPERA_BASE_MS = 1000; // 1s, 2s, 4s (backoff exponencial)

    /**
     * Límite de tokens de salida. Los modelos Gemini recientes "piensan" antes de
     * responder y ese razonamiento también cuenta aquí; con un valor bajo la
     * respuesta visible se corta a la mitad.
     */
    private static final int MAX_TOKENS_SALIDA = 2048;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.8-flash}")
    private String modelo;

    @Value("${gemini.api.url}")
    private String baseUrl;

    public GeminiAdapter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String generarRespuesta(String systemPrompt, String historial, String mensajeNuevo) {
        String conversacion = construirConversacion(historial, mensajeNuevo);

        Exception ultimaExcepcion = null;

        for (int intento = 1; intento <= MAX_REINTENTOS; intento++) {
            try {
                log.debug("Intento {}/{} de llamar a Gemini", intento, MAX_REINTENTOS);
                return llamarGemini(systemPrompt, conversacion);

            } catch (WebClientResponseException e) {
                int status = e.getStatusCode().value();
                log.warn("Gemini respondió {}: {}", status, e.getResponseBodyAsString());

                // Solo reintentamos en 503 (sobrecarga) o 429 (cuota temporal)
                if ((status == 503 || status == 429) && intento < MAX_REINTENTOS) {
                    long espera = ESPERA_BASE_MS * (long) Math.pow(2, intento - 1);
                    log.info("Reintentando en {} ms...", espera);
                    dormir(espera);
                    ultimaExcepcion = e;
                    continue;
                }

                // Si es 404 (modelo no existe), no reintentamos
                if (status == 404) {
                    throw new IaNoDisponibleException(
                            "El modelo de IA no está disponible. Contacta al administrador.", e);
                }

                throw new IaNoDisponibleException(
                        "El tutor IA está saturado. Intenta de nuevo en unos segundos.", e);

            } catch (IaNoDisponibleException e) {
                throw e;

            } catch (Exception e) {
                log.error("Error inesperado al llamar a Gemini", e);
                ultimaExcepcion = e;
                if (intento < MAX_REINTENTOS) {
                    dormir(ESPERA_BASE_MS * (long) Math.pow(2, intento - 1));
                    continue;
                }
            }
        }

        throw new IaNoDisponibleException(
                "No se pudo contactar al tutor IA después de " + MAX_REINTENTOS + " intentos.",
                ultimaExcepcion);
    }

    private String llamarGemini(String systemPrompt, String conversacion) {
        Map<String, Object> body = Map.of(
                // Reglas del tutor: van aparte para que el modelo no las "continúe" como texto
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", conversacion))
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", MAX_TOKENS_SALIDA,
                        "topP", 0.95
                )
        );

        String url = baseUrl + "/" + modelo + ":generateContent?key=" + apiKey;

        String respuestaJson = webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();

        if (respuestaJson == null || respuestaJson.isBlank()) {
            throw new IaNoDisponibleException("La IA no devolvió respuesta");
        }

        return extraerTextoDeRespuesta(respuestaJson);
    }

    /** Historial previo + mensaje nuevo del estudiante (las reglas van en systemInstruction). */
    private String construirConversacion(String historial, String mensajeNuevo) {
        StringBuilder sb = new StringBuilder();
        if (historial != null && !historial.isBlank()) {
            sb.append("--- CONVERSACIÓN PREVIA ---\n");
            sb.append(historial).append("\n");
            sb.append("--- FIN DE LA CONVERSACIÓN ---\n\n");
        }
        sb.append("Mensaje del estudiante:\n");
        sb.append(mensajeNuevo);
        return sb.toString();
    }

    private String extraerTextoDeRespuesta(String respuestaJson) {
        try {
            JsonNode root = objectMapper.readTree(respuestaJson);
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                JsonNode feedback = root.path("promptFeedback");
                String razon = feedback.path("blockReason").asText("");
                throw new IaNoDisponibleException(
                        "La IA no generó respuesta" + (razon.isBlank() ? "" : ": " + razon));
            }

            JsonNode candidato = candidates.get(0);
            String finishReason = candidato.path("finishReason").asText("");
            if ("MAX_TOKENS".equals(finishReason)) {
                log.warn("Gemini cortó la respuesta por límite de tokens (maxOutputTokens={})",
                        MAX_TOKENS_SALIDA);
            }

            // Gemini puede dividir la respuesta en varias partes: se unen todas.
            // Las partes marcadas como "thought" son razonamiento interno y se omiten.
            StringBuilder texto = new StringBuilder();
            for (JsonNode parte : candidato.path("content").path("parts")) {
                if (parte.path("thought").asBoolean(false)) {
                    continue;
                }
                texto.append(parte.path("text").asText(""));
            }

            String resultado = texto.toString().trim();
            if (resultado.isBlank()) {
                throw new IaNoDisponibleException("La IA devolvió una respuesta vacía");
            }
            return resultado;

        } catch (JsonProcessingException e) {
            log.error("Error parseando respuesta de Gemini: {}", respuestaJson, e);
            throw new IaNoDisponibleException("Respuesta de IA inválida", e);
        }
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
