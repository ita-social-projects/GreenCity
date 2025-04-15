package greencity.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@RequiredArgsConstructor
public class RestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestLoggingInterceptor.class);
    private static final String DEFAULT_BODY_VALUE = "N/A";
    private static final String REQUEST_LOG_FORMAT = "Request - Endpoint: {}, Request Body: {}";
    private static final String RESPONSE_LOG_FORMAT =
        "Response - Endpoint: {}, Status: {}, Response: {}, Duration: {} ms";
    private static final String ERROR_LOG_FORMAT =
        "Response - Endpoint: {}, Status: {}, Error: Failed to execute request in {} ms: {}, Duration: {} ms";
    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String ENDPOINT_ATTRIBUTE = "endpoint";
    private static final String REQUEST_BODY_ATTRIBUTE = "requestBody";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        String endpoint = buildEndpoint(request);
        String requestBody = extractRequestBody(request);

        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(ENDPOINT_ATTRIBUTE, endpoint);
        request.setAttribute(REQUEST_BODY_ATTRIBUTE, requestBody);

        logger.info(REQUEST_LOG_FORMAT, endpoint, requestBody);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler, Exception ex) {
        long startTime = (long) request.getAttribute(START_TIME_ATTRIBUTE);
        String endpoint = (String) request.getAttribute(ENDPOINT_ATTRIBUTE);
        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();
        String responseBody = extractResponseBody(response);

        logResponse(endpoint, status, duration, responseBody, ex);
    }

    private String buildEndpoint(HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String queryString = request.getQueryString();
        return queryString != null ? endpoint + "?" + queryString : endpoint;
    }

    private String extractRequestBody(HttpServletRequest request) {
        if ("GET".equals(request.getMethod())) {
            String queryString = request.getQueryString();
            return queryString != null ? queryString : DEFAULT_BODY_VALUE;
        }

        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                try {
                    return new String(content, wrapper.getCharacterEncoding());
                } catch (Exception e) {
                    logger.warn("Failed to read request body: {}", e.getMessage());
                }
            }
        }
        return DEFAULT_BODY_VALUE;
    }

    private String extractResponseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            byte[] responseContent = wrapper.getContentAsByteArray();
            if (responseContent.length > 0) {
                try {
                    return new String(responseContent, wrapper.getCharacterEncoding());
                } catch (Exception e) {
                    logger.warn("Failed to read response body: {}", e.getMessage());
                }
            }
        }
        return DEFAULT_BODY_VALUE;
    }

    private void logResponse(String endpoint, int status, long duration, String responseBody, Exception ex) {
        if (status >= 400 || ex != null) {
            String errorMessage = ex != null ? ex.getClass().getSimpleName() + ": " + ex.getMessage() : "Unknown error";
            logger.info(ERROR_LOG_FORMAT, endpoint, status, duration, errorMessage, duration);
        } else {
            logger.info(RESPONSE_LOG_FORMAT, endpoint, status, responseBody, duration);
        }
    }
}