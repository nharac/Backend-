package co.edu.ucundinamarca.saesu.core.excel;

import co.edu.ucundinamarca.saesu.core.excel.LibroExcel.Celda;
import co.edu.ucundinamarca.saesu.core.excel.LibroExcel.Estilo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class LibroExcelTest {

    @Test
    @DisplayName("Referencias de celda estilo Excel (A1, Z1, AA1, AB3)")
    void referencias() {
        assertThat(LibroExcel.ref(1, 1)).isEqualTo("A1");
        assertThat(LibroExcel.ref(1, 26)).isEqualTo("Z1");
        assertThat(LibroExcel.ref(1, 27)).isEqualTo("AA1");
        assertThat(LibroExcel.ref(3, 28)).isEqualTo("AB3");
    }

    @Test
    @DisplayName("Las fechas se guardan como número de días desde 1899-12-30")
    void fechas() {
        assertThat(LibroExcel.serialExcel(LocalDateTime.of(2026, 10, 28, 12, 0))).isEqualTo(46323.5);
    }

    @Test
    @DisplayName("El texto se escapa para XML y nunca queda como fórmula")
    void textoSeguro() throws IOException {
        LibroExcel libro = new LibroExcel();
        libro.hoja("Prueba").fila(Celda.texto("=HYPERLINK(\"x\") <b> & \u0001", Estilo.TEXTO), Celda.numero(0.5, Estilo.PORCENTAJE));

        Map<String, String> partes = abrir(libro.generar());

        assertThat(partes).containsKeys("[Content_Types].xml", "xl/workbook.xml", "xl/styles.xml", "xl/worksheets/sheet1.xml");
        String hoja = partes.get("xl/worksheets/sheet1.xml");
        assertThat(hoja).contains("t=\"inlineStr\"", "=HYPERLINK(&quot;x&quot;) &lt;b&gt; &amp; ")
                .doesNotContain("<f>", "\u0001")
                .contains("<v>0.5</v>");
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
