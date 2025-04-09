package greencity.health;

import greencity.repository.NotificationRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceHealthIndicatorTest {

    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private MeterRegistry meterRegistry;

    private NotificationServiceHealthIndicator healthIndicator;

    private final int minNotificationsThreshold = 5;

    @Test
    void testHealthUpWhenNotificationsCountIsAboveThreshold() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            ZonedDateTime last24Hours = fixedNow.minusHours(24).atZone(ZoneId.systemDefault());
            long recentNotifications = 10;

            when(notificationRepo.countByTimeAfter(last24Hours)).thenReturn(recentNotifications);
            when(meterRegistry.gauge(eq("app_notification_service_health"), eq(1))).thenReturn(null);

            healthIndicator =
                new NotificationServiceHealthIndicator(notificationRepo, meterRegistry, minNotificationsThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.UP, health.getStatus());
            assertEquals("Notification service is active", health.getDetails().get("notificationService"));
            assertEquals(recentNotifications, health.getDetails().get("notificationsLast24h"));

            verify(notificationRepo).countByTimeAfter(last24Hours);
            verify(meterRegistry).gauge("app_notification_service_health", 1);
        }
    }

    @Test
    void testHealthOutOfServiceWhenNotificationsCountIsBelowThreshold() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            ZonedDateTime last24Hours = fixedNow.minusHours(24).atZone(ZoneId.systemDefault());
            long recentNotifications = 3;

            when(notificationRepo.countByTimeAfter(last24Hours)).thenReturn(recentNotifications);
            when(meterRegistry.gauge(eq("app_notification_service_health"), eq(0))).thenReturn(null);

            healthIndicator =
                new NotificationServiceHealthIndicator(notificationRepo, meterRegistry, minNotificationsThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
            assertEquals("Low notification activity detected", health.getDetails().get("notificationService"));
            assertEquals(recentNotifications, health.getDetails().get("notificationsLast24h"));
            assertEquals(minNotificationsThreshold, health.getDetails().get("minThreshold"));

            verify(notificationRepo).countByTimeAfter(last24Hours);
            verify(meterRegistry).gauge("app_notification_service_health", 0);
        }
    }

    @Test
    void testHealthDownWhenRepositoryThrowsException() {
        LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedNow);

            ZonedDateTime last24Hours = fixedNow.minusHours(24).atZone(ZoneId.systemDefault());
            RuntimeException exception = new RuntimeException("Database error");

            when(notificationRepo.countByTimeAfter(last24Hours)).thenThrow(exception);
            when(meterRegistry.gauge(eq("app_notification_service_health"), eq(0))).thenReturn(null);

            healthIndicator =
                new NotificationServiceHealthIndicator(notificationRepo, meterRegistry, minNotificationsThreshold);

            Health health = healthIndicator.health();

            assertEquals(Status.DOWN, health.getStatus());
            assertEquals("Error checking notification service", health.getDetails().get("notificationService"));
            assertEquals("Database error", health.getDetails().get("error"));

            verify(notificationRepo).countByTimeAfter(last24Hours);
            verify(meterRegistry).gauge("app_notification_service_health", 0);
        }
    }
}