package greencity.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.constant.JwtTokenLogMessages.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging activity related to JWT token processing within the TokenUtilService class.
 * Responsibilities:
 * - Logs method invocations and their arguments
 * - Logs method results or thrown exceptions
 * - If an HTTP request is passed, extracts and logs information from the JWT token
 **/
@Aspect
@Component
@Slf4j
public class JwtTokenActivityLoggingAspect {
    @Value("${tokenKey}")
    private String accessTokenKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Length of the "Bearer " prefix in the Authorization header, used to extract the token
     */
    private static final int BEARER_PREFIX_LENGTH = 7;
    /**
     *  Number of milliseconds in one second, used for time conversion
     */
    private static final int MILLIS_IN_SECOND = 1000;
    /**
     * Threshold (in milliseconds) for logging a warning when the token is close to expiring.
     * Set to 5 minutes.
     */
    private static final long TOKEN_EXPIRY_WARNING_THRESHOLD_MS = Duration.ofMinutes(5).toMillis();

    @Pointcut("execution(* greencity.security.utils.TokenUtilService.*(..))")
    public void tokenUtilMethods() {
    }

    /**
     * Logs method execution details around the invocation of TokenUtilService methods.
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the result of the intercepted method
     * @throws Throwable any exception thrown by the intercepted method
     */
    @Around("tokenUtilMethods()")
    public Object logAroundTokenUtilMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info(METHOD_CALLED, methodName, toJsonSafe(args));

        Object result;
        try {
            result = joinPoint.proceed();
            log.info(METHOD_SUCCESS, methodName, toJsonSafe(result));
            Arrays.stream(args)
                .filter(HttpServletRequest.class::isInstance)
                .map(HttpServletRequest.class::cast)
                .findFirst()
                .ifPresent(this::logTokenDetails);
            return result;
        } catch (Throwable throwable) {
            log.error(METHOD_EXCEPTION, methodName, throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    /**
     * Extracts and logs details from the JWT token in the Authorization header,
     * including subject (email), issued date, expiration date, roles, and remaining time.
     * Logs a warning if the token is near expiration.
     *
     * @param request the HTTP request containing the Authorization header
     */
    private void logTokenDetails(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn(NO_BEARER_TOKEN);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX_LENGTH);

        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(accessTokenKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String email = claims.getSubject();
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();

            long timeLeftMillis = expiration.getTime() - System.currentTimeMillis();
            long timeLeftSec = timeLeftMillis / MILLIS_IN_SECOND;

            if (timeLeftMillis < TOKEN_EXPIRY_WARNING_THRESHOLD_MS) {
                log.warn(TOKEN_EXPIRING, email, timeLeftSec);
            }

            Object rawRoles = claims.get(CLAIM_ROLES);
            if (rawRoles instanceof List<?> roles) {
                log.info(TOKEN_ROLES, email, roles);
                if (roles.contains(ADMIN_ROLE)) {
                    log.info(ADMIN_ACCESS, email);
                }
            }
            log.info(TOKEN_INSPECTION_SUMMARY, email, issuedAt, expiration, timeLeftSec);
        } catch (Exception e) {
            log.error(TOKEN_PARSE_FAILED, e.getMessage(), e);
        }
    }

    /**
     * Safely serializes an object to a JSON string. If serialization fails,
     * returns a fallback string.
     *
     * @param obj the object to serialize
     * @return the JSON string or an error indicator
     */
    private String toJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return JSON_CONVERSION_FAILED;
        }
    }
}
