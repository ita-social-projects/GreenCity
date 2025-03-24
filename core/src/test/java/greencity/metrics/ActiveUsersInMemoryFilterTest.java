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
class ActiveUsersInMemoryFilterTest {

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
    private ActiveUsersInMemoryFilter filter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        filter = new ActiveUsersInMemoryFilter(meterRegistry);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_authenticatedUser_addsToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user123");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(), "Метрика має показувати 1");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_anonymousUser_doesNotAddToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(0.0, meterRegistry.get("app_active_users").gauge().value(), "Метрика має залишатися 0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_nullAuthentication_doesNotAddToActiveUsers() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(0.0, meterRegistry.get("app_active_users").gauge().value(), "Метрика має залишатися 0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void removeInactiveUsers_removesUsersAfterTimeout() throws ServletException, IOException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user123");

        Instant oldTime = Instant.now().minusSeconds(61 * 60);
        Map<String, Instant> activeUsers = getActiveUsersField(filter);
        activeUsers.put("user123", oldTime);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(), "Метрика показує 1 активного користувача");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void removeInactiveUsers_keepsActiveUsers() throws ServletException, IOException {
        // Додаємо користувача з недавнім часом
        Instant recentTime = Instant.now().minusSeconds(30 * 60);
        Map<String, Instant> activeUsers = getActiveUsersField(filter);
        activeUsers.put("user456", recentTime);

        // Виконуємо фільтр без нової автентифікації
        when(securityContext.getAuthentication()).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);

        assertEquals(1.0, meterRegistry.get("app_active_users").gauge().value(), "Метрика показує 1 активного користувача");
        verify(filterChain).doFilter(request, response);
    }

    private Map<String, Instant> getActiveUsersField(ActiveUsersInMemoryFilter filter) {
        try {
            java.lang.reflect.Field field = ActiveUsersInMemoryFilter.class.getDeclaredField("activeUsers");
            field.setAccessible(true);
            return (Map<String, Instant>) field.get(filter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access activeUsers field", e);
        }
    }
}