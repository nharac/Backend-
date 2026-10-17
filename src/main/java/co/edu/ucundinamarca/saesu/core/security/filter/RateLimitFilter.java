package co.edu.ucundinamarca.saesu.core.security.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Un mapa para guardar los "baldes" de cada IP
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Límite: 20 peticiones por minuto por IP
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Obtenemos la IP del cliente
        String ip = request.getRemoteAddr();
        
        // Obtenemos o creamos el balde para esta IP
        Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

        // Intentamos consumir 1 token
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response); // Si hay tokens, lo dejamos pasar
        } else {
            // Si no hay tokens, bloqueamos la petición
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Demasiadas peticiones. Por favor, intenta de nuevo en un minuto.\"}");
        }
    }
}