package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUsersInMemoryMetrics extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ActiveUsersInMemoryMetrics.class);
    private final MeterRegistry meterRegistry;
    private final Map<String, Instant> activeUsers = new ConcurrentHashMap<>();
    private final Map<String, List<Instant>> userLogins = new ConcurrentHashMap<>();
    private static final long INACTIVITY_TIMEOUT_MINUTES = 60;
    private static final long INACTIVITY_TIMEOUT_SECONDS = INACTIVITY_TIMEOUT_MINUTES * 60;
    private static final long LOGIN_WINDOW_HOURS = 3;
    private static final long LOGIN_WINDOW_SECONDS = LOGIN_WINDOW_HOURS * 3600;

    public ActiveUsersInMemoryMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        Gauge.builder("app_active_users", this::getActiveUsersCount)
            .description("Number of active users on the site")
            .baseUnit("users")
            .register(meterRegistry);

        Gauge.builder("app_user_logins_per_3h", this::getUserLoginsCount)
            .description("Number of unique user logins in the last 3 hours")
            .baseUnit("users")
            .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String username = auth.getName();
            Instant now = Instant.now();
            activeUsers.put(username, now);
        }

        removeInactiveUsers();
        filterChain.doFilter(request, response);
    }

    public void recordLogin(String email, Long loginTime) {
        Instant signInInstant = Instant.ofEpochMilli(loginTime);
        userLogins.computeIfAbsent(email, k -> new ArrayList<>()).add(signInInstant);
        logger.debug("New login recorded for user: {} at time: {}", email, signInInstant);

        removeInactiveUsers();
    }

    private void removeInactiveUsers() {
        Instant now = Instant.now();
        activeUsers.entrySet()
            .removeIf(entry -> now.getEpochSecond() - entry.getValue().getEpochSecond() > INACTIVITY_TIMEOUT_SECONDS);

        userLogins.forEach((key, logins) -> logins
            .removeIf(login -> now.getEpochSecond() - login.getEpochSecond() > LOGIN_WINDOW_SECONDS));
        userLogins.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private int getActiveUsersCount() {
        return activeUsers.size();
    }

    private int getUserLoginsCount() {
        return userLogins.values().stream().mapToInt(List::size).sum();
    }
}