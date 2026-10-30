package co.edu.ucundinamarca.saesu.core.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generador mínimo de archivos .xlsx (Office Open XML) sin librerías externas.
 * Un .xlsx es un ZIP con unos XML adentro; aquí se escriben solo las partes necesarias:
 * hojas con texto, números, porcentajes y fechas, estilos fijos, celdas combinadas,
 * encabezado inmovilizado y filtros.
 *
 * Los textos se guardan como "inline strings": Excel nunca los interpreta como fórmulas,
 * así que no hay riesgo de inyección (=HYPERLINK(...), +57..., etc.).
 */
public class LibroExcel {

    /** Estilos disponibles (el número es su posición en styles.xml). */
    public enum Estilo {
        NORMAL(0), TITULO(1), SUBTITULO(2), ENCABEZADO(3), TEXTO(4), ENTERO(5),
        PORCENTAJE(6), PORCENTAJE_BAJO(7), PORCENTAJE_ALTO(8), RIESGO(9), AL_DIA(10),
        FECHA(11), VACIO(12), ETIQUETA(13), PORCENTAJE_MEDIO(14), TEXTO_SUAVE(15), NUMERO_GRANDE(16), NUEVO(17);

        final int id;

        Estilo(int id) {
            this.id = id;
        }
    }

    /** Una celda: texto, número o fecha, con su estilo. */
    public record Celda(Object valor, Estilo estilo) {
        public static Celda texto(String v, Estilo e) { return new Celda(v, e); }
        public static Celda numero(double v, Estilo e) { return new Celda(v, e); }
        public static Celda fecha(LocalDateTime v, Estilo e) { return new Celda(v, e); }
        public static Celda vacia(Estilo e) { return new Celda(null, e); }
    }

    public static class Hoja {
        private final String nombre;
        private final List<List<Celda>> filas = new ArrayList<>();
        private final List<Double> altos = new ArrayList<>();
        private final List<String> combinadas = new ArrayList<>();
        private double[] anchos = new double[0];
        private int filasInmovilizadas;
        private String filtro;

        private Hoja(String nombre) {
            this.nombre = nombre;
        }

        public Hoja anchos(double... anchos) {
            this.anchos = anchos;
            return this;
        }

        /** Agrega una fila (alto null = automático) y devuelve su número (1, 2, 3...). */
        public int fila(Double alto, Celda... celdas) {
            filas.add(List.of(celdas));
            altos.add(alto);
            return filas.size();
        }

        public int fila(Celda... celdas) {
            return fila(null, celdas);
        }

        public Hoja combinar(int fila, int colDesde, int colHasta) {
            combinadas.add(ref(fila, colDesde) + ":" + ref(fila, colHasta));
            return this;
        }

        /** Deja fijas las primeras n filas al bajar con el scroll. */
        public Hoja inmovilizarFilas(int n) {
            this.filasInmovilizadas = n;
            return this;
        }

        /** Botones de filtro en la fila de encabezados, hasta la última fila escrita. */
        public Hoja filtro(int filaEncabezado, int columnas) {
            this.filtro = ref(filaEncabezado, 1) + ":" + ref(Math.max(filaEncabezado, filas.size()), columnas);
            return this;
        }
    }

    private final List<Hoja> hojas = new ArrayList<>();

    public Hoja hoja(String nombre) {
        Hoja h = new Hoja(nombre.length() > 31 ? nombre.substring(0, 31) : nombre);
        hojas.add(h);
        return h;
    }

    public byte[] generar() {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(salida, StandardCharsets.UTF_8)) {
            escribir(zip, "[Content_Types].xml", tiposDeContenido());
            escribir(zip, "_rels/.rels", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                    </Relationships>""");
            escribir(zip, "xl/workbook.xml", libro());
            escribir(zip, "xl/_rels/workbook.xml.rels", relacionesLibro());
            escribir(zip, "xl/styles.xml", ESTILOS);
            for (int i = 0; i < hojas.size(); i++) {
                escribir(zip, "xl/worksheets/sheet" + (i + 1) + ".xml", hoja(hojas.get(i)));
            }
            zip.finish();
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el archivo de Excel", e);
        }
    }

    // ---------------------------------------------------------------

    private static void escribir(ZipOutputStream zip, String ruta, String xml) throws IOException {
        zip.putNextEntry(new ZipEntry(ruta));
        zip.write(xml.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String tiposDeContenido() {
        StringBuilder sb = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                <Default Extension="xml" ContentType="application/xml"/>
                <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                """);
        for (int i = 1; i <= hojas.size(); i++) {
            sb.append("<Override PartName=\"/xl/worksheets/sheet").append(i)
              .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
        }
        return sb.append("</Types>").toString();
    }

    private String libro() {
        StringBuilder sb = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                <sheets>""");
        for (int i = 0; i < hojas.size(); i++) {
            sb.append("<sheet name=\"").append(xml(hojas.get(i).nombre)).append("\" sheetId=\"").append(i + 1)
              .append("\" r:id=\"rId").append(i + 1).append("\"/>");
        }
        sb.append("</sheets>");
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < hojas.size(); i++) {
            Hoja h = hojas.get(i);
            if (h.filtro != null) {
                String[] partes = h.filtro.split(":");
                nombres.append("<definedName name=\"_xlnm._FilterDatabase\" localSheetId=\"").append(i)
                       .append("\" hidden=\"1\">'").append(xml(h.nombre.replace("'", "''"))).append("'!")
                       .append(absoluta(partes[0])).append(':').append(absoluta(partes[1])).append("</definedName>");
            }
        }
        if (!nombres.isEmpty()) {
            sb.append("<definedNames>").append(nombres).append("</definedNames>");
        }
        return sb.append("</workbook>").toString();
    }

    private String relacionesLibro() {
        StringBuilder sb = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""");
        int i = 1;
        for (; i <= hojas.size(); i++) {
            sb.append("<Relationship Id=\"rId").append(i)
              .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
              .append(i).append(".xml\"/>");
        }
        sb.append("<Relationship Id=\"rId").append(i)
          .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>");
        return sb.append("</Relationships>").toString();
    }

    private static String hoja(Hoja h) {
        StringBuilder sb = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""");
        sb.append("<sheetPr><pageSetUpPr fitToPage=\"1\"/></sheetPr>");
        sb.append("<sheetViews><sheetView workbookViewId=\"0\" showGridLines=\"0\">");
        if (h.filasInmovilizadas > 0) {
            sb.append("<pane ySplit=\"").append(h.filasInmovilizadas).append("\" topLeftCell=\"A")
              .append(h.filasInmovilizadas + 1).append("\" activePane=\"bottomLeft\" state=\"frozen\"/>");
        }
        sb.append("</sheetView></sheetViews><sheetFormatPr defaultRowHeight=\"18\"/>");
        if (h.anchos.length > 0) {
            sb.append("<cols>");
            for (int i = 0; i < h.anchos.length; i++) {
                sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
                  .append("\" width=\"").append(h.anchos[i]).append("\" customWidth=\"1\"/>");
            }
            sb.append("</cols>");
        }
        sb.append("<sheetData>");
        for (int f = 0; f < h.filas.size(); f++) {
            int numero = f + 1;
            Double alto = h.altos.get(f);
            sb.append("<row r=\"").append(numero).append('"');
            if (alto != null) {
                sb.append(" ht=\"").append(alto).append("\" customHeight=\"1\"");
            }
            sb.append('>');
            List<Celda> celdas = h.filas.get(f);
            for (int c = 0; c < celdas.size(); c++) {
                celda(sb, ref(numero, c + 1), celdas.get(c));
            }
            sb.append("</row>");
        }
        sb.append("</sheetData>");
        if (h.filtro != null) {
            sb.append("<autoFilter ref=\"").append(h.filtro).append("\"/>");
        }
        if (!h.combinadas.isEmpty()) {
            sb.append("<mergeCells count=\"").append(h.combinadas.size()).append("\">");
            h.combinadas.forEach(r -> sb.append("<mergeCell ref=\"").append(r).append("\"/>"));
            sb.append("</mergeCells>");
        }
        sb.append("<pageMargins left=\"0.5\" right=\"0.5\" top=\"0.6\" bottom=\"0.6\" header=\"0.3\" footer=\"0.3\"/>");
        sb.append("<pageSetup orientation=\"landscape\" fitToWidth=\"1\" fitToHeight=\"0\"/>");
        return sb.append("</worksheet>").toString();
    }

    private static void celda(StringBuilder sb, String ref, Celda c) {
        int s = c.estilo() == null ? 0 : c.estilo().id;
        Object v = c.valor();
        if (v == null) {
            sb.append("<c r=\"").append(ref).append("\" s=\"").append(s).append("\"/>");
        } else if (v instanceof Number n) {
            sb.append("<c r=\"").append(ref).append("\" s=\"").append(s).append("\"><v>")
              .append(numero(n.doubleValue())).append("</v></c>");
        } else if (v instanceof LocalDateTime fecha) {
            sb.append("<c r=\"").append(ref).append("\" s=\"").append(s).append("\"><v>")
              .append(numero(serialExcel(fecha))).append("</v></c>");
        } else {
            sb.append("<c r=\"").append(ref).append("\" s=\"").append(s).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
              .append(xml(v.toString())).append("</t></is></c>");
        }
    }

    /** Fecha como número de días desde el 30/12/1899 (así guarda Excel las fechas). */
    static double serialExcel(LocalDateTime fecha) {
        // Excel no maneja zonas horarias: se calcula en UTC para que no influya ningún cambio de hora
        OffsetDateTime base = LocalDateTime.of(1899, 12, 30, 0, 0).atOffset(ZoneOffset.UTC);
        return ChronoUnit.SECONDS.between(base, fecha.atOffset(ZoneOffset.UTC)) / 86400.0;
    }

    private static String numero(double d) {
        return d == Math.rint(d) && Math.abs(d) < 1e15 ? String.valueOf((long) d) : String.valueOf(d);
    }

    /** (1, 1) → "A1"; (3, 28) → "AB3". */
    static String ref(int fila, int columna) {
        StringBuilder letras = new StringBuilder();
        int c = columna;
        while (c > 0) {
            int resto = (c - 1) % 26;
            letras.insert(0, (char) ('A' + resto));
            c = (c - 1) / 26;
        }
        return letras.toString() + fila;
    }

    private static String absoluta(String ref) {
        int i = 0;
        while (i < ref.length() && Character.isLetter(ref.charAt(i))) i++;
        return "$" + ref.substring(0, i) + "$" + ref.substring(i);
    }

    /** Escapa para XML y quita caracteres de control que el formato no permite. */
    static String xml(String texto) {
        StringBuilder sb = new StringBuilder(texto.length());
        for (char ch : texto.toCharArray()) {
            switch (ch) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                default -> {
                    if (ch >= 0x20 || ch == '\t' || ch == '\n' || ch == '\r') sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    // Fuentes, rellenos, bordes y formatos. El orden de <xf> en cellXfs = Estilo.id.
    private static final String ESTILOS = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
            <numFmts count="3">
              <numFmt numFmtId="164" formatCode="0.0%"/>
              <numFmt numFmtId="165" formatCode="dd/mm/yyyy hh:mm"/>
              <numFmt numFmtId="166" formatCode="#,##0"/>
            </numFmts>
            <fonts count="11">
              <font><sz val="11"/><color rgb="FF1F2F3F"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="18"/><color rgb="FF4F46E5"/><name val="Calibri"/><family val="2"/></font>
              <font><sz val="10"/><color rgb="FF6B7280"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FFFFFFFF"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FFB91C1C"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FF047857"/><name val="Calibri"/><family val="2"/></font>
              <font><sz val="11"/><color rgb="FF9CA3AF"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FF1F2F3F"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FFB45309"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="16"/><color rgb="FF1F2F3F"/><name val="Calibri"/><family val="2"/></font>
              <font><b/><sz val="11"/><color rgb="FF4338CA"/><name val="Calibri"/><family val="2"/></font>
            </fonts>
            <fills count="8">
              <fill><patternFill patternType="none"/></fill>
              <fill><patternFill patternType="gray125"/></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FF4F46E5"/><bgColor indexed="64"/></patternFill></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FFFEE2E2"/><bgColor indexed="64"/></patternFill></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FFD1FAE5"/><bgColor indexed="64"/></patternFill></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FFFEF3C7"/><bgColor indexed="64"/></patternFill></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FFEEF2FF"/><bgColor indexed="64"/></patternFill></fill>
              <fill><patternFill patternType="solid"><fgColor rgb="FFF8FAFC"/><bgColor indexed="64"/></patternFill></fill>
            </fills>
            <borders count="2">
              <border><left/><right/><top/><bottom/><diagonal/></border>
              <border><left/><right/><top/><bottom style="thin"><color rgb="FFE5E7EB"/></bottom><diagonal/></border>
            </borders>
            <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
            <cellXfs count="18">
              <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
              <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1" applyAlignment="1"><alignment vertical="center"/></xf>
              <xf numFmtId="0" fontId="2" fillId="0" borderId="0" xfId="0" applyFont="1" applyAlignment="1"><alignment vertical="top"/></xf>
              <xf numFmtId="0" fontId="3" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1" applyAlignment="1"><alignment horizontal="center" vertical="center" wrapText="1"/></xf>
              <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1"><alignment vertical="center"/></xf>
              <xf numFmtId="166" fontId="0" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="164" fontId="0" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="164" fontId="4" fillId="3" borderId="1" xfId="0" applyNumberFormat="1" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="164" fontId="5" fillId="4" borderId="1" xfId="0" applyNumberFormat="1" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="4" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="5" fillId="4" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="165" fontId="0" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="6" fillId="0" borderId="1" xfId="0" applyFont="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="7" fillId="6" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment vertical="center"/></xf>
              <xf numFmtId="164" fontId="8" fillId="5" borderId="1" xfId="0" applyNumberFormat="1" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="2" fillId="0" borderId="1" xfId="0" applyFont="1" applyBorder="1" applyAlignment="1"><alignment vertical="center" wrapText="1"/></xf>
              <xf numFmtId="166" fontId="9" fillId="0" borderId="1" xfId="0" applyNumberFormat="1" applyFont="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
              <xf numFmtId="0" fontId="10" fillId="6" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
            </cellXfs>
            <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
            </styleSheet>""";
}
