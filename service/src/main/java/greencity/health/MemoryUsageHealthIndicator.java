package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component
public class MemoryUsageHealthIndicator implements HealthIndicator {
    private static final Logger logger = LoggerFactory.getLogger(MemoryUsageHealthIndicator.class);
    private final MeterRegistry meterRegistry;
    private final MemoryMXBean memoryMXBean;
    private static final double MAX_MEMORY_USAGE_PERCENTAGE = 80.0;

    @Autowired
    public MemoryUsageHealthIndicator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    public Health health() {
        try {
            long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed();
            long maxMemory = memoryMXBean.getHeapMemoryUsage().getMax();
            double memoryUsagePercentage = (double) usedMemory / maxMemory * 100;

            if (memoryUsagePercentage <= MAX_MEMORY_USAGE_PERCENTAGE) {
                meterRegistry.gauge("app_memory_usage_health", 1);
                return Health.up()
                        .withDetail("memoryUsage", "Memory usage is within safe limits")
                        .withDetail("memoryUsagePercentage", memoryUsagePercentage)
                        .withDetail("usedMemoryBytes", usedMemory)
                        .build();
            } else {
                meterRegistry.gauge("app_memory_usage_health", 0);
                return Health.down()
                        .withDetail("memoryUsage", "High memory usage detected")
                        .withDetail("memoryUsagePercentage", memoryUsagePercentage)
                        .withDetail("usedMemoryBytes", usedMemory)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Memory usage health check failed: {}", e.getMessage());
            meterRegistry.gauge("app_memory_usage_health", 0);
            return Health.down()
                    .withDetail("memoryUsage", "Error checking memory usage")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
