package co.edu.ucundinamarca.saesu.core.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filtro = new RequestLoggingFilter();

    @Test
    void asignaUnRequestIdQueSeVeDuranteLaPeticionYVuelveEnLaCabecera() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/quiz/historial");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> visto = new AtomicReference<>();

        filtro.doFilter(request, response, new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                visto.set(MDC.get(RequestLoggingFilter.MDC_REQUEST_ID));
            }
        });

        assertThat(visto.get()).hasSize(8);
        assertThat(response.getHeader(RequestLoggingFilter.CABECERA)).isEqualTo(visto.get());
        assertThat(MDC.get(RequestLoggingFilter.MDC_REQUEST_ID)).as("se limpia al terminar").isNull();
    }

    @Test
    void reutilizaUnIdValidoQueMandeElCliente() {
        assertThat(RequestLoggingFilter.idDe("abc12345-xyz")).isEqualTo("abc12345-xyz");
    }

    @Test
    void ignoraIdsConCaracteresRarosParaNoInyectarLineasEnElLog() {
        String id = RequestLoggingFilter.idDe("malo\nERROR falso");
        assertThat(id).hasSize(8).doesNotContain("\n");
        assertThat(RequestLoggingFilter.idDe(null)).hasSize(8);
    }

    @Test
    void noRegistraRutasFueraDeLaApi() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filtro.doFilter(new MockHttpServletRequest("GET", "/actuator/health"), response, new MockFilterChain());
        assertThat(response.getHeader(RequestLoggingFilter.CABECERA)).isNull();
    }

    @Test
    void enmascaraElCorreo() {
        assertThat(RequestLoggingFilter.enmascarar("laura.gomez@ucundinamarca.edu.co")).isEqualTo("la***@ucundinamarca.edu.co");
        assertThat(RequestLoggingFilter.enmascarar("a@x.co")).isEqualTo("a***@x.co");
        assertThat(RequestLoggingFilter.enmascarar("sin-arroba")).isEqualTo("***");
        assertThat(RequestLoggingFilter.enmascarar(null)).isEqualTo("-");
    }
}
