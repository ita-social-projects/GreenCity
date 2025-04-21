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
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestLoggingInterceptor.class);
    private static final String DEFAULT_BODY_VALUE = "N/A";
    private static final String REQUEST_LOG_FORMAT = "Request - Endpoint: {}, Request Body: {}";
    private static final String RESPONSE_LOG_FORMAT =
        "Response - Endpoint: {}, Status: {}, Response: {}, Duration: {} ms";
    private static final String ERROR_LOG_FORMAT = "Response - Endpoint: {}, Status: {}, Error: {}, Duration: {} ms";
    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String ENDPOINT_ATTRIBUTE = "endpoint";
    private static final String REQUEST_BODY_ATTRIBUTE = "requestBody";
    private static final int MAX_LOG_STRING_LENGTH = 1000;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        String endpoint = buildEndpoint(request);
        String requestBody = extractRequestBody(request);

        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(ENDPOINT_ATTRIBUTE, endpoint);
        request.setAttribute(REQUEST_BODY_ATTRIBUTE, requestBody);

        if (logger.isInfoEnabled()) {
            logger.info(REQUEST_LOG_FORMAT, sanitize(endpoint), sanitize(requestBody));
        }
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
            return extractContentFromWrapper(wrapper, "request body");
        }
        return DEFAULT_BODY_VALUE;
    }

    private String extractResponseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            String responseBody = extractContentFromWrapper(wrapper, "response body");
            try {
                wrapper.copyBodyToResponse();
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to copy response body: {}", sanitize(e.getMessage()));
                }
            }
            return responseBody;
        }
        return DEFAULT_BODY_VALUE;
    }

    private String extractContentFromWrapper(Object wrapper, String logMessagePrefix) {
        if (!(wrapper instanceof ContentCachingRequestWrapper || wrapper instanceof ContentCachingResponseWrapper)) {
            return DEFAULT_BODY_VALUE;
        }

        byte[] content = getContentBytes(wrapper);
        if (content.length == 0) {
            return DEFAULT_BODY_VALUE;
        }

        return processContentWithEncoding(content, wrapper, logMessagePrefix);
    }

    private byte[] getContentBytes(Object wrapper) {
        if (wrapper instanceof ContentCachingRequestWrapper requestWrapper) {
            return requestWrapper.getContentAsByteArray();
        } else if (wrapper instanceof ContentCachingResponseWrapper responseWrapper) {
            return responseWrapper.getContentAsByteArray();
        }
        return new byte[0];
    }

    private String processContentWithEncoding(byte[] content, Object wrapper, String logMessagePrefix) {
        try {
            String encoding = getEncoding(wrapper);
            return new String(content, encoding != null ? encoding : "UTF-8");
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to read {}: {}", logMessagePrefix, sanitize(e.getMessage()));
            }
            return DEFAULT_BODY_VALUE;
        }
    }

    private String getEncoding(Object wrapper) {
        if (wrapper instanceof ContentCachingRequestWrapper requestWrapper) {
            return requestWrapper.getCharacterEncoding();
        } else if (wrapper instanceof ContentCachingResponseWrapper responseWrapper) {
            return responseWrapper.getCharacterEncoding();
        }
        return null;
    }

    private void logResponse(String endpoint, int status, long duration, String responseBody, Exception ex) {
        if (status >= 400 || ex != null) {
            String errorMessage = Objects.requireNonNullElse(ex != null ? ex.getMessage() : responseBody, "");
            if (logger.isInfoEnabled()) {
                logger.info(ERROR_LOG_FORMAT, sanitize(endpoint), status, sanitize(errorMessage), duration);
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(RESPONSE_LOG_FORMAT, sanitize(endpoint), status, sanitize(responseBody), duration);
            }
        }
    }

    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String truncated = input;
        if (input.length() > MAX_LOG_STRING_LENGTH) {
            truncated = input.substring(0, MAX_LOG_STRING_LENGTH - 3) + "...";
        }
        return truncated.replaceAll("[<>\"&'\\n\\r]", "_");
    }
}