package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GarbageCollectionMetricsTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private GarbageCollectorMXBean gcBean1;

    @Mock
    private GarbageCollectorMXBean gcBean2;

    @Mock
    private Gauge.Builder gaugeBuilder;

    @BeforeEach
    void setUp() {
        when(gcBean1.getName()).thenReturn("G1 Young Generation");
        when(gcBean2.getName()).thenReturn("G1 Old Generation");
    }

    @Test
    void testMetricsInitialized() {
        try (MockedStatic<Instant> instantMock = mockStatic(Instant.class);
            MockedStatic<Gauge> gaugeStaticMock = mockStatic(Gauge.class);
            MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            Instant now = Instant.parse("2025-04-08T10:00:00Z");
            instantMock.when(Instant::now).thenReturn(now);

            List<GarbageCollectorMXBean> gcBeans = Arrays.asList(gcBean1, gcBean2);
            managementFactoryMock.when(ManagementFactory::getGarbageCollectorMXBeans).thenReturn(gcBeans);

            gaugeStaticMock.when(() -> Gauge.builder(anyString(), any(), any()))
                .thenReturn(gaugeBuilder);
            when(gaugeBuilder.description(anyString())).thenReturn(gaugeBuilder);
            when(gaugeBuilder.baseUnit(anyString())).thenReturn(gaugeBuilder);
            when(gaugeBuilder.tag(anyString(), anyString())).thenReturn(gaugeBuilder);
            when(gaugeBuilder.register(any(MeterRegistry.class))).thenReturn(mock(Gauge.class));

            GarbageCollectionMetrics gcMetrics = new GarbageCollectionMetrics(meterRegistry);

            gaugeStaticMock.verify(() -> Gauge.builder(eq("app_gc_time_ms_per_hour"), any(), any()), times(2));
            gaugeStaticMock.verify(() -> Gauge.builder(eq("app_gc_count_per_hour"), any(), any()), times(2));

            verify(gaugeBuilder, times(2)).tag(eq("collector"), eq("G1 Young Generation"));
            verify(gaugeBuilder, times(2)).tag(eq("collector"), eq("G1 Old Generation"));
        }
    }

    @Test
    void testGCTimePerHour() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            Instant now = Instant.parse("2025-04-08T10:00:00Z");

            List<GarbageCollectorMXBean> gcBeans = Arrays.asList(gcBean1, gcBean2);
            managementFactoryMock.when(ManagementFactory::getGarbageCollectorMXBeans).thenReturn(gcBeans);

            GarbageCollectionMetrics gcMetrics = new GarbageCollectionMetrics(meterRegistry);

            when(gcBean1.getCollectionTime()).thenReturn(100L, 300L);
            when(gcBean2.getCollectionTime()).thenReturn(200L, 500L);

            Method getGCTimePerHourMethod = GarbageCollectionMetrics.class.getDeclaredMethod("getGCTimePerHour",
                GarbageCollectorMXBean.class, Instant.class);
            getGCTimePerHourMethod.setAccessible(true);

            assertEquals(0.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean1, now),
                "First call should return 0 (not enough data)");
            assertEquals(0.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean2, now),
                "First call should return 0 (not enough data)");

            assertEquals(200.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean1, now),
                "GC time for G1 Young Generation should be 200ms (300-100)");
            assertEquals(300.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean2, now),
                "GC time for G1 Old Generation should be 300ms (500-200)");

            when(gcBean1.getCollectionTime()).thenReturn(-1L);
            assertEquals(0.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean1, now),
                "GC time should be 0 if not supported");
        }
    }

    @Test
    void testGCCountPerHour() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            Instant now = Instant.parse("2025-04-08T10:00:00Z");

            List<GarbageCollectorMXBean> gcBeans = Arrays.asList(gcBean1, gcBean2);
            managementFactoryMock.when(ManagementFactory::getGarbageCollectorMXBeans).thenReturn(gcBeans);

            GarbageCollectionMetrics gcMetrics = new GarbageCollectionMetrics(meterRegistry);

            when(gcBean1.getCollectionCount()).thenReturn(5L, 8L);
            when(gcBean2.getCollectionCount()).thenReturn(3L, 7L);

            Method getGCCountPerHourMethod = GarbageCollectionMetrics.class.getDeclaredMethod("getGCCountPerHour",
                GarbageCollectorMXBean.class, Instant.class);
            getGCCountPerHourMethod.setAccessible(true);

            assertEquals(0.0, (double) getGCCountPerHourMethod.invoke(gcMetrics, gcBean1, now),
                "First call should return 0 (not enough data)");
            assertEquals(0.0, (double) getGCCountPerHourMethod.invoke(gcMetrics, gcBean2, now),
                "First call should return 0 (not enough data)");

            assertEquals(3.0, (double) getGCCountPerHourMethod.invoke(gcMetrics, gcBean1, now),
                "GC count for G1 Young Generation should be 3 (8-5)");
            assertEquals(4.0, (double) getGCCountPerHourMethod.invoke(gcMetrics, gcBean2, now),
                "GC count for G1 Old Generation should be 4 (7-3)");
        }
    }

    @Test
    void testCleanupOldRecords() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try (MockedStatic<ManagementFactory> managementFactoryMock = mockStatic(ManagementFactory.class)) {
            List<GarbageCollectorMXBean> gcBeans = Arrays.asList(gcBean1, gcBean2);
            managementFactoryMock.when(ManagementFactory::getGarbageCollectorMXBeans).thenReturn(gcBeans);

            Instant now = Instant.parse("2025-04-08T10:00:00Z");
            Instant twoHoursLater = Instant.parse("2025-04-08T12:00:00Z");

            GarbageCollectionMetrics gcMetrics = new GarbageCollectionMetrics(meterRegistry);

            when(gcBean1.getCollectionTime()).thenReturn(100L, 300L);

            Method cleanupGCTimeMethod = GarbageCollectionMetrics.class.getDeclaredMethod("cleanupOldGCTimeRecords",
                GarbageCollectorMXBean.class, Instant.class);
            cleanupGCTimeMethod.setAccessible(true);

            Method getGCTimePerHourMethod = GarbageCollectionMetrics.class.getDeclaredMethod("getGCTimePerHour",
                GarbageCollectorMXBean.class, Instant.class);
            getGCTimePerHourMethod.setAccessible(true);

            getGCTimePerHourMethod.invoke(gcMetrics, gcBean1, now);

            cleanupGCTimeMethod.invoke(gcMetrics, gcBean1, twoHoursLater);

            assertEquals(0.0, (double) getGCTimePerHourMethod.invoke(gcMetrics, gcBean1, twoHoursLater),
                "After cleanup, should return 0 (no data in window)");
        }
    }
}