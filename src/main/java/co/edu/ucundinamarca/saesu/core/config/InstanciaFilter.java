package co.edu.ucundinamarca.saesu.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Pone en cada respuesta el encabezado X-Instancia con el nombre de la réplica que la atendió. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InstanciaFilter extends OncePerRequestFilter {

    public static final String ENCABEZADO = "X-Instancia";

    private final String instancia;

    public InstanciaFilter(@Value("${saesu.instancia:local}") String instancia) {
        this.instancia = instancia;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader(ENCABEZADO, instancia);
        chain.doFilter(request, response);
    }
}
