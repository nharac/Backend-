package co.edu.ucundinamarca.saesu.core.security.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Limita peticiones por IP del cliente.
 * - Login y registro: 10 por minuto (frena ataques de fuerza bruta).
 * - Resto de la API: 120 por minuto (una página hace varias peticiones a la vez).
 *
 * Detrás de nginx todas las peticiones llegan desde la IP del contenedor, por eso se usa
 * la cabecera X-Real-IP / X-Forwarded-For, pero SOLO si la petición viene de una red
 * privada (el propio nginx). Si alguien llama directo al puerto 8080 no puede falsificarla.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private static final int LIMITE_AUTH = 10;
    private static final int LIMITE_API = 120;

    // Los baldes expiran solos si la IP deja de usar la API (antes el mapa crecía sin límite)
    private final Cache<String, Bucket> baldes = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(50_000)
            .build();

    private static Bucket nuevoBalde(int porMinuto) {
        Bandwidth limite = Bandwidth.classic(porMinuto, Refill.greedy(porMinuto, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limite).build();
    }

    /** Rutas públicas con el límite estricto (fuerza bruta de contraseñas y de códigos de recuperación). */
    private static final java.util.Set<String> RUTAS_AUTH = java.util.Set.of(
            "/api/auth/login", "/api/auth/registro", "/api/auth/recuperar", "/api/auth/recuperar/validar", "/api/auth/restablecer",
            "/api/auth/verificar-correo", "/api/auth/reenviar-verificacion");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Swagger, actuator, etc. no cuentan
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        boolean esAuth = RUTAS_AUTH.contains(uri);
        String clave = (esAuth ? "auth:" : "api:") + ipCliente(request);
        Bucket balde = baldes.get(clave, k -> nuevoBalde(esAuth ? LIMITE_AUTH : LIMITE_API));

        if (balde.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Límite de peticiones superado: {} {} desde {}", request.getMethod(), uri, ipCliente(request));
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"status\": 429, \"message\": \"Demasiadas peticiones. Por favor, intenta de nuevo en un minuto.\"}");
        }
    }

    /** IP real del cliente: la cabecera del proxy solo se acepta si viene de una red privada. */
    static String ipCliente(HttpServletRequest request) {
        String remota = request.getRemoteAddr();
        if (esPrivada(remota)) {
            String real = request.getHeader("X-Real-IP");
            if (real != null && !real.isBlank()) {
                return real.trim();
            }
            String reenviada = request.getHeader("X-Forwarded-For");
            if (reenviada != null && !reenviada.isBlank()) {
                return reenviada.split(",")[0].trim();
            }
        }
        return remota;
    }

    private static boolean esPrivada(String ip) {
        if (ip == null) return false;
        return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("127.")
                || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")
                || ip.matches("^172\\.(1[6-9]|2\\d|3[01])\\..*");
    }
}
