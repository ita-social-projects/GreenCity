package greencity.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Aspect for logging REST request and response details. Logs the endpoint,
 * request body, response status, and response body for all controller methods.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RestLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(RestLoggingAspect.class);
    private static final String DEFAULT_BODY_VALUE = "N/A";
    private static final int DEFAULT_SUCCESS_STATUS = 200;
    private static final int ERROR_STATUS = 500;
    private static final String REQUEST_LOG_FORMAT = "Request - Endpoint: %s, Request Body: %s";
    private static final String RESPONSE_LOG_FORMAT =
        "Response - Endpoint: %s, Status: %d, Response: %s, Duration: %d ms";
    private static final String ERROR_LOG_FORMAT = "Response - Endpoint: %s, Status: %d, Error: %s, Duration: %d ms";
    private final ObjectMapper objectMapper;

    /**
     * Intercepts all method calls in controllers annotated with @RestController and
     * logs the request and response details.
     *
     * @param joinPoint the join point for the controller method
     * @return the result of the controller method execution
     * @throws Throwable if an error occurs during method execution
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.warn("No Servlet Request Attributes found, skipping logging for this request");
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String endpoint = buildEndpoint(request);
        String requestBody = extractRequestBody(joinPoint.getArgs());
        logger.info(String.format(REQUEST_LOG_FORMAT, endpoint, requestBody));

        long startTime = System.currentTimeMillis();
        Object result = executeRequest(joinPoint, endpoint);
        long duration = System.currentTimeMillis() - startTime;

        int status = response != null ? response.getStatus() : DEFAULT_SUCCESS_STATUS;
        String responseBody = serializeResponseBody(result);
        logger.info(String.format(RESPONSE_LOG_FORMAT, endpoint, status, responseBody, duration));

        return result;
    }

    /**
     * Builds the full endpoint string, including the HTTP method, URI, and query
     * parameters.
     *
     * @param request the HTTP request
     * @return the full endpoint string
     */
    private String buildEndpoint(HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            endpoint += "?" + queryString;
        }
        return endpoint;
    }

    /**
     * Extracts and serializes the request body from the method arguments.
     *
     * @param args the arguments of the controller method
     * @return the serialized request body, or "N/A" if serialization fails
     */
    private String extractRequestBody(Object[] args) {
        for (Object arg : args) {
            if (isSerializableArgument(arg)) {
                try {
                    return objectMapper.writeValueAsString(arg);
                } catch (Exception e) {
                    logger.warn("Failed to serialize request body: {}", e.getMessage());
                    return DEFAULT_BODY_VALUE;
                }
            }
        }
        return DEFAULT_BODY_VALUE;
    }

    /**
     * Checks if an argument can be serialized.
     *
     * @param arg the method argument
     * @return true if the argument can be serialized
     */
    private boolean isSerializableArgument(Object arg) {
        return arg != null
            && !(arg instanceof HttpServletRequest)
            && !(arg instanceof HttpServletResponse);
    }

    /**
     * Executes the request and handles potential errors.
     *
     * @param joinPoint the join point for the method execution
     * @param endpoint  the endpoint for logging in case of an error
     * @return the result of the method execution
     * @throws Throwable if an error occurs
     */
    private Object executeRequest(ProceedingJoinPoint joinPoint, String endpoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error(String.format(ERROR_LOG_FORMAT, endpoint, ERROR_STATUS, t.getMessage(), duration));
            throw t;
        }
    }

    /**
     * Serializes the response body to JSON.
     *
     * @param result the result of the method execution
     * @return the serialized response body, or "N/A" if serialization fails
     */
    private String serializeResponseBody(Object result) {
        if (result == null) {
            return DEFAULT_BODY_VALUE;
        }
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            logger.warn("Failed to serialize response body: {}", e.getMessage());
            return DEFAULT_BODY_VALUE;
        }
    }
}