package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(DiskSpaceHealthIndicator.class);
    private final MeterRegistry meterRegistry;
    private static final double MIN_FREE_SPACE_PERCENTAGE = 10.0;
    private final File root;

    @Autowired
    public DiskSpaceHealthIndicator(MeterRegistry meterRegistry) {
        this(meterRegistry, new File("/"));
    }

    public DiskSpaceHealthIndicator(MeterRegistry meterRegistry, File root) {
        this.meterRegistry = meterRegistry;
        this.root = root;
    }

    @Override
    public Health health() {
        try {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            double freeSpacePercentage = (double) freeSpace / totalSpace * 100;

            if (freeSpacePercentage >= MIN_FREE_SPACE_PERCENTAGE) {
                meterRegistry.gauge("app_disk_space_health", 1);
                return Health.up()
                    .withDetail("diskSpace", "Sufficient disk space available")
                    .withDetail("freeSpacePercentage", freeSpacePercentage)
                    .withDetail("freeSpaceBytes", freeSpace)
                    .build();
            } else {
                meterRegistry.gauge("app_disk_space_health", 0);
                return Health.down()
                    .withDetail("diskSpace", "Low disk space detected")
                    .withDetail("freeSpacePercentage", freeSpacePercentage)
                    .withDetail("freeSpaceBytes", freeSpace)
                    .build();
            }
        } catch (Exception e) {
            logger.error("Disk space health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_disk_space_health", 0);
            return Health.down()
                .withDetail("diskSpace", "Error checking disk space")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}