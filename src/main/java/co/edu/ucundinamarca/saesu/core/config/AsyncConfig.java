package co.edu.ucundinamarca.saesu.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita @Async: los correos (bienvenida, nueva respuesta, recuperación de contraseña)
 * se envían en segundo plano para que el usuario no espere a Gmail.
 * Si un envío falla, Spring lo deja en el log sin afectar la petición.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
