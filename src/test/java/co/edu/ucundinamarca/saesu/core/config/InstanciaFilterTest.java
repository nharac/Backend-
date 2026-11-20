package co.edu.ucundinamarca.saesu.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class InstanciaFilterTest {

    @Test
    void poneElNombreDeLaReplicaEnCadaRespuesta() throws Exception {
        MockHttpServletResponse respuesta = new MockHttpServletResponse();
        new InstanciaFilter("backend2").doFilter(new MockHttpServletRequest("GET", "/api/temas"), respuesta, new MockFilterChain());
        assertThat(respuesta.getHeader("X-Instancia")).isEqualTo("backend2");
    }

    @Test
    void elEndpointDiceLaInstancia() {
        assertThat(new InstanciaController("backend1").instancia()).containsEntry("instancia", "backend1").containsKey("hora");
    }
}
