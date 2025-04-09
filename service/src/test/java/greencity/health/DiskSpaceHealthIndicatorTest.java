package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiskSpaceHealthIndicatorTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private File root;

    private DiskSpaceHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new DiskSpaceHealthIndicator(meterRegistry, root);
    }

    @Test
    void testHealthUpWhenFreeSpaceIsAboveThreshold() {
        long totalSpace = 1000L;
        long freeSpace = 200L;
        double expectedFreeSpacePercentage = (double) freeSpace / totalSpace * 100;

        when(root.getTotalSpace()).thenReturn(totalSpace);
        when(root.getFreeSpace()).thenReturn(freeSpace);
        when(meterRegistry.gauge(eq("app_disk_space_health"), eq(1))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Sufficient disk space available", health.getDetails().get("diskSpace"));
        assertEquals(expectedFreeSpacePercentage, (Double) health.getDetails().get("freeSpacePercentage"), 0.01);
        assertEquals(freeSpace, health.getDetails().get("freeSpaceBytes"));

        verify(meterRegistry).gauge("app_disk_space_health", 1);
        verify(root).getTotalSpace();
        verify(root).getFreeSpace();
    }

    @Test
    void testHealthDownWhenFreeSpaceIsBelowThreshold() {
        long totalSpace = 1000L;
        long freeSpace = 50L;
        double expectedFreeSpacePercentage = (double) freeSpace / totalSpace * 100;

        when(root.getTotalSpace()).thenReturn(totalSpace);
        when(root.getFreeSpace()).thenReturn(freeSpace);
        when(meterRegistry.gauge(eq("app_disk_space_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Low disk space detected", health.getDetails().get("diskSpace"));
        assertEquals(expectedFreeSpacePercentage, (Double) health.getDetails().get("freeSpacePercentage"), 0.01);
        assertEquals(freeSpace, health.getDetails().get("freeSpaceBytes"));

        verify(meterRegistry).gauge("app_disk_space_health", 0);
        verify(root).getTotalSpace();
        verify(root).getFreeSpace();
    }

    @Test
    void testHealthDownWhenExceptionOccurs() {
        RuntimeException exception = new RuntimeException("File access error");
        when(root.getTotalSpace()).thenThrow(exception);
        when(meterRegistry.gauge(eq("app_disk_space_health"), eq(0))).thenReturn(null);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Error checking disk space", health.getDetails().get("diskSpace"));
        assertEquals("File access error", health.getDetails().get("error"));

        verify(meterRegistry).gauge("app_disk_space_health", 0);
        verify(root).getTotalSpace();
        verify(root, never()).getFreeSpace();
    }
}