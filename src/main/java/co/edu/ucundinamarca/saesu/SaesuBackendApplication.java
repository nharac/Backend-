package co.edu.ucundinamarca.saesu;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // <-- coffe

@SpringBootApplication
@EnableCaching // <-- esto es para habilitar la caché en Spring Boot
// Las tareas programadas se activan en core/config/TareasProgramadasConfig (se pueden apagar por réplica)

public class SaesuBackendApplication {

    public static void main(String[] args) {
        // Cargar variables del .env ANTES de que Spring arranque
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        System.out.println("Variables de entorno cargadas desde .env");

        SpringApplication.run(SaesuBackendApplication.class, args);
    }
}