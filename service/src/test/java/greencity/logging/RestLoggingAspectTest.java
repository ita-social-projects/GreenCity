package greencity.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.isNull;

@ExtendWith(MockitoExtension.class)
class RestLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RestLoggingAspect restLoggingAspect;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    void logRestCall_successfulExecution() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getQueryString()).thenReturn("param=value");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(response.getStatus()).thenReturn(200);
        when(objectMapper.writeValueAsString("requestBody")).thenReturn("\"requestBody\"");
        when(objectMapper.writeValueAsString(result)).thenReturn("\"responseBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenNoServletRequestAttributes() throws Throwable {
        RequestContextHolder.resetRequestAttributes();
        Object result = "resultWithoutAttributes";

        when(joinPoint.proceed()).thenReturn(result);

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenSerializationRequestFails() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(response.getStatus()).thenReturn(201);

        when(objectMapper.writeValueAsString("requestBody"))
            .thenThrow(new JsonProcessingException("Serialization failed") {
            });
        when(objectMapper.writeValueAsString(result)).thenReturn("\"responseBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenSerializationResponseFails() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(response.getStatus()).thenReturn(200);

        when(objectMapper.writeValueAsString("requestBody")).thenReturn("\"requestBody\"");
        when(objectMapper.writeValueAsString(result))
            .thenThrow(new JsonProcessingException("Serialization failed") {
            });

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenControllerThrowsException() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        RuntimeException exception = new RuntimeException("Controller exception");

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenThrow(exception);
        when(objectMapper.writeValueAsString("requestBody")).thenReturn("\"requestBody\"");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> restLoggingAspect.logRestCall(joinPoint));
        assertEquals(exception, thrown);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenRequestHasNoBody() throws Throwable {
        Object[] args = new Object[] {};

        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(response.getStatus()).thenReturn(200);
        when(objectMapper.writeValueAsString(result)).thenReturn("\"responseBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_whenResponseIsNull() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        Object result = null;

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(null);
        when(response.getStatus()).thenReturn(204);
        when(objectMapper.writeValueAsString("requestBody")).thenReturn("\"requestBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertNull(actualResult);
        verify(joinPoint, times(1)).proceed();
        verify(objectMapper, never()).writeValueAsString((Object) isNull());
    }

    @Test
    void logRestCall_whenResponseIsNullAndDefaultStatusUsed() throws Throwable {
        Object[] args = new Object[] {"requestBody"};
        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(objectMapper.writeValueAsString("requestBody")).thenReturn("\"requestBody\"");
        when(objectMapper.writeValueAsString(result)).thenReturn("\"responseBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logRestCall_withNonSerializableArguments() throws Throwable {
        Object[] args = new Object[] {null, request, response};

        Object result = "responseBody";

        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(servletRequestAttributes.getResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(result);
        when(response.getStatus()).thenReturn(200);
        when(objectMapper.writeValueAsString(result)).thenReturn("\"responseBody\"");

        Object actualResult = restLoggingAspect.logRestCall(joinPoint);

        assertEquals(result, actualResult);
        verify(joinPoint, times(1)).proceed();
    }
}