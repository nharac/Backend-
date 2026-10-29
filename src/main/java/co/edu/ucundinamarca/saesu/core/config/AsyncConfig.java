package co.edu.ucundinamarca.saesu.core.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

/**
 * Habilita @Async: los correos (bienvenida, nueva respuesta, recuperación de contraseña)
 * se envían en segundo plano para que el usuario no espere a Gmail.
 * Si un envío falla, Spring lo deja en el log sin afectar la petición.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Copia el requestId y el usuario de la petición al hilo que envía el correo.
     * Sin esto, las líneas de log del envío salían sin id y no se podían relacionar con la petición
     * que las originó. Spring Boot aplica este decorador al ejecutor de @Async automáticamente.
     */
    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return tarea -> {
            Map<String, String> contexto = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> previo = MDC.getCopyOfContextMap();
                if (contexto != null) {
                    MDC.setContextMap(contexto);
                }
                try {
                    tarea.run();
                } finally {
                    if (previo != null) {
                        MDC.setContextMap(previo);
                    } else {
                        MDC.clear();
                    }
                }
            };
        };
    }
}
