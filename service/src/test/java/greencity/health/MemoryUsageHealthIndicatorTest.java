package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemoryUsageHealthIndicatorTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private MemoryMXBean memoryMXBean;

    @Mock
    private MemoryUsage heapMemoryUsage;

    @Mock
    private MemoryUsage nonHeapMemoryUsage;

    private MemoryUsageHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new MemoryUsageHealthIndicator(meterRegistry);
        try {
            java.lang.reflect.Field field = MemoryUsageHealthIndicator.class.getDeclaredField("memoryMXBean");
            field.setAccessible(true);
            field.set(healthIndicator, memoryMXBean);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set MemoryMXBean for testing", e);
        }
    }

    @Test
    void testHealthUpWhenMemoryUsageIsBelowThreshold() {
        long usedHeapMemory = 300L;
        long usedNonHeapMemory = 100L;
        long maxMemory = 1000L;
        double expectedMemoryUsagePercentage = (double) (usedHeapMemory + usedNonHeapMemory) / maxMemory * 100;

        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(heapMemoryUsage);
        when(memoryMXBean.getNonHeapMemoryUsage()).thenReturn(nonHeapMemoryUsage);
        when(heapMemoryUsage.getUsed()).thenReturn(usedHeapMemory);
        when(nonHeapMemoryUsage.getUsed()).thenReturn(usedNonHeapMemory);
        when(heapMemoryUsage.getMax()).thenReturn(maxMemory);

        when(meterRegistry.gauge(eq("app_memory_usage_health"), eq(1))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Memory usage is within safe limits", health.getDetails().get("memoryUsage"));
        assertEquals(expectedMemoryUsagePercentage, (Double) health.getDetails().get("memoryUsagePercentage"), 0.01);
        assertEquals(usedHeapMemory + usedNonHeapMemory, health.getDetails().get("usedMemoryBytes"));

        verify(meterRegistry).gauge("app_memory_usage_health", 1);
        verify(memoryMXBean, times(2)).getHeapMemoryUsage(); // Очікуємо 2 виклики
        verify(memoryMXBean).getNonHeapMemoryUsage();
    }

    @Test
    void testHealthDownWhenMemoryUsageIsAboveThreshold() {
        long usedHeapMemory = 700L;
        long usedNonHeapMemory = 200L;
        long maxMemory = 1000L;
        double expectedMemoryUsagePercentage = (double) (usedHeapMemory + usedNonHeapMemory) / maxMemory * 100; // 90%

        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(heapMemoryUsage);
        when(memoryMXBean.getNonHeapMemoryUsage()).thenReturn(nonHeapMemoryUsage);
        when(heapMemoryUsage.getUsed()).thenReturn(usedHeapMemory);
        when(nonHeapMemoryUsage.getUsed()).thenReturn(usedNonHeapMemory);
        when(heapMemoryUsage.getMax()).thenReturn(maxMemory);

        when(meterRegistry.gauge(eq("app_memory_usage_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("High memory usage detected", health.getDetails().get("memoryUsage"));
        assertEquals(expectedMemoryUsagePercentage, (Double) health.getDetails().get("memoryUsagePercentage"), 0.01);
        assertEquals(usedHeapMemory + usedNonHeapMemory, health.getDetails().get("usedMemoryBytes"));

        verify(meterRegistry).gauge("app_memory_usage_health", 0);
        verify(memoryMXBean, times(2)).getHeapMemoryUsage();
        verify(memoryMXBean).getNonHeapMemoryUsage();
    }

    @Test
    void testHealthDownWhenExceptionOccurs() {
        RuntimeException exception = new RuntimeException("MemoryMXBean error");
        when(memoryMXBean.getHeapMemoryUsage()).thenThrow(exception);

        when(meterRegistry.gauge(eq("app_memory_usage_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Error checking memory usage", health.getDetails().get("memoryUsage"));
        assertEquals("MemoryMXBean error", health.getDetails().get("error"));

        verify(meterRegistry).gauge("app_memory_usage_health", 0);
        verify(memoryMXBean, times(1)).getHeapMemoryUsage();
    }
}