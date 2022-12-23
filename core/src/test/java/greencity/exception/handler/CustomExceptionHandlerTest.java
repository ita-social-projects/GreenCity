package greencity.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.UserDeactivatedException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {
    @Mock
    private ErrorAttributes errorAttributes;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private CustomExceptionHandler exceptionHandler;
    private static final String EXCEPTION_MESSAGE = "Example failure";
    private static final String MESSAGE_JSON = "{ \"message\":\"" + EXCEPTION_MESSAGE + "\" }";
    private static final String INCORRECT_MESSAGE_JSON = "Not a json";
    private static final String REQUEST_URI = "api/example";
    private static final String TRACE = "example trace";
    private static final String TIME_STAMP = LocalDateTime.of(2022, 12, 23, 13, 52).toString();

    @Test
    void testHandleHttpClientErrorException() throws JsonProcessingException {
        // given
        HttpClientErrorException exception =
            new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "statusText", MESSAGE_JSON.getBytes(),
                Charset.defaultCharset());
        WebRequest webRequest = givenWebRequestMock();

        Map<String, Object> stubbedErrorAttributes = new HashMap<>();
        stubbedErrorAttributes.put("timestamp", TIME_STAMP);
        stubbedErrorAttributes.put("trace", TRACE);
        when(errorAttributes.getErrorAttributes(webRequest, true)).thenReturn(stubbedErrorAttributes);

        // when
        ResponseEntity<Object> result =
            exceptionHandler.handleHttpClientErrorException(exception, webRequest);

        // then
        HttpClientErrorExceptionResponse expectedResponseBody =
            new HttpClientErrorExceptionResponse(stubbedErrorAttributes,
                EXCEPTION_MESSAGE, REQUEST_URI);

        assertEquals(expectedResponseBody, result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(objectMapper, times(1)).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    void testHandleHttpClientErrorException_ThrowsJsonProcessingExceptionWhenExceptionDoesntContainJsonMessage() {
        // given
        HttpClientErrorException exception =
            new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "statusText",
                INCORRECT_MESSAGE_JSON.getBytes(), Charset.defaultCharset());
        WebRequest webRequest = givenWebRequestMock();

        // when
        Executable executable = () -> exceptionHandler.handleHttpClientErrorException(exception, webRequest);

        // then
        assertThrows(JsonProcessingException.class, executable);
    }

    @Test
    void testHandleUserDeactivatedExceptionTest() {
        // given
        UserDeactivatedException ex = new UserDeactivatedException(ErrorMessage.USER_DEACTIVATED);
        WebRequest webRequest = givenWebRequestMock();

        Map<String, Object> stubbedErrorAttributes = givenStubbedErrorAttributes(EXCEPTION_MESSAGE);
        when(errorAttributes.getErrorAttributes(webRequest, true)).thenReturn(stubbedErrorAttributes);

        // when
        ResponseEntity<Object> response = exceptionHandler.handleUserDeactivatedException(ex, webRequest);

        // then
        ExceptionResponse expectedResponseBody = new ExceptionResponse(stubbedErrorAttributes);
        assertEquals(expectedResponseBody, response.getBody());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private static Map<String, Object> givenStubbedErrorAttributes(String errorMessage) {
        Map<String, Object> stubbedErrorAttributes = new HashMap<>();
        stubbedErrorAttributes.put("timestamp", TIME_STAMP);
        stubbedErrorAttributes.put("trace", TRACE);
        stubbedErrorAttributes.put("message", errorMessage);
        stubbedErrorAttributes.put("path", REQUEST_URI);
        return stubbedErrorAttributes;
    }

    private static WebRequest givenWebRequestMock() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(REQUEST_URI);
        return new ServletWebRequest(request);
    }
}