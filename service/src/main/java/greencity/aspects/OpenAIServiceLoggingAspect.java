package greencity.aspects;

import greencity.log.OpenAILogMessages;
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
        log.info(OpenAILogMessages.OPENAI_REQUEST_INITIATED, prompt);
        log.debug(OpenAILogMessages.OPENAI_REQUEST_PARAMETER_VALIDATION);
        log.trace(OpenAILogMessages.START_REQUEST_PARAMETER_VALIDATION, prompt);
    }

    @AfterReturning(value = "execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..))", returning = "response")
    public void logAfterMakeRequest(String response) {
        log.info(OpenAILogMessages.OPENAI_RESPONSE_RECEIVED, response);
        log.debug(OpenAILogMessages.RESPONSE_DETAILS, response);
        log.trace(OpenAILogMessages.FULL_RESPONSE_FROM_OPENAI, response);
    }

    @AfterThrowing(value = "execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..))", throwing = "exception")
    public void logErrorInMakeRequest(Exception exception) {
        log.error(OpenAILogMessages.OPENAI_REQUEST_FAILED, exception);
        log.debug(OpenAILogMessages.STACK_TRACE_OF_THE_ERROR, exception);
        log.trace(OpenAILogMessages.COMPLETE_EXCEPTION_STACK_TRACE, exception);
    }

    @Around("execution(public * greencity.service.OpenAIServiceImpl.makeRequest(..)) && args(prompt)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, String prompt) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.trace(OpenAILogMessages.METHOD_EXECUTION_STARTED, LocalDateTime.now());

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info(OpenAILogMessages.METHOD_EXECUTED_IN_MS, duration);
        log.debug(OpenAILogMessages.EXECUTION_TIME_FOR_METHOD, duration, prompt);

        return result;
    }
}
