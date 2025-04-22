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

@Aspect
@Component
@Slf4j
public class JwtTokenActivityLoggingAspect {
    @Value("${tokenKey}")
    private String accessTokenKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* greencity.security.utils.TokenUtilService.*(..))")
    public void tokenUtilMethods() {}

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
                .filter(arg -> arg instanceof HttpServletRequest)
                .map(arg -> (HttpServletRequest) arg)
                .findFirst()
                .ifPresent(this::logTokenDetails);
            return result;
        } catch (Throwable throwable) {
            log.error(METHOD_EXCEPTION, methodName, throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    private void logTokenDetails(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn(NO_BEARER_TOKEN);
            return;
        }

        String token = authHeader.substring(7);

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
            long timeLeftSec = timeLeftMillis / 1000;

            if (timeLeftMillis < Duration.ofMinutes(5).toMillis()) {
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

    private String toJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return JSON_CONVERSION_FAILED;
        }
    }
}
