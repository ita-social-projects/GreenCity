package greencity.service;

import greencity.config.ScheduleConfig;
import greencity.repository.HabitAssignRepo;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StatusHabitSchedulerTest {

    @Mock
    @SpyBean
    private ScheduleConfig scheduleConfig;

    @Test
    public void whenWaitOneSecond_thenScheduledIsCalledAtLeastTenTimes() {
        await()
            .atMost(4, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(scheduleConfig, atMost(0)).checkExpired());
    }
}
