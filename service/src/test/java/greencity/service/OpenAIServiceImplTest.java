package greencity.service;

import greencity.ModelUtils;
import greencity.constant.OpenAIConstants;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.enums.OpenAIResponseFormat;
import greencity.exception.exceptions.OpenAIRequestException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceImplTest {

    private final String apiKey = "mock-api-key";
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    private final String model = "gpt-3.5-turbo";
    private final Integer maxCompletionTokens = 100;
    private final Double temperature = 0.5;
    private final LanguageDTO language = ModelUtils.getLanguageDTO();

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @InjectMocks
    private OpenAIServiceImpl openAIService;

    @BeforeEach
    void setUp() {
        openAIService.setApiKey(apiKey);
        openAIService.setApiUrl(apiUrl);
        openAIService.setModel(model);
        openAIService.setMaxCompletionTokens(maxCompletionTokens);
        openAIService.setTemperature(temperature);
    }

    @Test
    void makeRequestWhenValidResponseTest() {
        long epoch = Instant.now().getEpochSecond();
        Map<String, Object> usage = Map.of(
            "prompt_tokens", 2,
            "completion_tokens", 3);
        Map<String, Object> message = Map.of("content", "world");
        Map<String, Object> choice = Map.of("message", message);
        Map<String, Object> body = new HashMap<>();
        body.put("id", "resp-1");
        body.put("choices", Collections.singletonList(choice));
        body.put("usage", usage);
        body.put("created", (int) epoch);
        stubRestClient(body);

        OpenAIResponseDTO dto = openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT);

        assertEquals("resp-1", dto.getId());
        assertEquals("world", dto.getContent());
        assertEquals(2, dto.getUsedInputTokens());
        assertEquals(3, dto.getUsedOutputTokens());
        assertEquals(LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC), dto.getResponseDateTime());
        verify(restClient).post();
    }

    @Test
    void makeRequestForNullPromptTest() {
        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, null, OpenAIResponseFormat.TEXT));

        assertEquals(OpenAIConstants.ERROR_PROMPT_MISSING, ex.getMessage());
        verify(restClient, never()).post();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void makeRequestForEmptyPromptTest(String prompt) {
        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, prompt, OpenAIResponseFormat.TEXT));

        assertEquals(OpenAIConstants.ERROR_PROMPT_MISSING, ex.getMessage());
        verify(restClient, never()).post();
    }

    @Test
    void makeRequestWhenRestClientExceptionTest() {
        when(restClient.post()).thenThrow(new RestClientException(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE));

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT)
        );

        assertInstanceOf(RestClientException.class, ex.getCause());
        assertEquals(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE, ex.getMessage());
        verify(restClient).post();
    }

    @Test
    void makeRequestWhenNullResponseBodyTest() {
        stubRestClient(null);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).post();
    }

    @Test
    void makeRequestWhenInvalidResponseBodyTest() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("id", "123");
        stubRestClient(invalid);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).post();
    }

    private void stubRestClient(Map<String, Object> response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(nullable(Map.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
            .thenReturn(response);
    }
}