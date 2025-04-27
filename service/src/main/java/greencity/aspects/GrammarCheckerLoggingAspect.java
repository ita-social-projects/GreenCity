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

@Slf4j
@Component
@Aspect
public class GrammarCheckerLoggingAspect {
    @Around("execution(* greencity.service.GrammarChecker.checkGrammar(..)) && args(text)")
    public Object logGrammarCheck(ProceedingJoinPoint joinPoint, String text) throws Throwable {
        log.info(CHECK_GRAMMAR_LOG_MESSAGE, text, LocalDateTime.now());
        log.debug(CHECK_GRAMMAR_DEBUG_LOG, text);
        log.trace(START_PROCESSING_LOG);

        Object result;
        long startTime = System.currentTimeMillis();
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

    @Before("execution(* greencity.service.GrammarChecker.clearCache(..)) && args(text)")
    public void logCacheClear(String text) {
        log.info(CLEAR_CACHE_LOG_MESSAGE, text, LocalDateTime.now());
        log.trace(CLEARING_CACHE_LOG, text);
    }
}
