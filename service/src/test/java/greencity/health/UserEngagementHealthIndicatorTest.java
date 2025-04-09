package greencity.health;

import greencity.repository.UserRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserEngagementHealthIndicatorTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private MeterRegistry meterRegistry;

    private UserEngagementHealthIndicator healthIndicator;

    private final int minUserThreshold = 5;

    @Test
    void testHealthUpWhenNewUsersCountIsAboveThreshold() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            LocalDateTime last24Hours = fixedNow.minusHours(24);
            long newUsers = 7;

            when(userRepo.countByDateOfRegistrationAfter(last24Hours)).thenReturn(newUsers);
            when(meterRegistry.gauge(eq("app_user_engagement_health"), eq(1))).thenReturn(null);

            healthIndicator = new UserEngagementHealthIndicator(userRepo, meterRegistry, minUserThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.UP, health.getStatus());
            assertEquals("User engagement is normal", health.getDetails().get("userEngagement"));
            assertEquals(newUsers, health.getDetails().get("newUsersLast24h"));

            verify(userRepo).countByDateOfRegistrationAfter(last24Hours);
            verify(meterRegistry).gauge("app_user_engagement_health", 1);
        }
    }

    @Test
    void testHealthOutOfServiceWhenNewUsersCountIsBelowThreshold() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            LocalDateTime last24Hours = fixedNow.minusHours(24);
            long newUsers = 2;

            when(userRepo.countByDateOfRegistrationAfter(last24Hours)).thenReturn(newUsers);
            when(meterRegistry.gauge(eq("app_user_engagement_health"), eq(0))).thenReturn(null);

            healthIndicator = new UserEngagementHealthIndicator(userRepo, meterRegistry, minUserThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
            assertEquals("Low user engagement detected", health.getDetails().get("userEngagement"));
            assertEquals(newUsers, health.getDetails().get("newUsersLast24h"));
            assertEquals(minUserThreshold, health.getDetails().get("minThreshold"));

            verify(userRepo).countByDateOfRegistrationAfter(last24Hours);
            verify(meterRegistry).gauge("app_user_engagement_health", 0);
        }
    }

    @Test
    void testHealthDownWhenRepositoryThrowsException() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            LocalDateTime last24Hours = fixedNow.minusHours(24);
            RuntimeException exception = new RuntimeException("Database error");

            when(userRepo.countByDateOfRegistrationAfter(last24Hours)).thenThrow(exception);
            when(meterRegistry.gauge(eq("app_user_engagement_health"), eq(0))).thenReturn(null);

            healthIndicator = new UserEngagementHealthIndicator(userRepo, meterRegistry, minUserThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.DOWN, health.getStatus());
            assertEquals("Error checking user engagement", health.getDetails().get("userEngagement"));
            assertEquals("Database error", health.getDetails().get("error"));

            verify(userRepo).countByDateOfRegistrationAfter(last24Hours);
            verify(meterRegistry).gauge("app_user_engagement_health", 0);
        }
    }
}