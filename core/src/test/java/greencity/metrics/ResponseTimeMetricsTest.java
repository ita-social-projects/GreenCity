package greencity.metrics;

import greencity.constant.ErrorMessage;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ResponseTimeMetricsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private MeterRegistry meterRegistry;
    private ResponseTimeMetrics filter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        filter = new ResponseTimeMetrics(meterRegistry);
    }

    @Test
    void doFilterInternal_recordsResponseTime() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        Timer timer = meterRegistry.get("app_response_time").timer();
        assertEquals(1, timer.count(), "Exactly one request should be recorded");
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= 0, "Response time should be greater than or equal to 0");

        assertEquals(1, filter.getRequestCount(), "Request count should be 1");
        assertTrue(filter.getTotalResponseTime() >= 0, "Total response time should be greater than or equal to 0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withMultipleDelays_recordsMinMaxAvgResponseTime() throws ServletException, IOException {
        doAnswer(invocation -> {
            Thread.sleep(100);
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilterInternal(request, response, filterChain);

        doAnswer(invocation -> {
            Thread.sleep(200);
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilterInternal(request, response, filterChain);

        doAnswer(invocation -> {
            Thread.sleep(150);
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilterInternal(request, response, filterChain);

        assertEquals(3, filter.getRequestCount(), "Exactly three requests should be recorded");

        double minResponseTime = meterRegistry.get("app_response_time_min").gauge().value();
        double maxResponseTime = meterRegistry.get("app_response_time_max").gauge().value();
        double avgResponseTime = meterRegistry.get("app_response_time_avg").gauge().value();

        assertTrue(minResponseTime >= 100, "Minimum response time should be at least 100ms");
        assertTrue(minResponseTime <= 150, "Minimum response time should be less than or equal to 150ms");
        assertTrue(maxResponseTime >= 200, "Maximum response time should be at least 200ms");
        assertTrue(maxResponseTime <= 250, "Maximum response time should be less than or equal to 250ms");
        assertTrue(avgResponseTime >= 150, "Average response time should be around 150ms");
        assertTrue(avgResponseTime <= 200, "Average response time should be less than or equal to 200ms");

        Timer timer = meterRegistry.get("app_response_time").timer();
        assertEquals(3, timer.count(), "Exactly three requests should be recorded in Timer");
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= 450,
            "Total response time in Timer should be at least 450ms");

        verify(filterChain, times(3)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noRequests_returnsZeroForMinMaxAvg() {
        double minResponseTime = meterRegistry.get("app_response_time_min").gauge().value();
        double maxResponseTime = meterRegistry.get("app_response_time_max").gauge().value();
        double avgResponseTime = meterRegistry.get("app_response_time_avg").gauge().value();

        assertEquals(0.0, minResponseTime, "Minimum response time should be 0 when no requests");
        assertEquals(0.0, maxResponseTime, "Maximum response time should be 0 when no requests");
        assertEquals(0.0, avgResponseTime, "Average response time should be 0 when no requests");
        assertEquals(0, filter.getRequestCount(), "Request count should be 0");
        assertEquals(0.0, filter.getTotalResponseTime(), "Total response time should be 0");
    }

    @Test
    void doFilterInternal_nullParameters_throwsServletException() {
        ServletException exception = assertThrows(ServletException.class, () -> {
            filter.doFilterInternal(null, response, filterChain);
        });
        assertEquals(ErrorMessage.NULL_REQUEST_RESPONSE, exception.getMessage());

        exception = assertThrows(ServletException.class, () -> {
            filter.doFilterInternal(request, null, filterChain);
        });
        assertEquals(ErrorMessage.NULL_REQUEST_RESPONSE, exception.getMessage());

        exception = assertThrows(ServletException.class, () -> {
            filter.doFilterInternal(request, response, null);
        });
        assertEquals(ErrorMessage.NULL_REQUEST_RESPONSE, exception.getMessage());
    }

    @Test
    void doFilterInternal_exceptionInFilterChain_stillRecordsResponseTime() throws ServletException, IOException {
        doAnswer(invocation -> {
            Thread.sleep(10);
            throw new ServletException("Test exception");
        }).when(filterChain).doFilter(request, response);

        assertThrows(ServletException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });

        Timer timer = filter.getResponseTimer();
        assertEquals(1, timer.count(), "Exactly one request should be recorded even if an exception occurs");
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= 0, "Response time should be greater than or equal to 0");

        assertEquals(1, filter.getRequestCount(), "Request count should be 1");
        assertTrue(filter.getTotalResponseTime() >= 0, "Total response time should be greater than or equal to 0");
        assertTrue(meterRegistry.get("app_response_time_min").gauge().value() >= 0,
            "Minimum response time should be greater than or equal to 0");
        assertTrue(meterRegistry.get("app_response_time_max").gauge().value() >= 0,
            "Maximum response time should be greater than or equal to 0");
        assertTrue(meterRegistry.get("app_response_time_avg").gauge().value() >= 0,
            "Average response time should be greater than or equal to 0");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_singleRequest_updatesMinMaxAvgCorrectly() throws ServletException, IOException {
        doAnswer(invocation -> {
            Thread.sleep(50);
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilterInternal(request, response, filterChain);

        double minResponseTime = meterRegistry.get("app_response_time_min").gauge().value();
        double maxResponseTime = meterRegistry.get("app_response_time_max").gauge().value();
        double avgResponseTime = meterRegistry.get("app_response_time_avg").gauge().value();

        assertTrue(minResponseTime >= 50, "Minimum response time should be at least 50ms");
        assertTrue(minResponseTime <= 100, "Minimum response time should be less than or equal to 100ms");
        assertTrue(maxResponseTime >= 50, "Maximum response time should be at least 50ms");
        assertTrue(maxResponseTime <= 100, "Maximum response time should be less than or equal to 100ms");
        assertTrue(avgResponseTime >= 50, "Average response time should be at least 50ms");
        assertTrue(avgResponseTime <= 100, "Average response time should be less than or equal to 100ms");

        assertEquals(1, filter.getRequestCount(), "Request count should be 1");
        assertTrue(filter.getTotalResponseTime() >= 50, "Total response time should be at least 50ms");

        verify(filterChain).doFilter(request, response);
    }
}