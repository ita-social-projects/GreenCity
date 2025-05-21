package greencity.aspects;

import static greencity.constant.OpenAILogMessages.*;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OpenAIServiceLoggingAspect {

    @Before("execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..)) && args(prompt)")
    public void logBeforeMakeRequest(String prompt) {
        log.info(OPENAI_REQUEST_INITIATED, prompt);
        log.debug(OPENAI_REQUEST_PARAMETER_VALIDATION);
        log.trace(START_REQUEST_PARAMETER_VALIDATION, prompt);
    }

    @AfterReturning(value = "execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..))", returning = "response")
    public void logAfterMakeRequest(String response) {
        log.info(OPENAI_RESPONSE_RECEIVED, response);
        log.debug(RESPONSE_DETAILS, response);
        log.trace(FULL_RESPONSE_FROM_OPENAI, response);
    }

    @AfterThrowing(value = "execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..))", throwing = "exception")
    public void logErrorInMakeRequest(Exception exception) {
        log.error(OPENAI_REQUEST_FAILED, exception);
        log.debug(STACK_TRACE_OF_THE_ERROR, exception);
        log.trace(COMPLETE_EXCEPTION_STACK_TRACE, exception);
    }

    @Around("execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..)) && args(prompt)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, String prompt) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.trace(METHOD_EXECUTION_STARTED, LocalDateTime.now());

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info(METHOD_EXECUTED_IN_MS, duration);
        log.debug(EXECUTION_TIME_FOR_METHOD, duration, prompt);

        return result;
    }
}
