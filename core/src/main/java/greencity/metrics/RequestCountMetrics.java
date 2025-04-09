package greencity.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequestCountMetrics extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestCountMetrics.class);
    private static final long TIME_WINDOW_SECONDS = 3600;
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final int MAX_PROCESSED_REQUESTS = 10_000;

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Boolean> processedRequests = new ConcurrentHashMap<>();

    private final RequestMetric totalRequests;
    private final RequestMetric successfulRequests;
    private final RequestMetric failedRequests;
    private final RequestMetric prometheusRequests;

    public RequestCountMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.totalRequests = new RequestMetric("http_requests_total_per_hour", "Total number of HTTP requests per hour");
        this.successfulRequests = new RequestMetric("http_requests_successful_per_hour", "Total number of successful HTTP requests per hour");
        this.failedRequests = new RequestMetric("http_requests_failed_per_hour", "Total number of failed HTTP requests (4xx and 5xx) per hour");
        this.prometheusRequests = new RequestMetric("http_requests_prometheus_per_hour", "Total number of HTTP requests from Prometheus per hour");

        registerGauges();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = ensureRequestId(request, response);
        String requestUri = request.getRequestURI();

        if (isStaticResource(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isRequestAlreadyProcessed(requestId)) {
            filterChain.doFilter(request, response);
            return;
        }

        processRequest(request, response, filterChain, requestId, requestUri);
    }

    private String ensureRequestId(HttpServletRequest request, HttpServletResponse response) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
            response.setHeader(REQUEST_ID_HEADER, requestId);
            request.setAttribute(REQUEST_ID_HEADER, requestId);
        }
        return requestId;
    }

    private boolean isStaticResource(String uri) {
        return uri.endsWith(".ico") || uri.startsWith("/static/") || uri.startsWith("/resources/");
    }

    private boolean isRequestAlreadyProcessed(String requestId) {
        return processedRequests.putIfAbsent(requestId, true) != null;
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                                String requestId, String requestUri) throws ServletException, IOException {
        Instant now = Instant.now();
        StatusCapturingResponseWrapper responseWrapper = new StatusCapturingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
        } catch (Exception e) {
            responseWrapper.setStatusIfNotSet(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw e;
        } finally {
            updateMetrics(request, responseWrapper, now, requestId, requestUri);
            cleanupProcessedRequests();
        }
    }

    private void updateMetrics(HttpServletRequest request, StatusCapturingResponseWrapper responseWrapper,
                               Instant timestamp, String requestId, String requestUri) {
        int status = responseWrapper.getStatus();

        totalRequests.increment(timestamp);

        if (isPrometheusRequest(request)) {
            prometheusRequests.increment(timestamp);
        }

        if (isSuccessfulStatus(status)) {
            successfulRequests.increment(timestamp);
        } else if (isFailedStatus(status)) {
            failedRequests.increment(timestamp);
        }
    }

    private boolean isPrometheusRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.contains("Prometheus");
    }

    private boolean isSuccessfulStatus(int status) {
        return status >= HttpServletResponse.SC_OK && status < HttpServletResponse.SC_MULTIPLE_CHOICES;
    }

    private boolean isFailedStatus(int status) {
        return (status >= HttpServletResponse.SC_BAD_REQUEST && status < HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                || (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR && status < HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED);
    }

    private void cleanupProcessedRequests() {
        if (processedRequests.size() > MAX_PROCESSED_REQUESTS) {
            processedRequests.clear();
            logger.debug("Cleared processed requests map");
        }
    }

    private void registerGauges() {
        totalRequests.registerGauge(meterRegistry);
        successfulRequests.registerGauge(meterRegistry);
        failedRequests.registerGauge(meterRegistry);
        prometheusRequests.registerGauge(meterRegistry);

        Gauge.builder("app_error_rate_per_hour", this::getErrorRate)
                .description("Percentage of failed HTTP requests per hour")
                .baseUnit("percent")
                .register(meterRegistry);
    }

    private double getErrorRate() {
        int total = totalRequests.getCount();
        int failed = failedRequests.getCount();
        return total == 0 ? 0.0 : (double) failed / total * 100;
    }

    private class RequestMetric {
        private final String name;
        private final String description;
        private final Queue<Instant> timestamps = new ConcurrentLinkedQueue<>();
        private final AtomicInteger count = new AtomicInteger(0);

        RequestMetric(String name, String description) {
            this.name = name;
            this.description = description;
        }

        void increment(Instant timestamp) {
            timestamps.add(timestamp);
            count.incrementAndGet();
            cleanupOldTimestamps(timestamp);
        }

        void registerGauge(MeterRegistry registry) {
            Gauge.builder(name, count::get)
                    .description(description)
                    .baseUnit("requests")
                    .register(registry);
        }

        private void cleanupOldTimestamps(Instant now) {
            Instant threshold = now.minusSeconds(TIME_WINDOW_SECONDS);
            while (!timestamps.isEmpty() && timestamps.peek().isBefore(threshold)) {
                timestamps.poll();
                count.decrementAndGet();
            }
        }

        int getCount() {
            return count.get();
        }

        Queue<Instant> getTimestamps() {
            return timestamps;
        }
    }

    private static class StatusCapturingResponseWrapper extends HttpServletResponseWrapper {
        private int status;

        StatusCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            status = sc;
        }

        @Override
        public void sendError(int sc) throws IOException {
            super.sendError(sc);
            status = sc;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            super.sendError(sc, msg);
            status = sc;
        }

        void setStatusIfNotSet(int sc) {
            if (status == 0) {
                status = sc;
            }
        }

        @Override
        public int getStatus() {
            return status != 0 ? status : super.getStatus();
        }
    }

}