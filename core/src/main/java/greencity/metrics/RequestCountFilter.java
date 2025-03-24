package greencity.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestCountFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestCountFilter.class);
    private final Counter requestCounter;

    public RequestCountFilter(MeterRegistry meterRegistry) {
        this.requestCounter = Counter.builder("http_requests_total")
                .description("Total number of HTTP requests")
                .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            requestCounter.increment();
        } catch (Exception e) {
            logger.error("Error incrementing request count: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}