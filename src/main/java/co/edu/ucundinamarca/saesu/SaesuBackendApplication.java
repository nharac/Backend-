package co.edu.ucundinamarca.saesu;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // <-- coffe
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication
@EnableCaching // <-- esto es para habilitar la caché en Spring Boot
@EnableScheduling // <-- esto es para habilitar la programación de tareas en Spring Boot

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