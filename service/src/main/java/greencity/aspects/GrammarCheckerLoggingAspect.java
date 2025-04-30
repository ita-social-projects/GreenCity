package greencity.aspects;

import static greencity.constant.GrammarCheckConstants.*;
import greencity.dto.grammar.GrammarCheckResult;
import greencity.dto.grammar.GrammarError;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging activities related to grammar checking and cache clearing
 * in the {@code GrammarChecker} service.
 * Responsibilities:
 * - Logs the input text and timestamps for grammar checks.
 * - Records results of grammar analysis including number and details of errors.
 * - Measures execution time of the grammar checking process.
 * - Logs when the grammar check cache is cleared.
 */
@Slf4j
@Component
@Aspect
public class GrammarCheckerLoggingAspect {
    /**
     * Around advice that logs the execution of the {@code checkGrammar} method in {@code GrammarChecker}.
     * Logs include:
     * - The text to be checked.
     * - Time of the check.
     * - Whether errors were found and details about them.
     * - Execution duration in milliseconds.
     *
     * @param joinPoint the intercepted method call
     * @param text      the input text passed to the grammar checker
     * @return the result of the grammar check
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around("execution(* greencity.service.GrammarChecker.checkGrammar(..)) && args(text)")
    public Object logGrammarCheck(ProceedingJoinPoint joinPoint, String text) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info(CHECK_GRAMMAR_LOG_MESSAGE, text, LocalDateTime.now());
        log.debug(CHECK_GRAMMAR_DEBUG_LOG, text);
        log.trace(START_PROCESSING_LOG);

        Object result;
        try {
            result = joinPoint.proceed();
            if (result instanceof GrammarCheckResult grammarCheckResult) {
                log.trace(PROCESSING_RESULT_LOG);
                if (grammarCheckResult.getErrors().isEmpty()) {
                    log.info(NO_ERRORS_LOG);
                } else {
                    log.warn(FOUND_ERRORS_LOG, grammarCheckResult.getErrors().size());
                    for (GrammarError error : grammarCheckResult.getErrors()) {
                        log.warn(ERROR_DETAILS_LOG,
                            error.getOriginal(), error.getCorrection(),
                            error.getDescription(), error.getPosition());
                    }
                }
                log.info(GRAMMAR_CHECK_COMPLETED_LOG, grammarCheckResult.getCorrectedText().length());
                log.trace(FINAL_CORRECTED_TEXT_LOG, grammarCheckResult.getCorrectedText());
            }

        } catch (Exception e) {
            log.error(GRAMMAR_CHECK_ERROR_LOG, text, e.getMessage(), e);
            throw e;
        }

        long duration = System.currentTimeMillis() - startTime;
        log.debug(METHOD_EXECUTION_DURATION_LOG, duration);

        return result;
    }

    /**
     * Before advice that logs when the {@code clearCache} method in {@code GrammarChecker} is invoked.
     *
     * @param text the key or identifier for which the cache is being cleared
     */
    @Before("execution(* greencity.service.GrammarChecker.clearCache(..)) && args(text)")
    public void logCacheClear(String text) {
        log.info(CLEAR_CACHE_LOG_MESSAGE, text, LocalDateTime.now());
        log.trace(CLEARING_CACHE_LOG, text);
    }
}
