package greencity.aspects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Date;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@ExtendWith(MockitoExtension.class)
class QuartzLoggingAspectTest {

    @Mock
    private JoinPoint joinPoint;
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Trigger trigger;
    @Mock
    private Signature signature;
    @InjectMocks
    private QuartzLoggingAspect aspect;

    private final String cronExpression = "0 0 12 * * ?";

    @BeforeEach
    void setUp() throws Exception {
        var field = QuartzLoggingAspect.class.getDeclaredField("cronExpression");
        field.setAccessible(true);
        field.set(aspect, cronExpression);
    }

    @Test
    void logBeforeMethodExecutionTest() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg", 123});

        assertDoesNotThrow(() -> aspect.logBeforeMethodExecution(joinPoint));
    }

    @Test
    void logCompletionDetailsTest() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg", 123});

        assertDoesNotThrow(() -> aspect.logCompletionDetails(joinPoint));
    }

    @Test
    void logCronExpressionWarningTest() {
        assertDoesNotThrow(() -> aspect.logCronExpressionWarning());
    }

    @Test
    void logCronExpressionCriticalErrorTest() {
        assertDoesNotThrow(() -> aspect.logCronExpressionCriticalError());
    }

    @Test
    void logAroundSchedulerWithValidSchedulerTest() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(scheduler);

        when(scheduler.getTrigger(TriggerKey.triggerKey("ecoNewsGenerationTrigger"))).thenReturn(trigger);
        when(trigger.getNextFireTime()).thenReturn(new Date());
        when(trigger.getPreviousFireTime()).thenReturn(new Date(System.currentTimeMillis() - 10000));

        Object result = aspect.logAroundScheduler(proceedingJoinPoint);

        assertEquals(scheduler, result);
        verify(scheduler).getTrigger(TriggerKey.triggerKey("ecoNewsGenerationTrigger"));
    }

    @Test
    void logAroundSchedulerWithNotSchedulerTest() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(new Object());

        aspect.logAroundScheduler(proceedingJoinPoint);

        verify(scheduler, never()).getTrigger(TriggerKey.triggerKey("ecoNewsGenerationTrigger"));
    }

    @Test
    void logAroundSchedulerWhenExceptionTest() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Simulated failure"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> aspect.logAroundScheduler(proceedingJoinPoint));

        assertEquals("Simulated failure", exception.getMessage());
    }
}