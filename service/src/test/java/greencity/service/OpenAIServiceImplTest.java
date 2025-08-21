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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static greencity.constant.OpenAIConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private final String embeddingApiUrl = "https://api.openai.com/v1/embeddings";
    private final String embeddingApiModel = "text-embedding-3-small";
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
        openAIService.setEmbeddingApiModel(embeddingApiModel);
        openAIService.setEmbeddingApiUrl(embeddingApiUrl);

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

        assertEquals(ERROR_PROMPT_MISSING, ex.getMessage());
        verify(restClient, never()).post();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void makeRequestForEmptyPromptTest(String prompt) {
        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, prompt, OpenAIResponseFormat.TEXT));

        assertEquals(ERROR_PROMPT_MISSING, ex.getMessage());
        verify(restClient, never()).post();
    }

    @Test
    void makeRequestWhenRestClientExceptionTest() {
        when(restClient.post()).thenThrow(new RestClientException(ERROR_NO_OPENAI_RESPONSE));

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT)
        );

        assertInstanceOf(RestClientException.class, ex.getCause());
        assertEquals(ERROR_NO_OPENAI_RESPONSE, ex.getMessage());
        verify(restClient).post();
    }

    @Test
    void makeRequestWhenNullResponseBodyTest() {
        stubRestClient(null);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT));

        assertEquals(ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(MAX_REQUEST_ATTEMPTS)).post();
    }

    @Test
    void makeRequestWhenInvalidResponseBodyTest() {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("id", "123");
        stubRestClient(invalid);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequest(language, "hello", OpenAIResponseFormat.TEXT));

        assertEquals(ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(MAX_REQUEST_ATTEMPTS)).post();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void makeRequestEmbeddingWhenInvalidTitle(String title) {
        assertThrows(OpenAIRequestException.class, () -> openAIService.makeRequestEmbedding(title));
        verify(restClient, never()).post();
    }

    @Test
    void makeRequestEmbeddingWhenValidResponseTest() {
        List<Double> embedding = List.of(0.1, 0.2, 0.3);
        Map<String, Object> embeddingData = Map.of("embedding", embedding, "index", 0, "object", "embedding");
        Map<String, Object> usage = Map.of(
            "prompt_tokens", 5,
            "total_tokens", 5);

        Map<String, Object> apiResponseBody = new HashMap<>();
        apiResponseBody.put("object", "list");
        apiResponseBody.put("data", Collections.singletonList(embeddingData));
        apiResponseBody.put("model", embeddingApiModel);
        apiResponseBody.put("usage", usage);

        stubEmbeddingRestClient(apiResponseBody);

        OpenAIResponseDTO dto = openAIService.makeRequestEmbedding("test title");

        assertNotNull(dto);

        assertEquals(embedding.toString(), dto.getContent());
        assertEquals(5, dto.getUsedInputTokens());
        assertEquals(5, dto.getUsedOutputTokens());
        assertNotNull(dto.getResponseDateTime());
        assertNull(dto.getResponseFormat());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri(embeddingApiUrl);
        verify(requestBodyUriSpec).headers(any());
        verify(requestBodyUriSpec).body(nullable(Map.class));
        verify(requestBodyUriSpec).retrieve();
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    void makeRequestEmbeddingWhenNullResponseBodyTest() {
        stubEmbeddingRestClient(null);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequestEmbedding("test title"));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).post();
        verify(requestBodyUriSpec, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).uri(anyString());
        verify(responseSpec, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).body(any(ParameterizedTypeReference.class));
    }

    @Test
    void makeRequestEmbeddingWhenInvalidResponseBodyTest() {
        Map<String, Object> invalidResponse = new HashMap<>();
        invalidResponse.put("some_other_key", "value");

        stubEmbeddingRestClient(invalidResponse);

        OpenAIRequestException ex = assertThrows(
            OpenAIRequestException.class,
            () -> openAIService.makeRequestEmbedding("test title"));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).post();
        verify(requestBodyUriSpec, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).uri(anyString());
        verify(responseSpec, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).body(any(ParameterizedTypeReference.class));
    }
    @Test
    void makeRequestEmbeddingsWhenValidResponseTest() {
        List<String> titles = List.of("title1", "title2");

        List<Double> emb1 = List.of(0.1, 0.2);
        List<Double> emb2 = List.of(0.3, 0.4);

        Map<String, Object> data1 = Map.of("embedding", emb1, "index", 0);
        Map<String, Object> data2 = Map.of("embedding", emb2, "index", 1);

        Map<String, Object> usage = Map.of("prompt_tokens", 4, "total_tokens", 4);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("data", List.of(data1, data2));
        apiResponse.put("usage", usage);

        stubEmbeddingRestClient(apiResponse);

        List<OpenAIResponseDTO> result = openAIService.makeRequestEmbeddings(titles);

        assertEquals(2, result.size());
        assertEquals(emb1.toString(), result.get(0).getContent());
        assertEquals(4, result.get(0).getUsedInputTokens());
        assertEquals(4, result.get(0).getUsedOutputTokens());

        verify(restClient).post();
        verify(requestBodyUriSpec).uri(embeddingApiUrl);
    }
    @Test
    void makeRequestEmbeddingsWhenInputIsNullOrEmptyTest() {
        assertThrows(OpenAIRequestException.class, () -> openAIService.makeRequestEmbeddings(null));
        assertThrows(OpenAIRequestException.class, () -> openAIService.makeRequestEmbeddings(Collections.emptyList()));

        verify(restClient, never()).post();
    }
    @Test
    void makeRequestEmbeddingsWhenResponseBodyIsNullTest() {
        List<String> titles = List.of("title1");
        stubEmbeddingRestClient(null);

        OpenAIRequestException ex = assertThrows(OpenAIRequestException.class,
                () -> openAIService.makeRequestEmbeddings(titles));

        assertEquals(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED, ex.getMessage());
        verify(restClient, times(OpenAIConstants.MAX_REQUEST_ATTEMPTS)).post();
    }
    @Test
    void makeRequestEmbeddingsWhenDataSizeMismatchTest() {
        List<String> titles = List.of("title1", "title2");

        List<Double> emb1 = List.of(0.1, 0.2);

        Map<String, Object> data1 = Map.of("embedding", emb1, "index", 0);
        Map<String, Object> usage = Map.of("prompt_tokens", 2, "total_tokens", 2);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("data", List.of(data1)); //
        apiResponse.put("usage", usage);

        stubEmbeddingRestClient(apiResponse);

        assertThrows(OpenAIRequestException.class,
                () -> openAIService.makeRequestEmbeddings(titles));
    }
    @Test
    void makeRequestEmbeddingsWhenRestClientThrowsExceptionTest() {
        List<String> titles = List.of("title1");

        when(restClient.post()).thenThrow(new RestClientException("Connection error"));

        OpenAIRequestException ex = assertThrows(OpenAIRequestException.class,
                () -> openAIService.makeRequestEmbeddings(titles));

        assertEquals(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE, ex.getMessage());
        assertInstanceOf(RestClientException.class, ex.getCause());
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

    private void stubEmbeddingRestClient(Map<String, Object> response) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(embeddingApiUrl)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(nullable(Map.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(response);
    }

}