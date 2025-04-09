package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component
public class MemoryMetrics {
    private final MeterRegistry meterRegistry;
    private final MemoryMXBean memoryMXBean;

    public MemoryMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();

        Gauge.builder("app_memory_used_bytes", this, MemoryMetrics::getUsedMemory)
            .description("Amount of memory used by the application in bytes")
            .baseUnit("bytes")
            .register(meterRegistry);

        Gauge.builder("app_memory_total_bytes", this, MemoryMetrics::getTotalMemory)
            .description("Total amount of memory available to the JVM in bytes")
            .baseUnit("bytes")
            .register(meterRegistry);
    }

    @Scheduled(fixedRate = 60000)
    public void updateMemoryMetrics() {
        meterRegistry.gauge("app_memory_used_bytes", this, MemoryMetrics::getUsedMemory);
        meterRegistry.gauge("app_memory_total_bytes", this, MemoryMetrics::getTotalMemory);
    }

    private double getUsedMemory() {
        return memoryMXBean.getHeapMemoryUsage().getUsed() + memoryMXBean.getNonHeapMemoryUsage().getUsed();
    }

    private double getTotalMemory() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }
}