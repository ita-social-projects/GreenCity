package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MemoryMetricsTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private MemoryMXBean memoryMXBean;

    @Mock
    private Gauge usedMemoryGauge;

    @Mock
    private Gauge totalMemoryGauge;

    @Mock
    private MemoryUsage heapMemoryUsage;

    @Mock
    private MemoryUsage nonHeapMemoryUsage;

    private MemoryMetrics memoryMetrics;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Gauge.Builder<MemoryMetrics> usedMemoryBuilder = mock(Gauge.Builder.class);
        when(usedMemoryBuilder.description("Amount of memory used by the application in bytes")).thenReturn(usedMemoryBuilder);
        when(usedMemoryBuilder.baseUnit("bytes")).thenReturn(usedMemoryBuilder);
        when(usedMemoryBuilder.register(meterRegistry)).thenReturn(usedMemoryGauge);

        Gauge.Builder<MemoryMetrics> totalMemoryBuilder = mock(Gauge.Builder.class);
        when(totalMemoryBuilder.description("Total amount of memory available to the JVM in bytes")).thenReturn(totalMemoryBuilder);
        when(totalMemoryBuilder.baseUnit("bytes")).thenReturn(totalMemoryBuilder);
        when(totalMemoryBuilder.register(meterRegistry)).thenReturn(totalMemoryGauge);

        memoryMetrics = new MemoryMetrics(meterRegistry);

        Field memoryMXBeanField = MemoryMetrics.class.getDeclaredField("memoryMXBean");
        memoryMXBeanField.setAccessible(true);
        memoryMXBeanField.set(memoryMetrics, memoryMXBean);
    }

    @Test
    void constructor_shouldRegisterGauges() throws NoSuchFieldException, IllegalAccessException {
        assertNotNull(memoryMetrics);

        Field meterRegistryField = MemoryMetrics.class.getDeclaredField("meterRegistry");
        meterRegistryField.setAccessible(true);
        assertEquals(meterRegistry, meterRegistryField.get(memoryMetrics));

        Field memoryMXBeanField = MemoryMetrics.class.getDeclaredField("memoryMXBean");
        memoryMXBeanField.setAccessible(true);
        assertEquals(memoryMXBean, memoryMXBeanField.get(memoryMetrics));
    }

    @Test
    void updateMemoryMetrics_shouldUpdateGauges() {
        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(heapMemoryUsage);
        when(memoryMXBean.getNonHeapMemoryUsage()).thenReturn(nonHeapMemoryUsage);
        when(heapMemoryUsage.getUsed()).thenReturn(500L);
        when(nonHeapMemoryUsage.getUsed()).thenReturn(300L);
        when(heapMemoryUsage.getMax()).thenReturn(1000L);

        memoryMetrics.updateMemoryMetrics();

        verify(meterRegistry).gauge(eq("app_memory_used_bytes"), eq(memoryMetrics), any());
        verify(meterRegistry).gauge(eq("app_memory_total_bytes"), eq(memoryMetrics), any());
    }

    @Test
    void getUsedMemory_shouldReturnSumOfHeapAndNonHeap() throws Exception {
        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(heapMemoryUsage);
        when(memoryMXBean.getNonHeapMemoryUsage()).thenReturn(nonHeapMemoryUsage);
        when(heapMemoryUsage.getUsed()).thenReturn(500L);
        when(nonHeapMemoryUsage.getUsed()).thenReturn(300L);

        Method getUsedMemoryMethod = MemoryMetrics.class.getDeclaredMethod("getUsedMemory");
        getUsedMemoryMethod.setAccessible(true);
        double usedMemory = (double) getUsedMemoryMethod.invoke(memoryMetrics);

        assertEquals(800.0, usedMemory, 0.001);
    }

    @Test
    void getTotalMemory_shouldReturnHeapMax() throws Exception {
        when(memoryMXBean.getHeapMemoryUsage()).thenReturn(heapMemoryUsage);
        when(heapMemoryUsage.getMax()).thenReturn(1000L);

        Method getTotalMemoryMethod = MemoryMetrics.class.getDeclaredMethod("getTotalMemory");
        getTotalMemoryMethod.setAccessible(true);
        double totalMemory = (double) getTotalMemoryMethod.invoke(memoryMetrics);

        assertEquals(1000.0, totalMemory, 0.001);
    }
}