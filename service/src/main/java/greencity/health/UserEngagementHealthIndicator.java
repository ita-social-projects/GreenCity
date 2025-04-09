package greencity.health;

import greencity.repository.UserRepo;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class UserEngagementHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(UserEngagementHealthIndicator.class);
    private final UserRepo userRepo;
    private final MeterRegistry meterRegistry;
    private final int minUserThreshold;

    @Autowired
    public UserEngagementHealthIndicator(UserRepo userRepo, MeterRegistry meterRegistry,
        @Value("${user.engagement.min.threshold:5}") int minUserThreshold) {
        this.userRepo = userRepo;
        this.meterRegistry = meterRegistry;
        this.minUserThreshold = minUserThreshold;
    }

    @Override
    public Health health() {
        try {
            LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
            long newUsers = userRepo.countByDateOfRegistrationAfter(last24Hours);

            if (newUsers >= minUserThreshold) {
                meterRegistry.gauge("app_user_engagement_health", 1);
                return Health.up()
                    .withDetail("userEngagement", "User engagement is normal")
                    .withDetail("newUsersLast24h", newUsers)
                    .build();
            } else {
                meterRegistry.gauge("app_user_engagement_health", 0);
                return Health.outOfService()
                    .withDetail("userEngagement", "Low user engagement detected")
                    .withDetail("newUsersLast24h", newUsers)
                    .withDetail("minThreshold", minUserThreshold)
                    .build();
            }
        } catch (Exception e) {
            logger.error("User engagement health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_user_engagement_health", 0);
            return Health.down()
                .withDetail("userEngagement", "Error checking user engagement")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
