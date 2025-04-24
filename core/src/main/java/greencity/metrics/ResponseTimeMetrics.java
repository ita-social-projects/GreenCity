package greencity.metrics;

import greencity.constant.ErrorMessage;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

@Component
public class ResponseTimeMetrics extends OncePerRequestFilter {
    @Getter
    private final Timer responseTimer;
    private final MeterRegistry meterRegistry;
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxResponseTime = new AtomicLong(0);
    private final DoubleAdder totalResponseTime = new DoubleAdder();
    private final AtomicLong requestCount = new AtomicLong(0);

    public ResponseTimeMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.responseTimer = Timer.builder("app_response_time")
            .description("Time taken to process requests")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        Gauge.builder("app_response_time_min", this::getMinResponseTime)
            .description("Minimum response time in milliseconds")
            .baseUnit("milliseconds")
            .register(meterRegistry);

        Gauge.builder("app_response_time_max", this::getMaxResponseTime)
            .description("Maximum response time in milliseconds")
            .baseUnit("milliseconds")
            .register(meterRegistry);

        Gauge.builder("app_response_time_avg", this::getAvgResponseTime)
            .description("Average response time in milliseconds")
            .baseUnit("milliseconds")
            .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        validateParameters(request, response, filterChain);

        long startTime = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationNanos = System.nanoTime() - startTime;
            recordResponseTime(durationNanos);
        }
    }

    private void validateParameters(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException {
        if (request == null || response == null || filterChain == null) {
            throw new ServletException(ErrorMessage.NULL_REQUEST_RESPONSE);
        }
    }

    private void recordResponseTime(long durationNanos) {
        responseTimer.record(durationNanos, TimeUnit.NANOSECONDS);

        double durationMillis = durationNanos / 1_000_000.0;

        requestCount.incrementAndGet();
        totalResponseTime.add(durationMillis);
        updateMinResponseTime(durationMillis);
        updateMaxResponseTime(durationMillis);
    }

    private void updateMinResponseTime(double durationMillis) {
        long durationLong = (long) durationMillis;
        long currentMin;
        do {
            currentMin = minResponseTime.get();
            if (durationLong < currentMin) {
                minResponseTime.compareAndSet(currentMin, durationLong);
            }
        } while (durationLong < minResponseTime.get());
    }

    private void updateMaxResponseTime(double durationMillis) {
        long durationLong = (long) durationMillis;
        long currentMax;
        do {
            currentMax = maxResponseTime.get();
            if (durationLong > currentMax) {
                maxResponseTime.compareAndSet(currentMax, durationLong);
            }
        } while (durationLong > maxResponseTime.get());
    }

    private double getMinResponseTime() {
        return requestCount.get() == 0 ? 0 : minResponseTime.get();
    }

    private double getMaxResponseTime() {
        return requestCount.get() == 0 ? 0 : maxResponseTime.get();
    }

    private double getAvgResponseTime() {
        long count = requestCount.get();
        return count == 0 ? 0 : totalResponseTime.sum() / count;
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public double getTotalResponseTime() {
        return totalResponseTime.sum();
    }
}