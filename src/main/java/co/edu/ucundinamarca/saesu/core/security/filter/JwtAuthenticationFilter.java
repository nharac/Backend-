package co.edu.ucundinamarca.saesu.core.security.filter;

import co.edu.ucundinamarca.saesu.core.logging.RequestLoggingFilter;
import co.edu.ucundinamarca.saesu.core.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtService.validarToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String correo = jwtService.extraerCorreo(token);
            String rol = normalizarRol(jwtService.extraerRol(token));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Las líneas de log de esta petición salen con el usuario (correo enmascarado)
            MDC.put(RequestLoggingFilter.MDC_USUARIO, RequestLoggingFilter.enmascarar(correo));
        }

        chain.doFilter(request, response);
    }

    /**
     * Deja el rol como "DOCENTE" o "ESTUDIANTE" aunque en la BD haya quedado con espacios,
     * saltos de línea, minúsculas o el prefijo "ROLE_". Sin esto, un rol guardado como
     * "DOCENTE" + salto de línea daba la autoridad "ROLE_DOCENTE\n" y
     * {@code @PreAuthorize("hasRole('DOCENTE')")} respondía 403.
     */
    static String normalizarRol(String rol) {
        if (rol == null) {
            return "";
        }
        String limpio = rol.replaceAll("\\s+", "").toUpperCase();
        return limpio.startsWith("ROLE_") ? limpio.substring(5) : limpio;
    }
}
