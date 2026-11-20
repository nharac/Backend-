package co.edu.ucundinamarca.saesu.docente.application.service;

import co.edu.ucundinamarca.saesu.docente.application.dto.request.GenerarPreguntasRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest;
import co.edu.ucundinamarca.saesu.docente.application.dto.request.PreguntaDocenteRequest.OpcionRequest;
import co.edu.ucundinamarca.saesu.practica.domain.exception.IaNoDisponibleException;
import co.edu.ucundinamarca.saesu.practica.domain.valueobject.Tema;
import co.edu.ucundinamarca.saesu.practica.ports.output.IaServicePort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Borradores de preguntas generados con IA para el banco del docente.
 * No se guarda nada: el docente revisa cada borrador y decide cuál publicar (con el formulario normal).
 * Se descartan los que no tienen exactamente 4 opciones y una sola correcta.
 */
@Service
public class GenerarPreguntasService {

    private static final Logger log = LoggerFactory.getLogger(GenerarPreguntasService.class);

    static final String PROMPT = """
            Eres un docente universitario de matemáticas en Colombia que escribe preguntas de selección múltiple tipo ICFES.
            Escribe en español claro y sencillo. Usa notación con caracteres Unicode (x², √, π, ∫, matrices como [a b; c d]),
            nunca LaTeX. Cada pregunta tiene exactamente 4 opciones (A, B, C, D) y UNA sola correcta; las incorrectas deben ser
            errores típicos de estudiantes. Verifica tus cálculos dos veces antes de responder.
            Responde SOLO con un arreglo JSON válido, sin texto antes ni después y sin bloques ```:
            [{"enunciado": "...", "pista": "una ayuda corta que no da la respuesta",
              "opciones": [{"letra": "A", "texto": "...", "correcta": true}, {"letra": "B", "texto": "...", "correcta": false},
                           {"letra": "C", "texto": "...", "correcta": false}, {"letra": "D", "texto": "...", "correcta": false}]}]
            """;

    private final IaServicePort ia;
    private final ObjectMapper json;

    public GenerarPreguntasService(IaServicePort ia, ObjectMapper json) {
        this.ia = ia;
        this.json = json;
    }

    public List<PreguntaDocenteRequest> generar(GenerarPreguntasRequest r) {
        String pedido = "Genera " + r.cantidad() + " pregunta(s) de " + Tema.nombreDe(r.tema()) + ", nivel " + r.dificultad() + "."
                + (r.enfoque() == null || r.enfoque().isBlank() ? "" : " Enfócalas en: " + r.enfoque().trim() + ".");
        String respuesta = ia.generarRespuesta(PROMPT, "", pedido);
        List<PreguntaDocenteRequest> borradores = interpretar(respuesta, r.tema(), r.dificultad());
        if (borradores.isEmpty()) {
            throw new IaNoDisponibleException("La IA no devolvió preguntas válidas. Intenta de nuevo.");
        }
        return borradores.size() > r.cantidad() ? borradores.subList(0, r.cantidad()) : borradores;
    }

    /** Convierte la respuesta de la IA en borradores válidos (ignora los mal formados). */
    List<PreguntaDocenteRequest> interpretar(String respuesta, String tema, String dificultad) {
        List<PreguntaDocenteRequest> out = new ArrayList<>();
        if (respuesta == null) return out;
        String texto = respuesta.trim();
        int ini = texto.indexOf('[');
        int fin = texto.lastIndexOf(']');
        if (ini < 0 || fin <= ini) return out;
        try {
            for (JsonNode p : json.readTree(texto.substring(ini, fin + 1))) {
                String enunciado = p.path("enunciado").asText("").trim();
                JsonNode ops = p.path("opciones");
                if (enunciado.isEmpty() || !ops.isArray() || ops.size() != 4) continue;
                List<OpcionRequest> opciones = new ArrayList<>();
                int correctas = 0;
                for (int i = 0; i < 4; i++) {
                    JsonNode o = ops.get(i);
                    String txt = o.path("texto").asText("").trim();
                    boolean ok = o.path("correcta").asBoolean(false);
                    if (txt.isEmpty()) break;
                    if (ok) correctas++;
                    opciones.add(new OpcionRequest(String.valueOf("ABCD".charAt(i)), recortar(txt, 500), ok));
                }
                if (opciones.size() != 4 || correctas != 1) continue;
                String pista = p.path("pista").asText("").trim();
                out.add(new PreguntaDocenteRequest(tema, recortar(enunciado, 2000), dificultad,
                        pista.isEmpty() ? null : recortar(pista, 1000), opciones));
            }
        } catch (Exception e) {
            log.warn("La IA devolvió un JSON que no se pudo leer: {}", e.getMessage());
        }
        return out;
    }

    private static String recortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
