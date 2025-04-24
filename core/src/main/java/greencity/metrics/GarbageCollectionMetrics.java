package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class GarbageCollectionMetrics {
    private static final long TIME_WINDOW_SECONDS = 3600;
    private final MeterRegistry meterRegistry;
    private final List<GarbageCollectorMXBean> gcBeans;
    private final Map<GarbageCollectorMXBean, ConcurrentLinkedQueue<GCTimeRecord>> gcTimeHistory =
        new ConcurrentHashMap<>();
    private final Map<GarbageCollectorMXBean, ConcurrentLinkedQueue<GCCountRecord>> gcCountHistory =
        new ConcurrentHashMap<>();

    public GarbageCollectionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            gcTimeHistory.put(gcBean, new ConcurrentLinkedQueue<>());
            gcCountHistory.put(gcBean, new ConcurrentLinkedQueue<>());
        }
        registerGauges();
    }

    private void registerGauges() {
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            Gauge.builder("app_gc_time_ms_per_hour", this, metrics -> getGCTimePerHour(gcBean))
                .description("Time spent in garbage collection per hour in milliseconds")
                .baseUnit("milliseconds")
                .tag("collector", gcBean.getName())
                .register(meterRegistry);

            Gauge.builder("app_gc_count_per_hour", this, metrics -> getGCCountPerHour(gcBean))
                .description("Number of garbage collection cycles per hour")
                .baseUnit("cycles")
                .tag("collector", gcBean.getName())
                .register(meterRegistry);
        }
    }

    private double getGCTimePerHour(GarbageCollectorMXBean gcBean) {
        return getGCTimePerHour(gcBean, Instant.now());
    }

    double getGCTimePerHour(GarbageCollectorMXBean gcBean, Instant now) {
        ConcurrentLinkedQueue<GCTimeRecord> history = gcTimeHistory.get(gcBean);
        long currentGCTime = gcBean.getCollectionTime();

        history.add(new GCTimeRecord(now, currentGCTime));
        cleanupOldGCTimeRecords(gcBean, now);

        if (history.size() < 2) {
            return 0.0;
        }

        GCTimeRecord oldest = history.peek();
        long timeDiff = currentGCTime - oldest.gcTime;
        return timeDiff >= 0 ? timeDiff : 0.0;
    }

    private double getGCCountPerHour(GarbageCollectorMXBean gcBean) {
        return getGCCountPerHour(gcBean, Instant.now());
    }

    double getGCCountPerHour(GarbageCollectorMXBean gcBean, Instant now) {
        ConcurrentLinkedQueue<GCCountRecord> history = gcCountHistory.get(gcBean);
        long currentGCCount = gcBean.getCollectionCount();

        history.add(new GCCountRecord(now, currentGCCount));
        cleanupOldGCCountRecords(gcBean, now);

        if (history.size() < 2) {
            return 0.0;
        }

        GCCountRecord oldest = history.peek();
        long countDiff = currentGCCount - oldest.gcCount;
        return countDiff >= 0 ? countDiff : 0.0;
    }

    void cleanupOldGCTimeRecords(GarbageCollectorMXBean gcBean, Instant now) {
        ConcurrentLinkedQueue<GCTimeRecord> history = gcTimeHistory.get(gcBean);
        Instant threshold = now.minusSeconds(TIME_WINDOW_SECONDS);
        while (!history.isEmpty() && history.peek().timestamp.isBefore(threshold)) {
            history.poll();
        }
    }

    void cleanupOldGCCountRecords(GarbageCollectorMXBean gcBean, Instant now) {
        ConcurrentLinkedQueue<GCCountRecord> history = gcCountHistory.get(gcBean);
        Instant threshold = now.minusSeconds(TIME_WINDOW_SECONDS);
        while (!history.isEmpty() && history.peek().timestamp.isBefore(threshold)) {
            history.poll();
        }
    }

    private static class GCTimeRecord {
        final Instant timestamp;
        final long gcTime;

        GCTimeRecord(Instant timestamp, long gcTime) {
            this.timestamp = timestamp;
            this.gcTime = gcTime;
        }
    }

    private static class GCCountRecord {
        final Instant timestamp;
        final long gcCount;

        GCCountRecord(Instant timestamp, long gcCount) {
            this.timestamp = timestamp;
            this.gcCount = gcCount;
        }
    }
}