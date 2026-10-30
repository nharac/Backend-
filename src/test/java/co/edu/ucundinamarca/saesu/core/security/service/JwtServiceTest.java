package co.edu.ucundinamarca.saesu.core.security.service;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import co.edu.ucundinamarca.saesu.core.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtServiceTest {

    private final JwtService jwt = new JwtService();

    @BeforeEach
    void configurar() {
        ReflectionTestUtils.setField(jwt, "secret", "clave-de-prueba-de-al-menos-32-bytes-para-hs256!!");
        ReflectionTestUtils.setField(jwt, "expiration", 60_000L);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void elTokenDeAccesoLlevaCorreoYRol() {
        String token = jwt.generarToken("laura@ucundinamarca.edu.co", "ESTUDIANTE");

        assertThat(jwt.validarToken(token)).isTrue();
        assertThat(jwt.extraerCorreo(token)).isEqualTo("laura@ucundinamarca.edu.co");
        assertThat(jwt.extraerRol(token)).isEqualTo("ESTUDIANTE");
        assertThat(jwt.esRefreshToken(token)).isFalse();
    }

    @Test
    void elRefreshTokenSeReconoce() {
        String refresh = jwt.generarRefreshToken("laura@ucundinamarca.edu.co");
        assertThat(jwt.esRefreshToken(refresh)).isTrue();
        assertThat(jwt.extraerRol(refresh)).isNull();
    }

    @Test
    void unTokenAlteradoOVencidoNoEsValido() {
        String token = jwt.generarToken("laura@ucundinamarca.edu.co", "ESTUDIANTE");
        assertThat(jwt.validarToken(token + "x")).isFalse();
        assertThat(jwt.esRefreshToken("no-es-un-jwt")).isFalse();

        ReflectionTestUtils.setField(jwt, "expiration", -1_000L);
        assertThat(jwt.validarToken(jwt.generarToken("a@b.co", "DOCENTE"))).isFalse();
    }

    @Test
    void elFiltroAutenticaConUnTokenValido() throws Exception {
        JwtAuthenticationFilter filtro = new JwtAuthenticationFilter(jwt);
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/perfil");
        req.addHeader("Authorization", "Bearer " + jwt.generarToken("laura@ucundinamarca.edu.co", " role_docente "));
        FilterChain chain = mock(FilterChain.class);

        filtro.doFilter(req, new MockHttpServletResponse(), chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getName()).isEqualTo("laura@ucundinamarca.edu.co");
        assertThat(auth.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_DOCENTE");
        assertThat(MDC.get(RequestLoggingFilter.MDC_USUARIO)).isEqualTo("la***@ucundinamarca.edu.co");
        verify(chain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void elFiltroDejaPasarSinAutenticarCuandoNoHayTokenOEsInvalido() throws Exception {
        JwtAuthenticationFilter filtro = new JwtAuthenticationFilter(jwt);
        FilterChain chain = mock(FilterChain.class);

        filtro.doFilter(new MockHttpServletRequest("GET", "/api/x"), new MockHttpServletResponse(), chain);
        MockHttpServletRequest basic = new MockHttpServletRequest("GET", "/api/x");
        basic.addHeader("Authorization", "Basic abc");
        filtro.doFilter(basic, new MockHttpServletResponse(), chain);
        MockHttpServletRequest malo = new MockHttpServletRequest("GET", "/api/x");
        malo.addHeader("Authorization", "Bearer token.falso.x");
        filtro.doFilter(malo, new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain, org.mockito.Mockito.times(3)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
