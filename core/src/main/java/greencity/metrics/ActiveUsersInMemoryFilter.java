package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUsersInMemoryFilter extends OncePerRequestFilter {
    private final MeterRegistry meterRegistry;
    private final Map<String, Instant> activeUsers = new ConcurrentHashMap<>();
    private static final long INACTIVITY_TIMEOUT_MINUTES = 60;
    private static final long INACTIVITY_TIMEOUT_SECONDS = INACTIVITY_TIMEOUT_MINUTES * 60;

    public ActiveUsersInMemoryFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("app_active_users", this::getActiveUsersCount)
                .description("Number of active users on the site")
                .baseUnit("users")
                .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            activeUsers.put(auth.getName(), Instant.now());
        }
        removeInactiveUsers();
        filterChain.doFilter(request, response);
    }

    private void removeInactiveUsers() {
        Instant now = Instant.now();
        activeUsers.entrySet().removeIf(entry ->
                now.getEpochSecond() - entry.getValue().getEpochSecond() > INACTIVITY_TIMEOUT_SECONDS);
    }

    private int getActiveUsersCount() {
        return activeUsers.size();
    }
}