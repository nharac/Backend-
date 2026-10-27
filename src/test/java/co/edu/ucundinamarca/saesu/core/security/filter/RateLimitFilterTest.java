package co.edu.ucundinamarca.saesu.core.security.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

/** Límite de peticiones por cliente real (detrás de nginx) y sin poder falsificar la IP. */
class RateLimitFilterTest {

    private static MockHttpServletRequest peticion(String uri, String ipRemota) {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", uri);
        req.setRequestURI(uri);
        req.setRemoteAddr(ipRemota);
        return req;
    }

    private static int llamar(RateLimitFilter filtro, MockHttpServletRequest req) throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        filtro.doFilter(req, res, new MockFilterChain());
        return res.getStatus();
    }

    @Test
    @DisplayName("Detrás de nginx (IP privada) se usa la IP real de X-Real-IP")
    void usaXRealIpDesdeProxy() {
        MockHttpServletRequest req = peticion("/api/ranking", "172.18.0.3");
        req.addHeader("X-Real-IP", "190.85.10.20");

        assertThat(RateLimitFilter.ipCliente(req)).isEqualTo("190.85.10.20");
    }

    @Test
    @DisplayName("X-Forwarded-For: se toma la primera IP de la lista")
    void usaPrimeraIpDeXForwardedFor() {
        MockHttpServletRequest req = peticion("/api/ranking", "10.0.0.5");
        req.addHeader("X-Forwarded-For", "181.50.1.1, 10.0.0.5");

        assertThat(RateLimitFilter.ipCliente(req)).isEqualTo("181.50.1.1");
    }

    @Test
    @DisplayName("Desde una IP pública la cabecera se ignora (no se puede falsificar)")
    void ignoraCabeceraDesdeIpPublica() {
        MockHttpServletRequest req = peticion("/api/ranking", "200.1.2.3");
        req.addHeader("X-Real-IP", "1.1.1.1");

        assertThat(RateLimitFilter.ipCliente(req)).isEqualTo("200.1.2.3");
    }

    @Test
    @DisplayName("Login: a la petición 11 en un minuto responde 429")
    void limiteDeLogin() throws Exception {
        RateLimitFilter filtro = new RateLimitFilter();
        for (int i = 0; i < 10; i++) {
            assertThat(llamar(filtro, peticion("/api/auth/login", "200.1.2.3"))).isEqualTo(200);
        }
        assertThat(llamar(filtro, peticion("/api/auth/login", "200.1.2.3"))).isEqualTo(429);
    }

    @Test
    @DisplayName("Dos estudiantes detrás del mismo nginx tienen límites separados")
    void limitesSeparadosPorEstudiante() throws Exception {
        RateLimitFilter filtro = new RateLimitFilter();
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest a = peticion("/api/auth/login", "172.18.0.3");
            a.addHeader("X-Real-IP", "190.0.0.1");
            llamar(filtro, a);
        }
        MockHttpServletRequest b = peticion("/api/auth/login", "172.18.0.3");
        b.addHeader("X-Real-IP", "190.0.0.2");

        assertThat(llamar(filtro, b)).isEqualTo(200);
    }

    @Test
    @DisplayName("El resto de la API permite más peticiones (una página hace varias)")
    void limiteGeneralMasAlto() throws Exception {
        RateLimitFilter filtro = new RateLimitFilter();
        for (int i = 0; i < 60; i++) {
            assertThat(llamar(filtro, peticion("/api/practica/progreso", "200.1.2.3"))).isEqualTo(200);
        }
    }
}
