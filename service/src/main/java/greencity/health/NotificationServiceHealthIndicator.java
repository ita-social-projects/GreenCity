package greencity.health;

import greencity.repository.NotificationRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class NotificationServiceHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceHealthIndicator.class);
    private final NotificationRepo notificationRepo;
    private final MeterRegistry meterRegistry;
    private final int minNotificationsThreshold;

    @Autowired
    public NotificationServiceHealthIndicator(NotificationRepo notificationRepo, MeterRegistry meterRegistry,
        @Value("${notification.service.min.threshold:5}") int minNotificationsThreshold) {
        this.notificationRepo = notificationRepo;
        this.meterRegistry = meterRegistry;
        this.minNotificationsThreshold = minNotificationsThreshold;
    }

    @Override
    public Health health() {
        try {
            LocalDateTime last24HoursLocal = LocalDateTime.now().minusHours(24);
            ZonedDateTime last24Hours = last24HoursLocal.atZone(ZoneId.systemDefault());
            long recentNotifications = notificationRepo.countByTimeAfter(last24Hours);

            if (recentNotifications >= minNotificationsThreshold) {
                meterRegistry.gauge("app_notification_service_health", 1);
                return Health.up()
                    .withDetail("notificationService", "Notification service is active")
                    .withDetail("notificationsLast24h", recentNotifications)
                    .build();
            } else {
                meterRegistry.gauge("app_notification_service_health", 0);
                return Health.outOfService()
                    .withDetail("notificationService", "Low notification activity detected")
                    .withDetail("notificationsLast24h", recentNotifications)
                    .withDetail("minThreshold", minNotificationsThreshold)
                    .build();
            }
        } catch (Exception e) {
            logger.error("Notification service health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_notification_service_health", 0);
            return Health.down()
                .withDetail("notificationService", "Error checking notification service")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}