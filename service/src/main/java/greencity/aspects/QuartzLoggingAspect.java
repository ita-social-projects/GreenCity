package greencity.aspects;

import greencity.constant.QuartzConstants;
import java.util.Date;
import java.util.UUID;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

@Aspect
@Component
@Slf4j
public class QuartzLoggingAspect {
    @Value("${cron.generateEcoNews}")
    private String cronExpression;

    @Pointcut("execution(* greencity.config.QuartzConfig.ecoNewsGenerationTrigger(..))")
    public void ecoNewsGenerationTriggerMethods() {
    }

    @After("ecoNewsGenerationTriggerMethods()")
    public void logCronExpressionWarning() {
        log.warn(QuartzConstants.CRON_EXPRESSION_WARNING, cronExpression);
    }

    @AfterThrowing("ecoNewsGenerationTriggerMethods()")
    public void logCronExpressionCriticalError() {
        log.error(QuartzConstants.CRON_EXPRESSION_CRITICAL_ERROR, cronExpression);
    }

    @Before("execution(* greencity.config.QuartzConfig.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.trace(QuartzConstants.METHOD_ENTERING, methodName, Arrays.toString(joinPoint.getArgs()));
    }

    @After("execution(* greencity.config.QuartzConfig.*(..))")
    public void logCompletionDetails(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.debug(QuartzConstants.METHOD_COMPLETED_EXECUTION, methodName, Arrays.toString(joinPoint.getArgs()));
    }

    @Around("execution(* greencity.config.QuartzConfig.scheduler(..))")
    public Object logAroundScheduler(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        MDC.put(QuartzConstants.MDC_REQUEST_ID, requestId);

        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.trace(QuartzConstants.METHOD_CALLED, methodName, Arrays.toString(args));
        log.debug(QuartzConstants.CRON_EXPRESSION, cronExpression);
        log.debug(QuartzConstants.CRON_VALIDATION_STARTED);
        log.info(QuartzConstants.CRON_EXECUTION_STARTED, cronExpression);

        Object result;
        try {
            result = joinPoint.proceed();

            if (result instanceof Scheduler scheduler) {
                Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(
                    QuartzConstants.ECO_NEWS_GENERATION_TRIGGER_IDENTITY));
                Date next = trigger.getNextFireTime();
                Date previous = trigger.getPreviousFireTime();

                log.info("[SCHEDULED] Next fire time: {}", next != null ? next : "null");
                log.info("[EXECUTED] Previous fire time: {}", previous != null ? previous : "Never executed yet");
            }

            long endTime = System.currentTimeMillis();
            log.info(QuartzConstants.METHOD_SUCCESS, methodName, (endTime - startTime), result);
            log.debug(QuartzConstants.METHOD_CRON_VALIDATION_SUCCESS);
            log.info(QuartzConstants.METHOD_EXECUTION_TIME, methodName, (endTime - startTime));
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            log.error(QuartzConstants.METHOD_EXCEPTION, methodName, (endTime - startTime),
                throwable.getMessage(), throwable);
            log.debug(QuartzConstants.METHOD_CRON_VALIDATION_FAILED_EXCEPTION, throwable.getMessage());
            throw throwable;
        } finally {
            MDC.remove(QuartzConstants.MDC_REQUEST_ID);
        }

        log.debug(QuartzConstants.METHOD_FINISHED, methodName, Arrays.toString(args));
        return result;
    }
}
