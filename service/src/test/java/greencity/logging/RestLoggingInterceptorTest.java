package greencity.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestLoggingInterceptorTest {

    @InjectMocks
    private RestLoggingInterceptor restLoggingInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ContentCachingRequestWrapper requestWrapper;

    @Mock
    private ContentCachingResponseWrapper responseWrapper;

    @Mock
    private Object handler;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(RestLoggingInterceptor.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    void testPreHandle_SuccessfulGetRequestWithQueryString() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("param=value");

        assertTrue(restLoggingInterceptor.preHandle(request, response, handler));

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(request).setAttribute(eq("startTime"), startTimeCaptor.capture());
        assertInstanceOf(Long.class, startTimeCaptor.getValue());
        verify(request).setAttribute("endpoint", "GET /api/test?param=value");
        verify(request).setAttribute("requestBody", "param=value");

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Request - Endpoint: GET /api/test?param=value, Request Body: param=value", logEvent.getFormattedMessage());
    }

    @Test
    void testPreHandle_SuccessfulGetRequestWithoutQueryString() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn(null);

        assertTrue(restLoggingInterceptor.preHandle(request, response, handler));

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(request).setAttribute(eq("startTime"), startTimeCaptor.capture());
        assertInstanceOf(Long.class, startTimeCaptor.getValue());
        verify(request).setAttribute("endpoint", "GET /api/test");
        verify(request).setAttribute("requestBody", "N/A");

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Request - Endpoint: GET /api/test, Request Body: N/A", logEvent.getFormattedMessage());
    }

    @Test
    void testPreHandle_PostRequestWithBody() {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getRequestURI()).thenReturn("/api/test");
        when(requestWrapper.getQueryString()).thenReturn(null);
        when(requestWrapper.getContentAsByteArray()).thenReturn("requestBody".getBytes(StandardCharsets.UTF_8));
        when(requestWrapper.getCharacterEncoding()).thenReturn("UTF-8");

        assertTrue(restLoggingInterceptor.preHandle(requestWrapper, response, handler));

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(requestWrapper).setAttribute(eq("startTime"), startTimeCaptor.capture());
        assertInstanceOf(Long.class, startTimeCaptor.getValue());
        verify(requestWrapper).setAttribute("endpoint", "POST /api/test");
        verify(requestWrapper).setAttribute("requestBody", "requestBody");

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Request - Endpoint: POST /api/test, Request Body: requestBody", logEvent.getFormattedMessage());
    }

    @Test
    void testPreHandle_PostRequestWithEmptyBody() {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getRequestURI()).thenReturn("/api/test");
        when(requestWrapper.getQueryString()).thenReturn(null);
        when(requestWrapper.getContentAsByteArray()).thenReturn(new byte[0]);

        assertTrue(restLoggingInterceptor.preHandle(requestWrapper, response, handler));

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(requestWrapper).setAttribute(eq("startTime"), startTimeCaptor.capture());
        assertInstanceOf(Long.class, startTimeCaptor.getValue());
        verify(requestWrapper).setAttribute("endpoint", "POST /api/test");
        verify(requestWrapper).setAttribute("requestBody", "N/A");

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Request - Endpoint: POST /api/test, Request Body: N/A", logEvent.getFormattedMessage());
    }

    @Test
    void testPreHandle_PostRequestWithException() {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getRequestURI()).thenReturn("/api/test");
        when(requestWrapper.getQueryString()).thenReturn(null);
        when(requestWrapper.getContentAsByteArray()).thenReturn("requestBody".getBytes(StandardCharsets.UTF_8));
        when(requestWrapper.getCharacterEncoding()).thenThrow(new RuntimeException("Invalid encoding"));

        assertTrue(restLoggingInterceptor.preHandle(requestWrapper, response, handler));

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(requestWrapper).setAttribute(eq("startTime"), startTimeCaptor.capture());
        assertInstanceOf(Long.class, startTimeCaptor.getValue());
        verify(requestWrapper).setAttribute("endpoint", "POST /api/test");
        verify(requestWrapper).setAttribute("requestBody", "N/A");

        assertEquals(2, listAppender.list.size());
        ILoggingEvent warnEvent = listAppender.list.getFirst();
        assertEquals("Failed to read request body: Invalid encoding", warnEvent.getFormattedMessage());
        ILoggingEvent infoEvent = listAppender.list.get(1);
        assertEquals("Request - Endpoint: POST /api/test, Request Body: N/A", infoEvent.getFormattedMessage());
    }

    @Test
    void testAfterCompletion_SuccessfulResponse() {
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getAttribute("endpoint")).thenReturn("POST /api/test");
        when(responseWrapper.getStatus()).thenReturn(200);
        when(responseWrapper.getContentAsByteArray()).thenReturn("responseBody".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenReturn("UTF-8");

        restLoggingInterceptor.afterCompletion(request, responseWrapper, handler, null);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertTrue(logEvent.getFormattedMessage()
            .startsWith("Response - Endpoint: POST /api/test, Status: 200, Response: responseBody, Duration:"));
    }

    @Test
    void testAfterCompletion_ErrorResponseWithoutException() {
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getAttribute("endpoint")).thenReturn("POST /api/test");
        when(responseWrapper.getStatus()).thenReturn(400);
        when(responseWrapper.getContentAsByteArray()).thenReturn("errorResponse".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenReturn("UTF-8");

        restLoggingInterceptor.afterCompletion(request, responseWrapper, handler, null);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals(
            "Response - Endpoint: POST /api/test, Status: 400, Duration: " + (System.currentTimeMillis() - startTime)
                + " ms",
            logEvent.getFormattedMessage());
    }

    @Test
    void testAfterCompletion_ErrorResponseWithException() {
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getAttribute("endpoint")).thenReturn("POST /api/test");
        when(responseWrapper.getStatus()).thenReturn(200);
        when(responseWrapper.getContentAsByteArray()).thenReturn("responseBody".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenReturn("UTF-8");
        Exception ex = new RuntimeException("Test error");

        restLoggingInterceptor.afterCompletion(request, responseWrapper, handler, ex);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals(
            "Response - Endpoint: POST /api/test, Status: 200, Duration: " + (System.currentTimeMillis() - startTime)
                + " ms",
            logEvent.getFormattedMessage());
    }

    @Test
    void testAfterCompletion_ResponseWithEmptyBody() {
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getAttribute("endpoint")).thenReturn("POST /api/test");
        when(responseWrapper.getStatus()).thenReturn(200);
        when(responseWrapper.getContentAsByteArray()).thenReturn(new byte[0]);

        restLoggingInterceptor.afterCompletion(request, responseWrapper, handler, null);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertTrue(logEvent.getFormattedMessage()
            .startsWith("Response - Endpoint: POST /api/test, Status: 200, Response: N/A, Duration:"));
    }

    @Test
    void testAfterCompletion_ResponseWithException() {
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getAttribute("endpoint")).thenReturn("POST /api/test");
        when(responseWrapper.getStatus()).thenReturn(200);
        when(responseWrapper.getContentAsByteArray()).thenReturn("responseBody".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenThrow(new RuntimeException("Invalid encoding"));

        restLoggingInterceptor.afterCompletion(request, responseWrapper, handler, null);

        assertEquals(2, listAppender.list.size());
        ILoggingEvent warnEvent = listAppender.list.getFirst();
        assertEquals("Failed to read response body: Invalid encoding", warnEvent.getFormattedMessage());
        ILoggingEvent infoEvent = listAppender.list.get(1);
        assertTrue(infoEvent.getFormattedMessage()
            .startsWith("Response - Endpoint: POST /api/test, Status: 200, Response: N/A, Duration:"));
    }

    @Test
    void testBuildEndpoint_WithQueryString() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("param=value");

        String result = invokePrivateMethod("buildEndpoint", new Class<?>[]{HttpServletRequest.class}, request);

        assertEquals("GET /api/test?param=value", result);
    }

    @Test
    void testBuildEndpoint_WithoutQueryString() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn(null);

        String result = invokePrivateMethod("buildEndpoint", new Class<?>[]{HttpServletRequest.class}, request);

        assertEquals("POST /api/test", result);
    }

    @Test
    void testExtractRequestBody_GetWithQueryString() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn("param=value");

        String result = invokePrivateMethod("extractRequestBody", new Class<?>[]{HttpServletRequest.class}, request);

        assertEquals("param=value", result);
    }

    @Test
    void testExtractRequestBody_GetWithoutQueryString() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn(null);

        String result = invokePrivateMethod("extractRequestBody", new Class<?>[]{HttpServletRequest.class}, request);

        assertEquals("N/A", result);
    }

    @Test
    void testExtractRequestBody_PostWithBody() throws Exception {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getContentAsByteArray()).thenReturn("requestBody".getBytes(StandardCharsets.UTF_8));
        when(requestWrapper.getCharacterEncoding()).thenReturn("UTF-8");

        String result = invokePrivateMethod("extractRequestBody", new Class<?>[]{HttpServletRequest.class}, requestWrapper);

        assertEquals("requestBody", result);
    }

    @Test
    void testExtractRequestBody_PostWithEmptyBody() throws Exception {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getContentAsByteArray()).thenReturn(new byte[0]);

        String result = invokePrivateMethod("extractRequestBody", new Class<?>[]{HttpServletRequest.class}, requestWrapper);

        assertEquals("N/A", result);
    }

    @Test
    void testExtractRequestBody_PostWithException() throws Exception {
        when(requestWrapper.getMethod()).thenReturn("POST");
        when(requestWrapper.getContentAsByteArray()).thenReturn("requestBody".getBytes(StandardCharsets.UTF_8));
        when(requestWrapper.getCharacterEncoding()).thenThrow(new RuntimeException("Invalid encoding"));

        String result = invokePrivateMethod("extractRequestBody", new Class<?>[]{HttpServletRequest.class}, requestWrapper);

        assertEquals("N/A", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent warnEvent = listAppender.list.getFirst();
        assertEquals("Failed to read request body: Invalid encoding", warnEvent.getFormattedMessage());
    }

    @Test
    void testExtractResponseBody_WithBody() throws Exception {
        when(responseWrapper.getContentAsByteArray()).thenReturn("responseBody".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenReturn("UTF-8");

        String result = invokePrivateMethod("extractResponseBody", new Class<?>[]{HttpServletResponse.class}, responseWrapper);

        assertEquals("responseBody", result);
    }

    @Test
    void testExtractResponseBody_WithEmptyBody() throws Exception {
        when(responseWrapper.getContentAsByteArray()).thenReturn(new byte[0]);

        String result = invokePrivateMethod("extractResponseBody", new Class<?>[]{HttpServletResponse.class}, responseWrapper);

        assertEquals("N/A", result);
    }

    @Test
    void testExtractResponseBody_WithException() throws Exception {
        when(responseWrapper.getContentAsByteArray()).thenReturn("responseBody".getBytes(StandardCharsets.UTF_8));
        when(responseWrapper.getCharacterEncoding()).thenThrow(new RuntimeException("Invalid encoding"));

        String result = invokePrivateMethod("extractResponseBody", new Class<?>[]{HttpServletResponse.class}, responseWrapper);

        assertEquals("N/A", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent warnEvent = listAppender.list.getFirst();
        assertEquals("Failed to read response body: Invalid encoding", warnEvent.getFormattedMessage());
    }

    @Test
    void testLogResponse_SuccessfulResponse() throws Exception {
        String endpoint = "POST /api/test";
        int status = 200;
        long duration = 100L;
        String responseBody = "responseBody";

        invokeLogResponse(endpoint, status, duration, responseBody, null);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Response - Endpoint: POST /api/test, Status: 200, Response: responseBody, Duration: 100 ms",
            logEvent.getFormattedMessage());
    }

    @Test
    void testLogResponse_ErrorResponseWithoutException() throws Exception {
        String endpoint = "POST /api/test";
        int status = 400;
        long duration = 100L;
        String responseBody = "errorResponse";

        invokeLogResponse(endpoint, status, duration, responseBody, null);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Response - Endpoint: POST /api/test, Status: 400, Duration: 100 ms",
            logEvent.getFormattedMessage());
    }

    @Test
    void testLogResponse_ErrorResponseWithException() throws Exception {
        String endpoint = "POST /api/test";
        int status = 200;
        long duration = 100L;
        String responseBody = "responseBody";
        Exception ex = new RuntimeException("Test error");

        invokeLogResponse(endpoint, status, duration, responseBody, ex);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent logEvent = listAppender.list.getFirst();
        assertEquals("Response - Endpoint: POST /api/test, Status: 200, Duration: 100 ms",
            logEvent.getFormattedMessage());
    }

    private String invokePrivateMethod(String methodName, Class<?>[] parameterTypes, Object arg) throws Exception {
        Method method = RestLoggingInterceptor.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return (String) method.invoke(restLoggingInterceptor, arg);
    }

    private void invokeLogResponse(String endpoint, int status, long duration, String responseBody, Exception ex)
        throws Exception {
        Method method = RestLoggingInterceptor.class.getDeclaredMethod("logResponse",
            String.class, int.class, long.class, String.class, Exception.class);
        method.setAccessible(true);
        method.invoke(restLoggingInterceptor, endpoint, status, duration, responseBody, ex);
    }
}