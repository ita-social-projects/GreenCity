package greencity.metrics;

import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.lang.management.ManagementFactory;

@Component
public class CPUMetrics {
    private final MeterRegistry meterRegistry;
    private final OperatingSystemMXBean osBean;

    public CPUMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        Gauge.builder("app_cpu_usage_percent", this, CPUMetrics::getCpuUsage)
            .description("CPU usage of the application in percent")
            .baseUnit("percent")
            .register(meterRegistry);
    }

    @Scheduled(fixedRate = 60000)
    public void updateCPUMetrics() {
        meterRegistry.gauge("app_cpu_usage_percent", this, CPUMetrics::getCpuUsage);
    }

    private double getCpuUsage() {
        double cpuLoad = osBean.getProcessCpuLoad() * 100;
        return cpuLoad >= 0 ? cpuLoad : 0;
    }
}