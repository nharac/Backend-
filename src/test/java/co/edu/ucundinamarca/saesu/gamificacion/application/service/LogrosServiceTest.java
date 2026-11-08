package co.edu.ucundinamarca.saesu.gamificacion.application.service;

import co.edu.ucundinamarca.saesu.gamificacion.application.dto.response.LogroResponse;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.CategoriaLogro;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Cifra;
import co.edu.ucundinamarca.saesu.gamificacion.domain.model.Logro;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort;
import co.edu.ucundinamarca.saesu.gamificacion.ports.output.LogrosPort.Estadisticas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/** Reglas de los 44 logros (43 ganables y uno exclusivo). Sin base de datos: el repositorio es un mock. */
@ExtendWith(MockitoExtension.class)
class LogrosServiceTest {

    private static final Long U = 5L;

    private static final String DEV = "jfranciscolozano@ucundinamarca.edu.co";

    @Mock private LogrosPort repo;
    private LogrosService servicio;
    /** Correo del estudiante en las estadísticas. */
    private String correo = "ana@ucundinamarca.edu.co";

    @org.junit.jupiter.api.BeforeEach
    void crear() {
        servicio = new LogrosService(repo, null, " nechaves@ucundinamarca.edu.co , " + DEV.toUpperCase() + ",, ");
    }

    /** Cifras del estudiante; cada prueba cambia solo lo que necesita. */
    private final Map<Cifra, Long> cifras = new EnumMap<>(Cifra.class);
    private final Map<String, long[]> temas = new HashMap<>(Map.of(
            "limites", new long[]{20, 0}, "derivadas", new long[]{20, 0}, "integrales", new long[]{20, 0}));

    private void estudiante(Map<String, OffsetDateTime> yaTiene) {
        lenient().when(repo.obtenidos(U)).thenReturn(yaTiene);
        lenient().when(repo.estadisticas(U)).thenAnswer(inv -> new Estadisticas(cifras, temas, correo));
        lenient().when(repo.guardar(eq(U), anyString())).thenReturn(true);
    }

    private List<String> nuevos() {
        return servicio.evaluar(U).stream().map(LogroResponse::getCodigo).toList();
    }

    private LogroResponse buscar(List<LogroResponse> lista, String codigo) {
        return lista.stream().filter(l -> l.getCodigo().equals(codigo)).findFirst().orElseThrow();
    }

    @Test
    @DisplayName("El catálogo tiene 44 logros, 12 retos especiales, uno exclusivo y nombres únicos")
    void catalogo() {
        assertThat(Logro.values()).hasSize(44);
        assertThat(LogrosService.GANABLES).hasSize(43);
        assertThat(Arrays.stream(Logro.values()).filter(l -> l.getCategoria() == CategoriaLogro.RETOS)).hasSize(12);
        assertThat(Arrays.stream(Logro.values()).filter(Logro::esExclusivo)).containsExactly(Logro.DESARROLLADOR);
        assertThat(new HashSet<>(Arrays.stream(Logro.values()).map(Logro::getNombre).toList())).hasSize(44);
        // Los códigos de la primera versión siguen existiendo (ya están guardados en usuario_logros)
        assertThat(Arrays.stream(Logro.values()).map(Logro::name)).contains(
                "PRIMER_ACIERTO", "A_LA_PRIMERA", "DOMINIO_LIMITES", "DOMINIO_DERIVADAS", "DOMINIO_INTEGRALES",
                "RACHA_3", "RACHA_7", "CURIOSO", "COLABORADOR", "RESPUESTA_DE_ORO", "PUNTOS_500");
    }

    @Test
    @DisplayName("Un estudiante sin actividad no desbloquea nada")
    void sinActividad() {
        estudiante(new HashMap<>());

        assertThat(nuevos()).isEmpty();
        verify(repo, never()).guardar(anyLong(), anyString());
    }

    @Test
    @DisplayName("La primera respuesta correcta desbloquea 'Primer paso' y lo guarda")
    void primerAcierto() {
        estudiante(new HashMap<>());
        cifras.put(Cifra.ACIERTOS, 1L);
        cifras.put(Cifra.RESPONDIDAS, 1L);

        List<LogroResponse> nuevos = servicio.evaluar(U);

        assertThat(nuevos).extracting(LogroResponse::getCodigo).containsExactly("PRIMER_ACIERTO");
        assertThat(nuevos.get(0).isObtenido()).isTrue();
        assertThat(nuevos.get(0).getProgreso()).isEqualTo(1);
        verify(repo).guardar(U, "PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Un logro que ya tiene no se vuelve a guardar ni a anunciar")
    void yaObtenido() {
        estudiante(new HashMap<>(Map.of("PRIMER_ACIERTO", OffsetDateTime.now())));
        cifras.put(Cifra.ACIERTOS, 3L);

        assertThat(nuevos()).isEmpty();
        verify(repo, never()).guardar(U, "PRIMER_ACIERTO");
    }

    @Test
    @DisplayName("Si guardar devuelve false (otra petición lo guardó primero) no se anuncia")
    void carrera() {
        estudiante(new HashMap<>());
        lenient().when(repo.guardar(U, "PRIMER_ACIERTO")).thenReturn(false);
        cifras.put(Cifra.ACIERTOS, 1L);

        assertThat(nuevos()).isEmpty();
    }

    @Test
    @DisplayName("Umbrales de práctica, rachas, quiz, comunidad y puntos")
    void umbrales() {
        estudiante(new HashMap<>());
        cifras.putAll(Map.of(Cifra.ACIERTOS, 100L, Cifra.PRIMERA, 10L, Cifra.RACHA, 7L, Cifra.QUICES, 1L,
                Cifra.PUBLICACIONES, 1L, Cifra.RESPUESTAS_FORO, 9L, Cifra.OFICIALES, 1L, Cifra.MENSAJES_TUTOR, 10L,
                Cifra.PUNTOS, 500L, Cifra.AVANZADAS, 9L));

        assertThat(nuevos())
                .contains("PRIMER_ACIERTO", "ACIERTOS_25", "ACIERTOS_100", "A_LA_PRIMERA", "RACHA_3", "RACHA_7",
                        "QUIZ_1", "CURIOSO", "RESPUESTA_DE_ORO", "CHARLA_CON_SUMI", "PUNTOS_100", "PUNTOS_500")
                .doesNotContain("ACIERTOS_250", "RACHA_15", "COLABORADOR", "MENTOR", "PUNTOS_1000", "AVANZADAS_10",
                        "QUIZ_10");
    }

    @Test
    @DisplayName("Temas: explorador, aprendiz y dominio (todas las preguntas del tema)")
    void temas() {
        estudiante(new HashMap<>());
        temas.put("limites", new long[]{20, 20});    // todas
        temas.put("derivadas", new long[]{20, 12});  // 12 de 20
        temas.put("integrales", new long[]{20, 1});

        assertThat(nuevos())
                .contains("EXPLORADOR", "APRENDIZ_LIMITES", "APRENDIZ_DERIVADAS", "DOMINIO_LIMITES")
                .doesNotContain("APRENDIZ_INTEGRALES", "DOMINIO_DERIVADAS", "TRIPLE_CORONA");
    }

    @Test
    @DisplayName("Un tema sin preguntas no se puede dominar ni cuenta para la triple corona")
    void temaVacio() {
        estudiante(new HashMap<>());
        temas.put("limites", new long[]{20, 20});
        temas.put("derivadas", new long[]{20, 20});
        temas.put("integrales", new long[]{0, 0});

        assertThat(nuevos()).contains("DOMINIO_LIMITES", "DOMINIO_DERIVADAS")
                .doesNotContain("DOMINIO_INTEGRALES", "TRIPLE_CORONA");
    }

    @Test
    @DisplayName("Triple corona: dominar los tres temas")
    void tripleCorona() {
        estudiante(new HashMap<>());
        temas.replaceAll((t, v) -> new long[]{20, 20});

        assertThat(nuevos()).contains("DOMINIO_LIMITES", "DOMINIO_DERIVADAS", "DOMINIO_INTEGRALES", "TRIPLE_CORONA");
    }

    @Test
    @DisplayName("Precisión quirúrgica: 50 respuestas y al menos 80 % de acierto")
    void precision() {
        estudiante(new HashMap<>());
        cifras.put(Cifra.RESPONDIDAS, 50L);
        cifras.put(Cifra.ACIERTOS, 39L); // 78 %
        assertThat(nuevos()).doesNotContain("PRECISION_80");

        cifras.put(Cifra.ACIERTOS, 40L); // 80 %
        assertThat(nuevos()).contains("PRECISION_80");
    }

    @Test
    @DisplayName("Retos especiales: umbrales muy altos (50 seguidas, 100 en un día, 30 de noche...)")
    void retos() {
        estudiante(new HashMap<>());
        cifras.putAll(Map.of(Cifra.MAX_SEGUIDAS, 49L, Cifra.MAX_ACIERTOS_DIA, 100L, Cifra.RACHA, 60L,
                Cifra.NOCTURNAS, 30L, Cifra.MADRUGADAS, 29L, Cifra.QUICES_PERFECTOS, 5L));

        assertThat(nuevos()).contains("MARATON", "SIN_DESCANSO", "BUHO_NOCTURNO", "CERO_ERRORES")
                .doesNotContain("EN_LA_ZONA", "MADRUGADOR", "DESARROLLADOR");
    }

    @Test
    @DisplayName("Matemático (todo el banco) y Genio del cálculo (todas las avanzadas) dependen del banco")
    void matematicoYGenio() {
        estudiante(new HashMap<>());
        temas.replaceAll((t, v) -> new long[]{20, 20});
        cifras.put(Cifra.AVANZADAS, 14L);
        cifras.put(Cifra.TOTAL_AVANZADAS, 15L);

        List<LogroResponse> todos = servicio.listar(U);
        assertThat(buscar(todos, "MATEMATICO").isObtenido()).isTrue();
        assertThat(buscar(todos, "MATEMATICO").getMeta()).isEqualTo(60);
        LogroResponse genio = buscar(todos, "GENIO_DEL_CALCULO");
        assertThat(genio.isObtenido()).isFalse();
        assertThat(genio.getProgreso()).isEqualTo(14);
        assertThat(genio.getMeta()).isEqualTo(15);
    }

    @Test
    @DisplayName("Francotirador: 200 respuestas y al menos 95 % de acierto")
    void francotirador() {
        estudiante(new HashMap<>());
        cifras.put(Cifra.RESPONDIDAS, 200L);
        cifras.put(Cifra.ACIERTOS, 189L); // 94,5 %
        assertThat(nuevos()).contains("PRECISION_80").doesNotContain("FRANCOTIRADOR");

        cifras.put(Cifra.ACIERTOS, 190L); // 95 %
        assertThat(nuevos()).contains("FRANCOTIRADOR");
    }

    @Test
    @DisplayName("Desarrollador: solo los correos configurados (sin importar mayúsculas) y solo ellos lo ven")
    void desarrollador() {
        estudiante(new HashMap<>());
        assertThat(servicio.listar(U)).extracting(LogroResponse::getCodigo).doesNotContain("DESARROLLADOR").hasSize(43);
        verify(repo, never()).guardar(U, "DESARROLLADOR");

        correo = DEV;
        List<LogroResponse> todos = servicio.listar(U);
        LogroResponse dev = buscar(todos, "DESARROLLADOR");
        assertThat(dev.isObtenido()).isTrue();
        assertThat(dev.getRareza()).isEqualTo("EXCLUSIVO");
        assertThat(todos).hasSize(44);
        assertThat(buscar(todos, "COLECCIONISTA").getProgreso()).as("el exclusivo no cuenta").isZero();
    }

    @Test
    @DisplayName("Sin desarrolladores configurados nadie recibe la insignia")
    void sinDesarrolladores() {
        servicio = new LogrosService(repo, null, null);
        correo = DEV;
        estudiante(new HashMap<>());
        assertThat(nuevos()).doesNotContain("DESARROLLADOR");
    }

    @Test
    @DisplayName("Leyenda de SAESU: todos los demás logros, contando los de la misma evaluación")
    void coleccionista() {
        Map<String, OffsetDateTime> tiene = new HashMap<>();
        LogrosService.GANABLES.stream()
                .filter(l -> l != Logro.MADRUGADOR && l != Logro.BUHO_NOCTURNO && l != Logro.COLECCIONISTA)
                .forEach(l -> tiene.put(l.name(), OffsetDateTime.now()));
        tiene.put("CODIGO_VIEJO", OffsetDateTime.now()); // un código que ya no existe no cuenta
        estudiante(tiene);
        cifras.put(Cifra.MADRUGADAS, 30L);
        cifras.put(Cifra.NOCTURNAS, 30L);

        assertThat(nuevos()).containsExactly("BUHO_NOCTURNO", "MADRUGADOR", "COLECCIONISTA");
    }

    @Test
    @DisplayName("listar trae el avance, la categoría y la rareza de cada logro")
    void listarConAvance() {
        estudiante(new HashMap<>(Map.of("CURIOSO", OffsetDateTime.now())));
        cifras.put(Cifra.ACIERTOS, 60L);
        cifras.put(Cifra.RACHA, 2L);
        temas.put("integrales", new long[]{20, 7});

        List<LogroResponse> todos = servicio.listar(U);

        assertThat(todos).hasSize(43);
        LogroResponse cien = buscar(todos, "ACIERTOS_100");
        assertThat(cien.isObtenido()).isFalse();
        assertThat(cien.getProgreso()).isEqualTo(60);
        assertThat(cien.getMeta()).isEqualTo(100);
        assertThat(cien.getCategoria()).isEqualTo("PRACTICA");
        assertThat(cien.getRareza()).isEqualTo("RARO");
        assertThat(buscar(todos, "ACIERTOS_25").getProgreso()).as("no pasa de la meta").isEqualTo(25);
        assertThat(buscar(todos, "DOMINIO_INTEGRALES").getMeta()).as("meta = preguntas del tema").isEqualTo(20);
        assertThat(buscar(todos, "DOMINIO_INTEGRALES").getProgreso()).isEqualTo(7);
        assertThat(buscar(todos, "RACHA_3").getProgreso()).isEqualTo(2);
        assertThat(buscar(todos, "CURIOSO").isObtenido()).isTrue();
        assertThat(buscar(todos, "CURIOSO").getProgreso()).isEqualTo(1);
        assertThat(buscar(todos, "COLECCIONISTA").getProgreso()).as("Curioso + los 2 de 60 aciertos").isEqualTo(3);
        assertThat(buscar(todos, "COLECCIONISTA").getMeta()).isEqualTo(42);
        verify(repo, times(1)).estadisticas(U);
    }

    @Test
    @DisplayName("Un logro secreto sin obtener muestra solo su pista; obtenido, su nombre real")
    void secretos() {
        estudiante(new HashMap<>(Map.of("MADRUGADOR", OffsetDateTime.now())));

        List<LogroResponse> todos = servicio.listar(U);

        LogroResponse buho = buscar(todos, "BUHO_NOCTURNO");
        assertThat(buho.isSecreto()).isTrue();
        assertThat(buho.getNombre()).isEqualTo("???");
        assertThat(buho.getDescripcion()).isEqualTo(Logro.BUHO_NOCTURNO.getPistaSecreta());
        assertThat(buho.getIcono()).isEqualTo("lock");
        assertThat(buscar(todos, "MADRUGADOR").getNombre()).isEqualTo("Madrugador");
    }

    @Test
    @DisplayName("Si ya tiene todos los ganables no vuelve a calcular nada")
    void todosObtenidos() {
        Map<String, OffsetDateTime> todos = new HashMap<>();
        for (Logro l : LogrosService.GANABLES) todos.put(l.name(), OffsetDateTime.now());
        estudiante(todos);

        List<LogroResponse> lista = servicio.listar(U);

        assertThat(lista).allMatch(LogroResponse::isObtenido)
                .allMatch(l -> l.getProgreso() == l.getMeta());
        assertThat(servicio.evaluar(U)).isEmpty();
        verify(repo, never()).estadisticas(anyLong());
    }
}
