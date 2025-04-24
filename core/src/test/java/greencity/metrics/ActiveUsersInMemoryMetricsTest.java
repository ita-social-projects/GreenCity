package greencity.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActiveUsersInMemoryMetricsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private MeterRegistry meterRegistry;
    private ActiveUsersInMemoryMetrics filter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        filter = new ActiveUsersInMemoryMetrics(meterRegistry);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_authenticatedUser_addsToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user123");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(), "Metric should show 1 active user");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_anonymousUser_doesNotAddToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(0.0, meterRegistry.get("app_active_users").gauge().value(), "Metric should remain 0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_nullAuthentication_doesNotAddToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(0.0, meterRegistry.get("app_active_users").gauge().value(), "Metric should remain 0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void recordLogin_newLogin_addsToUserLogins() {
        String email = "user@example.com";
        long loginTime = System.currentTimeMillis();

        filter.recordLogin(email, loginTime);

        assertEquals(1.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(), "Metric should show 1 login");
    }

    @Test
    void recordLogin_sameUserWithinWindow_addsToUserLogins() {
        String email = "user@example.com";
        long loginTime1 = System.currentTimeMillis();
        long loginTime2 = loginTime1 + (1 * 60 * 60 * 1000);

        filter.recordLogin(email, loginTime1);
        filter.recordLogin(email, loginTime2);

        assertEquals(2.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(), "Metric should show 2 logins");
    }

    @Test
    void recordLogin_sameUserAfterWindow_addsToUserLogins() {
        String email = "user@example.com";
        long loginTime1 = System.currentTimeMillis() - (4 * 60 * 60 * 1000);
        long loginTime2 = System.currentTimeMillis();

        filter.recordLogin(email, loginTime1);
        filter.recordLogin(email, loginTime2);

        assertEquals(1.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(),
            "Metric should show 1 login since the old one is removed");
    }

    @Test
    void recordLogin_multipleUsersWithinWindow_addsToUserLogins() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        long loginTime = System.currentTimeMillis();

        filter.recordLogin(email1, loginTime);
        filter.recordLogin(email2, loginTime);

        assertEquals(2.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(), "Metric should show 2 logins");
    }

    @Test
    void removeInactiveUsers_removesExpiredActiveUsers() throws ServletException, IOException {
        String user = "user123";
        Instant oldTime = Instant.now().minusSeconds(61 * 60);
        setActiveUser(user, oldTime);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("newUser");
        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(),
            "Metric should show 1 active user (old one removed)");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void removeInactiveUsers_removesExpiredLogins() {
        String email = "user@example.com";
        long loginTime1 = System.currentTimeMillis() - (4 * 60 * 60 * 1000);
        long loginTime2 = System.currentTimeMillis();

        filter.recordLogin(email, loginTime1);
        filter.recordLogin(email, loginTime2);

        assertEquals(1.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(),
            "Metric should show 1 login (old one removed)");
    }

    @Test
    void removeInactiveUsers_keepsActiveUsersAndLogins() throws ServletException, IOException {
        String user = "user456";
        Instant recentTime = Instant.now().minusSeconds(30 * 60);
        setActiveUser(user, recentTime);

        String email = "user@example.com";
        long loginTime1 = System.currentTimeMillis() - (1 * 60 * 60 * 1000);
        long loginTime2 = System.currentTimeMillis() - (30 * 60 * 1000);
        filter.recordLogin(email, loginTime1);
        filter.recordLogin(email, loginTime2);

        when(securityContext.getAuthentication()).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(), "Metric should show 1 active user");
        assertEquals(2.0, meterRegistry.get("app_user_logins_per_3h").gauge().value(), "Metric should show 2 logins");
        verify(filterChain).doFilter(request, response);
    }

    private void setActiveUser(String username, Instant time) {
        try {
            java.lang.reflect.Field field = ActiveUsersInMemoryMetrics.class.getDeclaredField("activeUsers");
            field.setAccessible(true);
            Map<String, Instant> activeUsers = (Map<String, Instant>) field.get(filter);
            activeUsers.put(username, time);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set activeUsers", e);
        }
    }
}