package greencity.aspects;

import static greencity.constant.AIEcoNewsRelevanceConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.enums.RelevanceLevel;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class AIEcoNewsRelevanceLoggingAspect {
    @Pointcut("execution(public * greencity.service.AIEcoNewsRelevanceServiceImpl.*(..)) && !within(AIEcoNewsRelevanceLoggingAspect)")
    public void aiEcoNewsRelevanceMethods() {}

    /**
     * Logs method entry with detailed parameter information.
     *
     * @param joinPoint the join point representing the method call
     */
    @Before("aiEcoNewsRelevanceMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info(SEPARATOR);
        log.info(LOG_ENTER_METHOD,
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName(),
            ZonedDateTime.now().format(DATE_FORMATTER));

        logMethodArguments(joinPoint.getArgs());
    }

    /**
     * Specifically logs retry attempts for the calculateAIRelevanceScore method.
     */
    @Before("execution(public * greencity.service.AIEcoNewsRelevanceServiceImpl.calculateAIRelevanceScore(..))")
    public void logRetryAttempt(JoinPoint joinPoint) {
        try {
            EcoNewsDto ecoNews = findFirstArgumentOfType(joinPoint.getArgs(), EcoNewsDto.class);
            Long newsId = ecoNews != null ? ecoNews.getId() : null;
            log.info(LOG_RETRY, newsId);
        } catch (Exception e) {
            log.warn(LOG_RETRY_ERROR, e);
        }
    }

    /**
     * Specifically logs batch processing information for precompute and recalculate methods.
     */
    @Before("execution(public * greencity.service.AIEcoNewsRelevanceServiceImpl.precomputeRelevanceForNewNews(..)) || " +
        "execution(public * greencity.service.AIEcoNewsRelevanceServiceImpl.recalculateRelevanceForUser(..))")
    public void logBatchProcessingStart(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        if (METHOD_PRECOMPUTE.equals(methodName)) {
            EcoNewsDto news = findFirstArgumentOfType(joinPoint.getArgs(), EcoNewsDto.class);
            if (news != null) {
                log.info(LOG_BATCH_PRECOMPUTE,
                    news.getId());
            }
        } else if (METHOD_RECALCULATE.equals(methodName)) {
            Long userId = findFirstArgumentOfType(joinPoint.getArgs(), Long.class);
            log.info(LOG_BATCH_RECALCULATE,
                userId);
        }
    }

    /**
     * Logs method exit with separation for improved readability.
     *
     * @param joinPoint the join point representing the method call
     */
    @After("aiEcoNewsRelevanceMethods()")
    public void logMethodExit(JoinPoint joinPoint) {
        log.info(LOG_EXIT_METHOD,
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName());
        log.info(SEPARATOR);
    }

    /**
     * Logs detailed information about exceptions thrown during method execution.
     *
     * @param joinPoint the join point representing the method call
     * @param ex the exception that was thrown
     */
    @AfterThrowing(pointcut = "aiEcoNewsRelevanceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();

        log.error(LOG_EXCEPTION_DETAILS,
            joinPoint.getTarget().getClass().getSimpleName(),
            methodName);
        log.error(LOG_EXCEPTION_TYPE, ex.getClass().getName());
        log.error(LOG_EXCEPTION_MESSAGE, ex.getMessage());

        if (ex instanceof RuntimeException || shouldLogStackTrace(ex)) {
            log.error(LOG_EXCEPTION_STACKTRACE, ex);
        }
        log.error(LOG_EXCEPTION_ARGS);
        logMethodArguments(joinPoint.getArgs());
        Throwable rootCause = getRootCause(ex);

        if (rootCause != ex) {
            log.error(LOG_EXCEPTION_CAUSE,
                rootCause.getClass().getName(),
                rootCause.getMessage());
        }
        log.error(SEPARATOR);
    }

    /**
     * Logs exceptions with basic details and stack trace.
     *
     * @param joinPoint the join point representing the method call
     * @param ex the exception that was thrown
     */
    @AfterThrowing(pointcut = "aiEcoNewsRelevanceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        log.error(LOG_EXCEPTION_IN_METHOD, className, methodName,
            ex.getClass().getSimpleName(), ex.getMessage());

        logMethodArguments(joinPoint.getArgs());

        log.debug(LOG_EXCEPTION_FULL_STACKTRACE, className, methodName, ex);

    }

    /**
     * Logs the return value of a method after its execution.
     *
     * @param joinPoint the join point representing the method call
     * @param result the result returned by the method
     */
    @AfterReturning(pointcut = "aiEcoNewsRelevanceMethods()", returning = "result")
    public void logReturnValue(JoinPoint joinPoint, Object result) {
        if (result == null) return;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        String returnValue = truncate(String.valueOf(result));
        log.info(LOG_RETURN_VALUE, className, methodName, returnValue);
    }

    /**
     * Logs detailed method execution, performance metrics, and return values.
     *
     * @param joinPoint the join point representing the method call
     * @return the result from the method execution
     * @throws Throwable if the method execution throws an exception
     */
    @Around("aiEcoNewsRelevanceMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        StopWatch stopWatch = new StopWatch(methodName);
        stopWatch.start();
        long beforeMemory = getUsedMemory();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            long afterMemory = getUsedMemory();
            long memoryDiff = afterMemory - beforeMemory;

            log.info(LOG_PERFORMANCE,
                joinPoint.getTarget().getClass().getSimpleName(),
                methodName,
                stopWatch.getTotalTimeMillis(),
                memoryDiff / BYTES_IN_KILOBYTE);
            logReturnValue(result);
            return result;
        } catch (Throwable ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            log.error(LOG_EXCEPTION,
                joinPoint.getTarget().getClass().getSimpleName(),
                methodName,
                ex.getClass().getName(),
                ex.getMessage());

            throw ex;
        }
    }

    /**
     * Helper method to find the first argument of a specific type from an array of arguments.
     *
     * @param <T> the type to search for
     * @param args the array of arguments to search
     * @param type the class of the type to search for
     * @return the first argument of the specified type, or null if none found
     */
    @SuppressWarnings("unchecked")
    private <T> T findFirstArgumentOfType(Object[] args, Class<T> type) {
        if (args == null) return null;

        for (Object arg : args) {
            if (arg != null && type.isAssignableFrom(arg.getClass())) {
                return (T) arg;
            }
        }
        return null;
    }

    /**
     * Logs method arguments with type-specific handling.
     *
     * @param args the arguments passed to the method
     */
    private void logMethodArguments(Object[] args) {
        if (args == null || args.length == DEFAULT_INDEX) {
            log.info(LOG_NO_ARGUMENTS);
            return;
        }
        log.info(LOG_METHOD_ARGS);
        for (int i = DEFAULT_INDEX; i < args.length; i++) {
            logSingleArgument(args[i], i);
        }
    }

    /**
     * Logs a single argument with appropriate formatting based on its type.
     *
     * @param arg the argument to log
     * @param index the argument's position
     */
    private void logSingleArgument(Object arg, int index) {
        if (arg == null) {
            log.info(LOG_ARG_NULL, index);
            return;
        }
        switch (arg) {
            case EcoNewsDto ecoNews -> logEcoNewsArgument(ecoNews, index);
            case List<?> list -> logListArgument(list, index);
            case Long value -> log.info(LOG_ARG_LONG, index, value);
            default -> log.info(LOG_ARG_GENERIC, index, arg.getClass().getSimpleName(), arg);
        }
    }

    /**
     * Logs an EcoNewsDto argument.
     *
     * @param news the EcoNewsDto argument
     * @param index the argument's position
     */
    private void logEcoNewsArgument(EcoNewsDto news, int index) {
        String title = news.getTitle();
        String truncatedTitle = title != null
            ? title.substring(DEFAULT_INDEX,
            Math.min(MAX_TITLE_PREVIEW_LENGTH, title.length()))
            + TRUNCATION_SUFFIX
            : NULL_STRING;

        int contentLength = news.getContent() != null
            ? news.getContent().length()
            : DEFAULT_INDEX;

        log.info(LOG_ARG_ECONEWS, index, news.getId(), truncatedTitle, contentLength,
            news.getAuthor().getId(), news.getCreationDate());
    }

    /**
     * Logs a List argument.
     *
     * @param list the List argument
     * @param index the argument's position
     */
    private void logListArgument(List<?> list, int index) {
        String typeName = list.isEmpty()
            ? UNKNOWN_TYPE
            : list.getFirst().getClass().getSimpleName();

        String listContent = list.stream()
            .map(Object::toString)
            .collect(Collectors.joining(COMMA_SEPARATOR));

        String truncatedContent = listContent.length() > MAX_LOGGED_LIST_CONTENT_LENGTH
            ? listContent.substring(DEFAULT_INDEX, MAX_LOGGED_LIST_CONTENT_LENGTH)
            + TRUNCATION_SUFFIX
            : listContent;

        log.info(LOG_ARG_LIST, index, typeName, list.size(), truncatedContent);
    }

    /**
     * Gets the current used memory in bytes.
     *
     * @return used memory in bytes
     */
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Logs method return value with type-specific handling.
     *
     * @param result the result returned by the method
     */
    private void logReturnValue(Object result) {
        if (result == null) {
            log.info(LOG_RETURN_NULL);
            return;
        }

        switch (result) {
            case Double score -> log.info(LOG_RETURN_SCORE,
                String.format(FORMAT_FOUR_DECIMALS, score),
                classifyRelevanceScore(score));
            case List<?> list -> log.info(LOG_RETURN_LIST, list.size());
            default -> log.info(LOG_RETURN_GENERIC, result, result.getClass().getSimpleName());
        }
    }

    /**
     * Classifies a relevance score for better understanding in logs.
     *
     * @param score the relevance score to classify
     * @return a human-readable classification of the score
     */
    private String classifyRelevanceScore(double score) {
        return RelevanceLevel.fromScore(score).getDescription();
    }

    /**
     * Determines if stack trace should be logged for a given exception type.
     *
     * @param ex the exception to check
     * @return true if stack trace should be logged
     */
    private boolean shouldLogStackTrace(Throwable ex) {
        return ex instanceof IllegalArgumentException
            || ex.getClass().getName().contains(OPENAI_EXCEPTION_NAME);
    }

    /**
     * Gets the root cause of an exception.
     *
     * @param throwable the exception to find the root cause for
     * @return the root cause exception
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null || cause == throwable) {
            return throwable;
        }
        return getRootCause(cause);
    }

    /**
     * Truncates a string to a maximum length with a suffix if needed.
     *
     * @param input the string to truncate
     * @return the truncated string
     */
    private String truncate(String input) {
        if (input == null) return NULL_STRING;
        return input.length() > MAX_PARAM_LENGTH
            ? input.substring(DEFAULT_INDEX, MAX_PARAM_LENGTH)
            + TRUNCATION_SUFFIX
            : input;
    }
}