package co.edu.ucundinamarca.saesu.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Tiempo como dependencia (se puede fijar en las pruebas) y zona horaria como configuración (factor III).
 * <ul>
 *   <li>{@link Clock}: el reloj del servidor, en la misma zona en que la base guarda las fechas.</li>
 *   <li>{@link ZoneId}: la zona del negocio para decidir "qué día es" (reto del día, rachas, ranking).
 *       Se cambia con SAESU_ZONA_HORARIA sin recompilar.</li>
 * </ul>
 */
@Configuration
public class TiempoConfig {

    /** Zona que usa SAESU si no se configura otra. */
    public static final ZoneId ZONA_POR_DEFECTO = ZoneId.of("America/Bogota");

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ZoneId zonaSaesu(@Value("${saesu.zona-horaria:America/Bogota}") String zona) {
        // ZoneId.of solo acepta ids válidos (letras, números, / _ + -), así que es seguro usarlo en SQL
        return ZoneId.of(zona);
    }

    /** Marca que las consultas usan donde va la zona (se arman con constantes, nunca con datos del usuario). */
    public static final String MARCA_ZONA = "{zona}";

    /** Pone la zona en una consulta, p. ej. {@code AT TIME ZONE {zona}} → {@code AT TIME ZONE 'America/Bogota'}. */
    public static String conZona(String sql, ZoneId zona) {
        return sql.replace(MARCA_ZONA, "'" + zona.getId() + "'");
    }
}
