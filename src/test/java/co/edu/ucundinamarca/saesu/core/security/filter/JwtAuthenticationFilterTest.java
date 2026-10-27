package co.edu.ucundinamarca.saesu.core.security.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** El rol del token se normaliza antes de crear la autoridad ROLE_xxx. */
class JwtAuthenticationFilterTest {

    @Test
    @DisplayName("Un rol con salto de línea, espacios, minúsculas o ROLE_ queda limpio")
    void normalizaRol() {
        assertThat(JwtAuthenticationFilter.normalizarRol("DOCENTE\n")).isEqualTo("DOCENTE");
        assertThat(JwtAuthenticationFilter.normalizarRol("  docente ")).isEqualTo("DOCENTE");
        assertThat(JwtAuthenticationFilter.normalizarRol("ROLE_ESTUDIANTE")).isEqualTo("ESTUDIANTE");
        assertThat(JwtAuthenticationFilter.normalizarRol("role_estudiante\r\n")).isEqualTo("ESTUDIANTE");
        assertThat(JwtAuthenticationFilter.normalizarRol(null)).isEmpty();
    }
}
