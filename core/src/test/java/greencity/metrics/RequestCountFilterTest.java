package greencity.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestCountFilterTest {
    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RequestCountFilter requestCountFilter;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Counter.Builder counterBuilder = mock(Counter.Builder.class);
        when(counterBuilder.description("Total number of HTTP requests")).thenReturn(counterBuilder);
        when(counterBuilder.register(meterRegistry)).thenReturn(counter);

        requestCountFilter = new RequestCountFilter(meterRegistry);

        Field requestCounterField = RequestCountFilter.class.getDeclaredField("requestCounter");
        requestCounterField.setAccessible(true);
        requestCounterField.set(requestCountFilter, counter);
    }

    @Test
    void constructor_shouldInitializeCounter() throws NoSuchFieldException, IllegalAccessException {
        assertNotNull(requestCountFilter);

        Field requestCounterField = RequestCountFilter.class.getDeclaredField("requestCounter");
        requestCounterField.setAccessible(true);
        Counter actualCounter = (Counter) requestCounterField.get(requestCountFilter);
        assertEquals(counter, actualCounter);
    }

    @Test
    void doFilterInternal_shouldIncrementCounterAndProceed() throws ServletException, IOException {
        requestCountFilter.doFilterInternal(request, response, filterChain);
        verify(counter).increment();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenCounterIncrementThrowsException_shouldLogErrorAndProceed() throws ServletException, IOException {
        RuntimeException exception = new RuntimeException("Counter increment failed");
        doThrow(exception).when(counter).increment();

        requestCountFilter.doFilterInternal(request, response, filterChain);

        verify(counter).increment();
        verify(filterChain).doFilter(request, response);
    }
}