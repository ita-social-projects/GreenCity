package greencity.metrics;

import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CPUMetricsTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private OperatingSystemMXBean osBean;

    @Mock
    private Gauge gauge;

    private CPUMetrics cpuMetrics;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Gauge.Builder<CPUMetrics> gaugeBuilder = mock(Gauge.Builder.class);
        when(gaugeBuilder.description("CPU usage of the application in percent")).thenReturn(gaugeBuilder);
        when(gaugeBuilder.baseUnit("percent")).thenReturn(gaugeBuilder);
        when(gaugeBuilder.register(meterRegistry)).thenReturn(gauge);

        cpuMetrics = new CPUMetrics(meterRegistry);

        Field osBeanField = CPUMetrics.class.getDeclaredField("osBean");
        osBeanField.setAccessible(true);
        osBeanField.set(cpuMetrics, osBean);
    }

    @Test
    void constructor_shouldRegisterGauge() throws NoSuchFieldException, IllegalAccessException {
        assertNotNull(cpuMetrics);

        Field meterRegistryField = CPUMetrics.class.getDeclaredField("meterRegistry");
        meterRegistryField.setAccessible(true);
        assertEquals(meterRegistry, meterRegistryField.get(cpuMetrics));

        Field osBeanField = CPUMetrics.class.getDeclaredField("osBean");
        osBeanField.setAccessible(true);
        assertEquals(osBean, osBeanField.get(cpuMetrics));
    }

    @Test
    void updateCPUMetrics_shouldUpdateGauge() {
        when(osBean.getProcessCpuLoad()).thenReturn(0.25);

        cpuMetrics.updateCPUMetrics();

        verify(meterRegistry).gauge(eq("app_cpu_usage_percent"), eq(cpuMetrics), any());
    }

    @Test
    void getCpuUsage_shouldReturnPositiveCpuLoad() throws Exception {
        when(osBean.getProcessCpuLoad()).thenReturn(0.5);

        Method getCpuUsageMethod = CPUMetrics.class.getDeclaredMethod("getCpuUsage");
        getCpuUsageMethod.setAccessible(true);
        double cpuUsage = (double) getCpuUsageMethod.invoke(cpuMetrics);

        assertEquals(50.0, cpuUsage, 0.001);
    }

    @Test
    void getCpuUsage_shouldReturnZeroForNegativeCpuLoad() throws Exception {
        when(osBean.getProcessCpuLoad()).thenReturn(-0.1);

        Method getCpuUsageMethod = CPUMetrics.class.getDeclaredMethod("getCpuUsage");
        getCpuUsageMethod.setAccessible(true);
        double cpuUsage = (double) getCpuUsageMethod.invoke(cpuMetrics);

        assertEquals(0.0, cpuUsage, 0.001);
    }
}