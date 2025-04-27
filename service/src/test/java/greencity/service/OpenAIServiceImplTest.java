package greencity.service;

import greencity.utils.NullStringToNullConverter;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceImplTest {
    @InjectMocks
    private OpenAIServiceImpl openAIService;

    @Mock
    private RestTemplate restTemplate;
    String apiKey = "mock-api-key";
    String apiUrl = "https://api.openai.com/v1/chat/completions";

    @BeforeEach
    void setUp() {
        openAIService.setApiKey(apiKey);
        openAIService.setApiUrl(apiUrl);
    }

    @Test
    void makeRequest_returnsResponseContent() {
        Map<String, Object> mockResponseBody = Map.of(
            "choices", List.of(
                Map.of("message", Map.of("content", "Hello, how can I help you?"))));

        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals("Hello, how can I help you?", result);
    }

    @Test
    void makeRequest_returnsErrorMessage_whenResponseIsInvalid() {
        Map<String, Object> mockResponseBody = Map.of();

        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals("Could not get a valid response from OpenAI.", result);
    }

    @Test
    void makeRequest_returnsErrorMessage_whenResponseContainsNoChoices() {
        Map<String, Object> mockResponseBody = Collections.singletonMap("choices", null);

        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals("Could not get a valid response from OpenAI.", result);
    }

    @Test
    void makeRequest_returnsErrorMessage_whenResponseContainsEmptyChoices() {
        Map<String, Object> mockResponseBody = Map.of("choices", List.of());

        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals("Could not get a valid response from OpenAI.", result);
    }

    @Test
    void makeRequest_returnsErrorMessage_whenResponseContainsChoiceWithNoMessage() {
        Map<String, Object> mockResponseBody =
            Collections.singletonMap("choices", List.of(Collections.singletonMap("message", null)));

        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals("Could not get a valid response from OpenAI.", result);
    }

    @ParameterizedTest
    @CsvSource({
        "'null', https://api.openai.com/v1/chat/completions, Say hello, OpenAI API key is missing!",
        "mock-api-key, 'null', Say hello, OpenAI API URL is missing!",
        "mock-api-key, https://api.openai.com/v1/chat/completions, '', Prompt is missing!",
        "mock-api-key, https://api.openai.com/v1/chat/completions, 'null', Prompt is missing!",
        "'', https://api.openai.com/v1/chat/completions, Say hello, OpenAI API key is missing!",
        "mock-api-key, '', Say hello, OpenAI API URL is missing!"
    })
    void makeRequest_returnsErrorMessage_whenParametersAreInvalid(
        @ConvertWith(NullStringToNullConverter.class) String apiKey,
        @ConvertWith(NullStringToNullConverter.class) String apiUrl,
        @ConvertWith(NullStringToNullConverter.class) String prompt,
        String expectedMessage) {

        openAIService.setApiKey(apiKey);
        openAIService.setApiUrl(apiUrl);

        String result = openAIService.makeRequest(prompt);

        assertEquals(expectedMessage, result);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidResponses")
    void makeRequest_returnsErrorMessage_whenResponseIsInvalid(
        Map<String, Object> mockResponseBody,
        String expectedMessage) {
        when(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(new ParameterizedTypeReference<Map<String, Object>>() {
            }))).thenReturn(new ResponseEntity<>(mockResponseBody, HttpStatus.OK));

        String result = openAIService.makeRequest("Say hello");

        assertEquals(expectedMessage, result);
    }

    private static Stream<Arguments> provideInvalidResponses() {
        return Stream.of(
            Arguments.of(
                Collections.singletonMap("choices", null),
                "Could not get a valid response from OpenAI."),
            Arguments.of(
                Map.of("choices", List.of()),
                "Could not get a valid response from OpenAI."),
            Arguments.of(
                Map.of("choices",
                    List.of(Collections.singletonMap("message", Collections.emptyMap()))),
                "Could not get a valid response from OpenAI."),
            Arguments.of(
                Map.of("choices",
                    List.of(Collections.singletonMap("message", Map.of("content", "")))),
                "Could not get a valid response from OpenAI."));
    }
}
