package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class HabitTrackingHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(HabitTrackingHealthIndicator.class);
    private final MeterRegistry meterRegistry;
    private final int minHabitUpdates;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public HabitTrackingHealthIndicator(
        MeterRegistry meterRegistry,
        @Value("${habit.tracking.min.threshold:10}") int minHabitUpdates) {
        this.meterRegistry = meterRegistry;
        this.minHabitUpdates = minHabitUpdates;
    }

    @Override
    public Health health() {
        try {
            LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
            ZonedDateTime zonedLast24Hours = last24Hours.atZone(ZoneId.systemDefault());

            Query newHabitsQuery = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM habits WHERE created_at >= :last24Hours");
            newHabitsQuery.setParameter("last24Hours", last24Hours);
            long newHabitsCount = ((Number) newHabitsQuery.getSingleResult()).longValue();

            Query newAssignsQuery = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM habit_assign WHERE create_date >= :last24Hours");
            newAssignsQuery.setParameter("last24Hours", zonedLast24Hours);
            long newAssignsCount = ((Number) newAssignsQuery.getSingleResult()).longValue();

            long totalActivityCount = newHabitsCount + newAssignsCount;

            if (totalActivityCount >= minHabitUpdates) {
                meterRegistry.gauge("app_habit_tracking_health", 1);
                return Health.up()
                    .withDetail("habitTracking", "Habit tracking is active")
                    .withDetail("newHabitsLast24h", newHabitsCount)
                    .withDetail("newAssignsLast24h", newAssignsCount)
                    .withDetail("totalActivityLast24h", totalActivityCount)
                    .build();
            } else {
                meterRegistry.gauge("app_habit_tracking_health", 0);
                return Health.outOfService()
                    .withDetail("habitTracking", "Low habit tracking activity")
                    .withDetail("newHabitsLast24h", newHabitsCount)
                    .withDetail("newAssignsLast24h", newAssignsCount)
                    .withDetail("totalActivityLast24h", totalActivityCount)
                    .withDetail("minThreshold", minHabitUpdates)
                    .build();
            }
        } catch (Exception e) {
            logger.error("Habit tracking health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_habit_tracking_health", 0);
            return Health.down()
                .withDetail("habitTracking", "Error checking habit tracking")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}