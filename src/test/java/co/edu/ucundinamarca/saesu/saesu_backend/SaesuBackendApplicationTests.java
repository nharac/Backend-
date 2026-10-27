package co.edu.ucundinamarca.saesu.saesu_backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Levanta el contexto completo de Spring. Necesita la base de datos y las
 * variables del .env, por eso solo corre cuando DB_URL está definida
 * (las pruebas unitarias de abajo no la necesitan).
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class SaesuBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
