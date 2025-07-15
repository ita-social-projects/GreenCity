package greencity.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Trigger;

@ExtendWith(MockitoExtension.class)
class LoggingJobListenerTest {

    @Mock
    private JobExecutionContext context;
    @Mock
    private Trigger trigger;
    @Mock
    private JobDetail jobDetail;
    @Mock
    private JobKey jobKey;
    @InjectMocks
    private LoggingJobListener listener;

    @Test
    void getNameValidTest() {
        assertEquals("LoggingJobListener", listener.getName());
    }

    @Test
    void jobToBeExecutedWithFireTimesTest() {
        Date previous = new Date(System.currentTimeMillis() - 10_000);
        Date next = new Date(System.currentTimeMillis() + 10_000);

        when(context.getTrigger()).thenReturn(trigger);
        when(trigger.getPreviousFireTime()).thenReturn(previous);
        when(trigger.getNextFireTime()).thenReturn(next);

        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getKey()).thenReturn(jobKey);

        assertDoesNotThrow(() -> listener.jobToBeExecuted(context));
    }

    @Test
    void jobToBeExecutedWithNullFireTimesTest() {
        when(context.getTrigger()).thenReturn(trigger);
        when(trigger.getPreviousFireTime()).thenReturn(null);
        when(trigger.getNextFireTime()).thenReturn(null);

        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getKey()).thenReturn(jobKey);

        assertDoesNotThrow(() -> listener.jobToBeExecuted(context));
    }

    @Test
    void jobExecutionVetoedTest() {
        Date fireTime = new Date();
        Date scheduledTime = new Date();

        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getKey()).thenReturn(jobKey);
        when(jobKey.toString()).thenReturn("jobKey");

        when(context.getFireTime()).thenReturn(fireTime);
        when(context.getScheduledFireTime()).thenReturn(scheduledTime);

        assertDoesNotThrow(() -> listener.jobExecutionVetoed(context));
    }

    @Test
    void jobWasExecutedSuccessTest() {
        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getKey()).thenReturn(jobKey);

        assertDoesNotThrow(() -> listener.jobWasExecuted(context, null));
    }

    @Test
    void jobWasExecutedWithExceptionTest() {
        JobExecutionException exception = new JobExecutionException("Job failed");

        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getKey()).thenReturn(jobKey);

        assertDoesNotThrow(() -> listener.jobWasExecuted(context, exception));
    }
}