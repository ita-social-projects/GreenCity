package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class RequestCountMetricsTest {

    private RequestCountMetrics requestCountMetrics;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Gauge.Builder gaugeBuilder;

    private MockedStatic<Gauge> gaugeStaticMock;

    @BeforeEach
    void setUp() {
        gaugeStaticMock = mockStatic(Gauge.class);
        gaugeStaticMock.when(() -> Gauge.builder(anyString(), any()))
                .thenReturn(gaugeBuilder);
        when(gaugeBuilder.description(anyString())).thenReturn(gaugeBuilder);
        when(gaugeBuilder.baseUnit(anyString())).thenReturn(gaugeBuilder);
        when(gaugeBuilder.register(any(MeterRegistry.class))).thenReturn(mock(Gauge.class));

        requestCountMetrics = new RequestCountMetrics(meterRegistry);
    }

    @AfterEach
    void tearDown() {
        if (gaugeStaticMock != null) {
            gaugeStaticMock.close();
        }
    }

    private String getRequestIdHeader() throws NoSuchFieldException, IllegalAccessException {
        Field requestIdHeaderField = RequestCountMetrics.class.getDeclaredField("REQUEST_ID_HEADER");
        requestIdHeaderField.setAccessible(true);
        return (String) requestIdHeaderField.get(null);
    }

    private ConcurrentHashMap<String, Boolean> getProcessedRequests() throws NoSuchFieldException, IllegalAccessException {
        Field processedRequestsField = RequestCountMetrics.class.getDeclaredField("processedRequests");
        processedRequestsField.setAccessible(true);
        return (ConcurrentHashMap<String, Boolean>) processedRequestsField.get(requestCountMetrics);
    }

    private Object getRequestMetric(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field metricField = RequestCountMetrics.class.getDeclaredField(fieldName);
        metricField.setAccessible(true);
        return metricField.get(requestCountMetrics);
    }

    private AtomicInteger getMetricCount(Object requestMetric) throws NoSuchFieldException, IllegalAccessException {
        Field countField = requestMetric.getClass().getDeclaredField("count");
        countField.setAccessible(true);
        return (AtomicInteger) countField.get(requestMetric);
    }

    private int getMetricCountValue(String metricFieldName) throws NoSuchFieldException, IllegalAccessException {
        Object metric = getRequestMetric(metricFieldName);
        return getMetricCount(metric).get();
    }

    private double getErrorRate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getErrorRateMethod = RequestCountMetrics.class.getDeclaredMethod("getErrorRate");
        getErrorRateMethod.setAccessible(true);
        return (double) getErrorRateMethod.invoke(requestCountMetrics);
    }

    @Test
    void testMetricsInitialized() {
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        gaugeStaticMock.verify(() -> Gauge.builder(nameCaptor.capture(), any()), times(5));

        assertEquals("http_requests_total_per_hour", nameCaptor.getAllValues().get(0));
        assertEquals("http_requests_successful_per_hour", nameCaptor.getAllValues().get(1));
        assertEquals("http_requests_failed_per_hour", nameCaptor.getAllValues().get(2));
        assertEquals("http_requests_prometheus_per_hour", nameCaptor.getAllValues().get(3));
        assertEquals("app_error_rate_per_hour", nameCaptor.getAllValues().get(4));
    }

    @Test
    void testStaticResourceSkipped() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/static/style.css");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(0, getMetricCountValue("totalRequests"));
        assertEquals(0, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");
    }

    @Test
    void testRequestAlreadyProcessed() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String requestId = "test-id";
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(requestId);
        getProcessedRequests().put(requestId, true);

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(0, getMetricCountValue("totalRequests"));
        assertEquals(0, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");
    }

    @Test
    void testSuccessfulRequest() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String requestId = "test-id";
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.setStatus(HttpServletResponse.SC_OK);
            return null;
        }).when(filterChain).doFilter(any(), any());

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), any());
        assertEquals(1, getMetricCountValue("totalRequests"));
        assertEquals(1, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(getRequestIdHeader()), headerCaptor.capture());
        assertNotNull(headerCaptor.getValue());
    }

    @Test
    void testFailedRequest4xx() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(filterChain).doFilter(any(), any());

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), any());
        assertEquals(1, getMetricCountValue("totalRequests"));
        assertEquals(0, getMetricCountValue("successfulRequests"));
        assertEquals(1, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(100.0, getErrorRate(), "Error rate should be 100%");
    }

    @Test
    void testFailedRequest5xx() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());

        assertThrows(RuntimeException.class, () -> requestCountMetrics.doFilterInternal(request, response, filterChain));

        verify(filterChain).doFilter(eq(request), any());
        assertEquals(1, getMetricCountValue("totalRequests"));
        assertEquals(0, getMetricCountValue("successfulRequests"));
        assertEquals(1, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(100.0, getErrorRate(), "Error rate should be 100%");
    }

    @Test
    void testPrometheusRequest() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Prometheus/2.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.setStatus(HttpServletResponse.SC_OK);
            return null;
        }).when(filterChain).doFilter(any(), any());

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), any());
        assertEquals(1, getMetricCountValue("totalRequests"));
        assertEquals(1, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(1, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");
    }

    @Test
    void testCleanupProcessedRequests() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.setStatus(HttpServletResponse.SC_OK);
            return null;
        }).when(filterChain).doFilter(any(), any());

        Field maxProcessedRequestsField = RequestCountMetrics.class.getDeclaredField("MAX_PROCESSED_REQUESTS");
        maxProcessedRequestsField.setAccessible(true);
        int maxProcessedRequests = (int) maxProcessedRequestsField.get(null);

        for (int i = 0; i < maxProcessedRequests + 1; i++) {
            getProcessedRequests().put("id-" + i, true);
        }

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        assertTrue(getProcessedRequests().size() < maxProcessedRequests);
        assertEquals(1, getMetricCountValue("totalRequests"));
        assertEquals(1, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");
    }

    @Test
    void testMetricCleanupOldTimestamps() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.setStatus(HttpServletResponse.SC_OK);
            return null;
        }).when(filterChain).doFilter(any(), any());

        Field timeWindowSecondsField = RequestCountMetrics.class.getDeclaredField("TIME_WINDOW_SECONDS");
        timeWindowSecondsField.setAccessible(true);
        long timeWindowSeconds = (long) timeWindowSecondsField.get(null);

        Instant oldTimestamp = Instant.now().minusSeconds(timeWindowSeconds + 1);
        Object totalRequestsMetric = getRequestMetric("totalRequests");
        Method incrementMethod = totalRequestsMetric.getClass().getDeclaredMethod("increment", Instant.class);
        incrementMethod.setAccessible(true);
        incrementMethod.invoke(totalRequestsMetric, oldTimestamp);

        requestCountMetrics.doFilterInternal(request, response, filterChain);

        assertEquals(1, getMetricCountValue("totalRequests")); // Старий запис очищено, новий додано
        assertEquals(1, getMetricCountValue("successfulRequests"));
        assertEquals(0, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals(0.0, getErrorRate(), "Error rate should be 0%");
    }

    @Test
    void testMixedRequests() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(getRequestIdHeader())).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.setStatus(HttpServletResponse.SC_OK);
            return null;
        }).when(filterChain).doFilter(any(), any());
        requestCountMetrics.doFilterInternal(request, response, filterChain);

        doAnswer(invocation -> {
            HttpServletResponse wrapper = invocation.getArgument(1);
            wrapper.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }).when(filterChain).doFilter(any(), any());
        requestCountMetrics.doFilterInternal(request, response, filterChain);

        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(any(), any());
        assertThrows(RuntimeException.class, () -> requestCountMetrics.doFilterInternal(request, response, filterChain));

        verify(filterChain, times(3)).doFilter(eq(request), any());
        assertEquals(3, getMetricCountValue("totalRequests"));
        assertEquals(1, getMetricCountValue("successfulRequests"));
        assertEquals(2, getMetricCountValue("failedRequests"));
        assertEquals(0, getMetricCountValue("prometheusRequests"));
        assertEquals((2.0 / 3.0) * 100, getErrorRate(), 0.01, "Error rate should be ~66.67%");
    }
}