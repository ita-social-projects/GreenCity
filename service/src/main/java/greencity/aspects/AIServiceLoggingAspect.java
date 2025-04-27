package greencity.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.constant.LoggingConstants.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AIServiceLoggingAspect {
    private final ObjectMapper objectMapper;

    /**
     * Pointcut that matches all methods in the AIService class. This will allow us
     * to hook into method invocations within the AIService class.
     */
    @Pointcut("execution(* greencity.service.AIService.*(..))")
    public void aiServiceMethods() {
    }

    /**
     * Logs information before the execution of methods in the AIService class. Logs
     * method name, class name, thread name, method modifiers, return type,
     * arguments, and the caller information (if available).
     *
     * @param joinPoint the join point provides reflective access to the method
     *                  being invoked.
     */
    @Before("aiServiceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        Method method = getMethodFromJoinPoint(joinPoint);
        String className = method.getDeclaringClass().getName();
        String threadName = Thread.currentThread().getName();

        log.info(METHOD_INVOCATION,
            method.getName(),
            className,
            threadName);
        log.debug(METHOD_MODIFIERS,
            Modifier.toString(method.getModifiers()));
        log.debug(RETURN_TYPE,
            method.getReturnType().getSimpleName());
        logMethodArguments(joinPoint, method);
        logCallerInfo();
    }

    /**
     * Logs the returned result of the method after it has executed. Logs the
     * method's return type and the result itself. If the result is null, it logs a
     * null value.
     *
     * @param joinPoint the join point provides reflective access to the method
     *                  being invoked.
     * @param result    the result returned by the method.
     */
    @AfterReturning(value = "aiServiceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Method method = getMethodFromJoinPoint(joinPoint);

        if (result != null) {
            log.debug(RETURN_VALUE,
                method.getReturnType().getSimpleName(),
                safeToJson(result));
        } else {
            log.debug(RETURN_VALUE_NULL);
        }
    }

    /**
     * Logs the execution time of the method. This will log the duration taken to
     * execute the method in milliseconds. It also logs an exception if one is
     * thrown during the method execution.
     *
     * @param proceedingJoinPoint the proceeding join point that allows the method
     *                            to be executed.
     * @return the result returned by the method if no exceptions occur.
     * @throws Throwable if the method execution throws an exception.
     */
    @Around("aiServiceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Instant start = Instant.now();
        Method method = getMethodFromJoinPoint(proceedingJoinPoint);

        try {
            Object result = proceedingJoinPoint.proceed();
            logExecutionDuration(start, method.getName());
            return result;
        } catch (Throwable ex) {
            logExecutionDuration(start, method.getName(), ex);
            throw ex;
        }
    }

    /**
     * Logs any exceptions thrown by methods in the AIService class. It captures the
     * method name, exception message, and stack trace for debugging purposes.
     *
     * @param joinPoint the join point provides reflective access to the method
     *                  being invoked.
     * @param exception the exception thrown by the method.
     */
    @AfterThrowing(value = "aiServiceMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        Method method = getMethodFromJoinPoint(joinPoint);
        log.error(METHOD_THROW_EXCEPTION,
            method.getName(),
            exception.getMessage(),
            exception);
    }

    /**
     * Safely converts an object to a JSON string representation using Jackson's
     * ObjectMapper. If the object is too large, it truncates the string to the
     * first 1000 characters for readability.
     *
     * @param obj The object to be serialized.
     * @return The JSON string representation of the object.
     */
    private String safeToJson(Object obj) {
        if (obj == null) {
            return NULL;
        }
        try {
            String jsonString = objectMapper.writeValueAsString(obj);
            return jsonString.length() > 1000
                ? jsonString.substring(0, 1000) + TRUNCATED
                : jsonString;
        } catch (JsonProcessingException e) {
            return ERROR_SERIALIZING_TO_JSON;
        }
    }

    /**
     * Retrieves the Method object from the provided join point.
     *
     * @param joinPoint the join point provides reflective access to the method
     *                  being invoked.
     * @return the Method object associated with the join point.
     */
    private Method getMethodFromJoinPoint(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getMethod();
    }

    /**
     * Logs the method arguments. For each argument, it logs its index, name, type,
     * and value (as a JSON string).
     *
     * @param joinPoint the join point provides reflective access to the method
     *                  being invoked.
     * @param method    the method being invoked.
     */
    private void logMethodArguments(JoinPoint joinPoint, Method method) {
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature())
            .getParameterNames();
        Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < args.length; i++) {
            log.debug(ARGUMENT, i, parameterNames[i],
                parameterTypes[i].getSimpleName(),
                safeToJson(args[i]));
        }
    }

    /**
     * Logs the caller information by analyzing the stack trace. If available, it
     * logs the class name, method name, and line number of the calling method.
     */
    private void logCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            log.debug(CALLED_FROM,
                caller.getClassName(),
                caller.getMethodName(),
                caller.getLineNumber());
        }
    }

    /**
     * Logs the execution duration of a method, i.e., how long the method took to
     * execute.
     *
     * @param start      the timestamp when the method execution started.
     * @param methodName the name of the method being invoked.
     */
    private void logExecutionDuration(Instant start, String methodName) {
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        log.info(METHOD_EXECUTION_TIME, methodName, duration);
    }

    /**
     * Logs the execution duration along with the exception information if an
     * exception occurs.
     *
     * @param start      the timestamp when the method execution started.
     * @param methodName the name of the method being invoked.
     * @param ex         the exception thrown during the method execution.
     */
    private void logExecutionDuration(Instant start, String methodName, Throwable ex) {
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        log.error(EXCEPTION_IN_METHOD,
            methodName,
            duration,
            ex.getMessage(),
            ex);
    }
}
