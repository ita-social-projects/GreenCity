package greencity.health;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitTrackingHealthIndicatorTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Query newHabitsQuery;

    @Mock
    private Query newAssignsQuery;

    private HabitTrackingHealthIndicator habitTrackingHealthIndicator;

    @BeforeEach
    public void setUp() {
        habitTrackingHealthIndicator = new HabitTrackingHealthIndicator(meterRegistry, 10);

        try {
            java.lang.reflect.Field entityManagerField =
                HabitTrackingHealthIndicator.class.getDeclaredField("entityManager");
            entityManagerField.setAccessible(true);
            entityManagerField.set(habitTrackingHealthIndicator, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set entityManager", e);
        }
    }

    @Test
    public void testHealthUp_WhenActivityCountIsAboveThreshold() {
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habits WHERE created_at >= :last24Hours"))
                .thenReturn(newHabitsQuery);
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habit_assign WHERE create_date >= :last24Hours"))
                .thenReturn(newAssignsQuery);
        when(newHabitsQuery.setParameter(anyString(), any(LocalDateTime.class))).thenReturn(newHabitsQuery);
        when(newAssignsQuery.setParameter(anyString(), any(ZonedDateTime.class))).thenReturn(newAssignsQuery);
        when(newHabitsQuery.getSingleResult()).thenReturn(5L);
        when(newAssignsQuery.getSingleResult()).thenReturn(5L);

        Health health = habitTrackingHealthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Habit tracking is active", health.getDetails().get("habitTracking"));
        assertEquals(5L, health.getDetails().get("newHabitsLast24h"));
        assertEquals(5L, health.getDetails().get("newAssignsLast24h"));
        assertEquals(10L, health.getDetails().get("totalActivityLast24h"));

        verify(meterRegistry).gauge("app_habit_tracking_health", 1);
    }

    @Test
    public void testHealthOutOfService_WhenActivityCountIsBelowThreshold() {
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habits WHERE created_at >= :last24Hours"))
                .thenReturn(newHabitsQuery);
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habit_assign WHERE create_date >= :last24Hours"))
                .thenReturn(newAssignsQuery);
        when(newHabitsQuery.setParameter(anyString(), any(LocalDateTime.class))).thenReturn(newHabitsQuery);
        when(newAssignsQuery.setParameter(anyString(), any(ZonedDateTime.class))).thenReturn(newAssignsQuery);
        when(newHabitsQuery.getSingleResult()).thenReturn(2L);
        when(newAssignsQuery.getSingleResult()).thenReturn(3L);

        Health health = habitTrackingHealthIndicator.health();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
        assertEquals("Low habit tracking activity", health.getDetails().get("habitTracking"));
        assertEquals(2L, health.getDetails().get("newHabitsLast24h"));
        assertEquals(3L, health.getDetails().get("newAssignsLast24h"));
        assertEquals(5L, health.getDetails().get("totalActivityLast24h"));
        assertEquals(10, health.getDetails().get("minThreshold"));

        verify(meterRegistry).gauge("app_habit_tracking_health", 0);
    }

    @Test
    public void testHealthDown_WhenExceptionOccurs() {
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habits WHERE created_at >= :last24Hours"))
                .thenReturn(newHabitsQuery);
        when(newHabitsQuery.setParameter(anyString(), any(LocalDateTime.class))).thenReturn(newHabitsQuery);
        when(newHabitsQuery.getSingleResult()).thenThrow(new RuntimeException("Database error"));

        Health health = habitTrackingHealthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Error checking habit tracking", health.getDetails().get("habitTracking"));
        assertEquals("Database error", health.getDetails().get("error"));

        verify(meterRegistry).gauge("app_habit_tracking_health", 0);
    }

    @Test
    public void testHealthWithZeroActivity() {
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habits WHERE created_at >= :last24Hours"))
                .thenReturn(newHabitsQuery);
        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM habit_assign WHERE create_date >= :last24Hours"))
                .thenReturn(newAssignsQuery);
        when(newHabitsQuery.setParameter(anyString(), any(LocalDateTime.class))).thenReturn(newHabitsQuery);
        when(newAssignsQuery.setParameter(anyString(), any(ZonedDateTime.class))).thenReturn(newAssignsQuery);
        when(newHabitsQuery.getSingleResult()).thenReturn(0L);
        when(newAssignsQuery.getSingleResult()).thenReturn(0L);

        Health health = habitTrackingHealthIndicator.health();

        assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
        assertEquals("Low habit tracking activity", health.getDetails().get("habitTracking"));
        assertEquals(0L, health.getDetails().get("newHabitsLast24h"));
        assertEquals(0L, health.getDetails().get("newAssignsLast24h"));
        assertEquals(0L, health.getDetails().get("totalActivityLast24h"));
        assertEquals(10, health.getDetails().get("minThreshold"));

        verify(meterRegistry).gauge("app_habit_tracking_health", 0);
    }
}