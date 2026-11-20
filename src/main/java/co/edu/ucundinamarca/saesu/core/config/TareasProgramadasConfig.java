package co.edu.ucundinamarca.saesu.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Activa los @Scheduled (cierre de rachas, torneo semanal, recordatorios de tareas).
 * Con varias réplicas del backend se pone SAESU_TAREAS_PROGRAMADAS=false en todas menos una,
 * para que cada tarea corra una sola vez.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "saesu.tareas-programadas", havingValue = "true", matchIfMissing = true)
public class TareasProgramadasConfig {
}
