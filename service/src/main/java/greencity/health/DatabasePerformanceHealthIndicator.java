package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabasePerformanceHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceHealthIndicator.class);
    private final DataSource dataSource;
    private final MeterRegistry meterRegistry;

    @Autowired
    public DatabasePerformanceHealthIndicator(DataSource dataSource, MeterRegistry meterRegistry) {
        this.dataSource = dataSource;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {
            long startTime = System.currentTimeMillis();
            statement.execute("SELECT 1");
            long latencyMs = System.currentTimeMillis() - startTime;

            meterRegistry.gauge("app_database_health", 1);
            return Health.up()
                .withDetail("database", "PostgreSQL is reachable")
                .withDetail("latencyMs", latencyMs)
                .build();
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_database_health", 0);
            return Health.down()
                .withDetail("database", "PostgreSQL is unreachable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}