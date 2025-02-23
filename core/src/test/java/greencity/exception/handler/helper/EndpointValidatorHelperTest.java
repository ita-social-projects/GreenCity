package greencity.exception.handler.helper;

import greencity.constant.ErrorMessage;
import greencity.exception.helper.EndpointValidationHelper;
import greencity.validator.EndpointValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndpointValidatorHelperTest {

    @Mock
    private EndpointValidator endpointValidator;

    @Mock
    private HttpRequestMethodNotSupportedException exception;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private EndpointValidationHelper endpointValidationHelper;

    private List<String> allowedMethods;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        endpointValidationHelper = new EndpointValidationHelper(endpointValidator); // Реальний об'єкт
        allowedMethods = List.of("GET", "POST");
    }

    @Test
    void ResponseWithExtraCharactersTest() {
        String url = "/api/invalid/extraa";
        when(httpHeaders.getOrEmpty(HttpHeaders.ALLOW)).thenReturn(List.of("GET", "POST"));
        ServletWebRequest servletWebRequest = new ServletWebRequest(servletRequest);
        when(servletRequest.getRequestURI()).thenReturn(url);
        when(endpointValidator.checkUrl(url)).thenReturn(Boolean.FALSE);

        ResponseEntity<Object> response = endpointValidationHelper.response(null, httpHeaders, servletWebRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Not Found", responseBody.get("error"));
        assertEquals(String.format("No endpoint found for %s", url), responseBody.get("message"));
    }

    @Test
    void ResponseWithMethodNotAllowedTest() {
        String url = "/api/test";
        String method = "PUT";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(url);
        request.setMethod(method);
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        when(httpHeaders.getOrEmpty(HttpHeaders.ALLOW)).thenReturn(List.of("GET", "POST")); // Додаємо дозволені методи
        when(exception.getMethod()).thenReturn(method);
        when(endpointValidator.checkUrl(url)).thenReturn(true);

        ResponseEntity<Object> response = endpointValidationHelper.response(exception, httpHeaders, servletWebRequest);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(ErrorMessage.METHOD_NOT_ALLOWED, responseBody.get("error"));
        assertEquals(
            String.format("Method %s is not allowed for %s. Supported Methods: %s", method, url,
                List.of("GET", "POST")),
            responseBody.get("message"));
    }

    @Test
    void EvaluateConditionWithExtraCharactersTest() {
        String url = "/api/invalid/extraa";
        String method = "GET";
        when(endpointValidator.checkUrl(url)).thenReturn(Boolean.FALSE);

        String condition = endpointValidationHelper.evaluateCondition(url, method, allowedMethods);

        assertEquals("extraCharacters", condition);
    }

    @Test
    void EvaluateConditionWithMethodNotAllowedTest() {
        String url = "/api/test";
        String method = "PUT";

        when(endpointValidator.checkUrl(url)).thenReturn(true);
        String condition = endpointValidationHelper.evaluateCondition(url, method, allowedMethods);

        assertEquals("methodNotAllowed", condition);
    }

    @Test
    void GetUrlFromRequestTest() {
        String expectedUrl = "/api/test";
        when(webRequest.getDescription(false)).thenReturn("uri=" + expectedUrl);

        String actualUrl = endpointValidationHelper.getUrlFromRequest(webRequest);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void EvaluateConditionWithValidPlaceholderUrlTest() {
        String actualUrl = "/management/users/456/friends";
        String method = "GET";
        when(endpointValidator.checkUrl(actualUrl)).thenReturn(Boolean.TRUE);

        String condition = endpointValidationHelper.evaluateCondition(actualUrl, method, allowedMethods);

        assertEquals("default", condition);
    }

    @Test
    void EvaluateConditionWithInvalidPlaceholderUrlTest() {
        String actualUrl = "/management/users/{invalid}/delete-filtr";
        String method = "POST";
        when(endpointValidator.checkUrl(actualUrl)).thenReturn(Boolean.FALSE);

        String condition = endpointValidationHelper.evaluateCondition(actualUrl, method, allowedMethods);

        assertEquals("extraCharacters", condition);
    }
}
