package co.edu.ucundinamarca.saesu.docente.adapters.output.excel;

import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.PreguntaDificil;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstadisticasGrupoResponse.TemaEstadistica;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse;
import co.edu.ucundinamarca.saesu.docente.application.dto.response.EstudianteResumenResponse.TemaProgreso;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteEstudiantesExcelAdapterTest {

    private static final LocalDateTime GENERADO = LocalDateTime.of(2026, 10, 30, 9, 15);

    private static EstudianteResumenResponse estudiante(String nombre, String estado, boolean enRiesgo, long intentos,
                                                        Double promedioQuiz, LocalDateTime ultima, Integer gracia,
                                                        List<String> motivos) {
        return new EstudianteResumenResponse(1L, nombre, nombre.toLowerCase().replace(' ', '.') + "@ucundinamarca.edu.co",
                null, 120, 3, intentos, intentos / 2, intentos == 0 ? 0 : 45.0, ultima, enRiesgo, motivos,
                List.of(new TemaProgreso("limites", intentos, intentos / 2, 85.0, 4, 10),
                        new TemaProgreso("derivadas", 0, 0, 0, 0, 10)),
                estado, gracia, 2, promedioQuiz);
    }

    private static EstadisticasGrupoResponse grupo(List<PreguntaDificil> dificiles) {
        return new EstadisticasGrupoResponse(3, 2, 40, 62.5, 1, 30,
                List.of(new TemaEstadistica("limites", 20, 15, 75, 2, 10),
                        new TemaEstadistica("integrales", 0, 0, 0, 0, 10)),
                dificiles, List.of());
    }

    @Test
    void generaLasDosHojasConLosEstudiantesEnRiesgoPrimero() throws IOException {
        List<EstudianteResumenResponse> estudiantes = List.of(
                estudiante("Zoe Díaz", "al-dia", false, 10, 90.0, GENERADO.minusDays(1), null, List.of()),
                estudiante("Ana Ruiz", "en-riesgo", true, 4, null, null, null, List.of("No practica hace 9 días", "Aciertos bajos")),
                estudiante("Nuevo Pérez", "nuevo", false, 0, null, null, 1, List.of()),
                estudiante("Otra Nueva", "nuevo", false, 0, 30.0, null, 3, List.of()));

        Map<String, String> partes = abrir(new ReporteEstudiantesExcelAdapter().generar(estudiantes,
                grupo(List.of(new PreguntaDificil(5L, "Deriva sen(x²)", "derivadas", "avanzado", 20, 15, 75, null),
                        new PreguntaDificil(6L, "Integra 1/x", "geometria", "basico", 10, 4, 40, "x"))),
                GENERADO));

        String hoja1 = partes.get("xl/worksheets/sheet1.xml");
        assertThat(hoja1).contains("4 estudiantes", "1 en riesgo", "2 nuevos")
                .contains("No practica hace 9 días; Aciertos bajos")
                .contains("Recién registrado: tiene 1 día para empezar", "tiene 3 días")
                .contains("En riesgo", "Al día", "Nuevo", "Nunca");
        assertThat(hoja1.indexOf("Ana Ruiz")).isLessThan(hoja1.indexOf("Zoe Díaz"));

        String hoja2 = partes.get("xl/worksheets/sheet2.xml");
        assertThat(hoja2).contains("Resumen del grupo", "Límites", "Integrales", "Preguntas que más fallan",
                "Deriva sen(x²)", "geometria");
    }

    @Test
    void unSoloEstudianteSinPreguntasDificiles() throws IOException {
        Map<String, String> partes = abrir(new ReporteEstudiantesExcelAdapter().generar(
                List.of(estudiante("Sol Mora", "al-dia", false, 8, 100.0, GENERADO, null, List.of())),
                grupo(List.of()), GENERADO));

        assertThat(partes.get("xl/worksheets/sheet1.xml")).contains("1 estudiante").doesNotContain("en riesgo");
        assertThat(partes.get("xl/worksheets/sheet2.xml")).doesNotContain("Preguntas que más fallan");
    }

    private static Map<String, String> abrir(byte[] xlsx) throws IOException {
        Map<String, String> partes = new HashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(xlsx))) {
            ZipEntry e;
            while ((e = zip.getNextEntry()) != null) {
                partes.put(e.getName(), new String(zip.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        return partes;
    }
}
