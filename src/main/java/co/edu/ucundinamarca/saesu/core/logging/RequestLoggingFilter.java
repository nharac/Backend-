package co.edu.ucundinamarca.saesu.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Deja una línea de log por cada petición a la API:
 * <pre>GET /api/quiz/historial -> 200 (38 ms)</pre>
 *
 * Cada petición recibe un id corto (requestId) que se guarda en el MDC de SLF4J. Así todas las
 * líneas que escribe esa petición (servicios, adaptadores, errores) salen con el mismo id y se
 * pueden seguir juntas en el archivo de log. El id también se devuelve en la cabecera
 * {@code X-Request-Id} para cruzarlo con lo que ve el navegador.
 *
 * Corre antes que todos los filtros (incluidos los de seguridad), así que también registra las
 * peticiones que terminan en 401, 403 o 429.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final String CABECERA = "X-Request-Id";
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_USUARIO = "usuario";

    private static final Logger log = LoggerFactory.getLogger("saesu.http");
    /** Solo se acepta un id que venga de afuera si es corto y sin caracteres raros (evita inyectar líneas en el log). */
    private static final Pattern ID_VALIDO = Pattern.compile("[A-Za-z0-9-]{8,64}");
    private static final long LENTA_MS = 1500;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return !ruta.startsWith("/api/") || ruta.startsWith("/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = idDe(request.getHeader(CABECERA));
        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(CABECERA, requestId);
        long inicio = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            registrar(request.getMethod(), request.getRequestURI(), response.getStatus(), ms);
            MDC.remove(MDC_USUARIO);
            MDC.remove(MDC_REQUEST_ID);
        }
    }

    static String idDe(String recibido) {
        if (recibido != null && ID_VALIDO.matcher(recibido).matches()) {
            return recibido;
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static void registrar(String metodo, String ruta, int estado, long ms) {
        String lenta = ms >= LENTA_MS ? " LENTA" : "";
        if (estado >= 500) {
            log.error("{} {} -> {} ({} ms){}", metodo, ruta, estado, ms, lenta);
        } else if (estado >= 400 || !lenta.isEmpty()) {
            log.warn("{} {} -> {} ({} ms){}", metodo, ruta, estado, ms, lenta);
        } else {
            log.info("{} {} -> {} ({} ms)", metodo, ruta, estado, ms);
        }
    }

    /**
     * Correo enmascarado para el log: "laura.gomez@ucundinamarca.edu.co" queda "la***@ucundinamarca.edu.co".
     * Sirve para saber quién hizo la petición sin dejar el correo completo en el archivo.
     */
    public static String enmascarar(String correo) {
        if (correo == null || correo.isBlank()) {
            return "-";
        }
        int arroba = correo.indexOf('@');
        if (arroba <= 0) {
            return "***";
        }
        String usuario = correo.substring(0, arroba);
        String visible = usuario.length() <= 2 ? usuario.substring(0, 1) : usuario.substring(0, 2);
        return visible + "***" + correo.substring(arroba);
    }
}
