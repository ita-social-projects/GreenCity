package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.enums.OpenAIResponseFormat;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.exception.exceptions.OpenAIResponseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.*;
import static greencity.constant.OpenAIConstants.*;

@Setter
@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;
    @Value("${openai.api.url.embedding}")
    private String embeddingApiUrl;
    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.model.embedding}")
    private String embeddingApiModel;
    @Value("${openai.api.max_completion_tokens}")
    private Integer maxCompletionTokens;
    @Value("${openai.api.dimensions.tokens}")
    private Integer dimensionsTokens;
    @Value("${openai.api.temperature}")
    private Double temperature;

    private final RestClient restClient;

    public OpenAIServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Makes a request to the OpenAI API using the provided language and request.
     *
     * @param language       the language settings for the request
     * @param request        the prompt to send to the OpenAI API
     * @param responseFormat the format of the response
     * @return the response from the OpenAI API as a String
     * @throws OpenAIRequestException if there is a validation error, server is
     *                                unavailable or the maximum number of request
     *                                attempts is reached
     */
    @Override
    public OpenAIResponseDTO makeRequest(LanguageDTO language, String request, OpenAIResponseFormat responseFormat) {
        String validationError = validateRequestParameters(request);
        if (validationError != null) {
            throw new OpenAIRequestException(validationError);
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBody(language, request, responseFormat);

        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                return sendRequest(headers, body);
            } catch (OpenAIResponseException e) {
                log.error(e.getMessage());
                log.error(MESSAGE_CURRENT_ATTEMPT, i);
            } catch (RestClientException e) {
                log.error(e.getMessage());
                log.error(ERROR_ATTEMPTING_STOPPED);
                throw new OpenAIRequestException(ERROR_NO_OPENAI_RESPONSE, e);
            }
        }

        log.error(ERROR_MAX_ATTEMPTS_REACHED);
        throw new OpenAIRequestException(ERROR_MAX_ATTEMPTS_REACHED);
    }

    @Override
    public OpenAIResponseDTO makeRequestEmbedding(String title) {
//        if (ecoNewsDto == null || ecoNewsDto.getTitle() == null || ecoNewsDto.getTitle().isEmpty()) {
//            throw new OpenAIRequestException("EcoNews title must not be null or empty");
//        }
        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("input", title);
        body.put("model", embeddingApiModel);
        body.put("dimensions", dimensionsTokens);
        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                return sendEmbeddingRequest(headers, body);
            } catch (OpenAIResponseException e) {
                log.error(e.getMessage());
                log.error(MESSAGE_CURRENT_ATTEMPT, i);
            } catch (RestClientException e) {
                log.error(e.getMessage());
                log.error(ERROR_ATTEMPTING_STOPPED);
                throw new OpenAIRequestException(ERROR_NO_OPENAI_RESPONSE, e);
            }
        }

        log.error(ERROR_MAX_ATTEMPTS_REACHED);
        throw new OpenAIRequestException(ERROR_MAX_ATTEMPTS_REACHED);
    }



    private Map<String, Object> createRequestBody(LanguageDTO language,
        String prompt,
        OpenAIResponseFormat responseFormat) {
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_MODEL_KEY, model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
            RESPONSE_ROLE_KEY,
            ROLE_SYSTEM,
            RESPONSE_JSON_CONTENT_KEY,
            String.join(" ",
                AI_ROLE_POLICY,
                AI_HEADINGS_POLICY,
                AI_LANGUAGE_POLICY.formatted(language.getName()))));
        messages.add(Map.of(
            RESPONSE_ROLE_KEY,
            ROLE_USER,
            RESPONSE_JSON_CONTENT_KEY,
            String.join(" ", prompt, AI_LANGUAGE_POLICY.formatted(language.getName()))));
        body.put(REQUEST_MESSAGES_KEY, messages);
        body.put(REQUEST_MAX_TOKENS_KEY, maxCompletionTokens);
        body.put(REQUEST_TEMPERATURE_KEY, temperature);
        body.put(REQUEST_RESPONSE_FORMAT_KEY, responseFormat.getFormat());

        return body;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(OPENAI_AUTH_HEADER, OPENAI_BEARER_PREFIX + apiKey);
        headers.add(OPENAI_CONTENT_TYPE_HEADER, OPENAI_APPLICATION_JSON);
        return headers;
    }

    private String validateRequestParameters(String prompt) {
        Map<Object, String> validationResults = new HashMap<>();
        validationResults.put(apiKey, ERROR_API_KEY_MISSING);
        validationResults.put(apiUrl, ERROR_API_URL_MISSING);
        validationResults.put(prompt, ERROR_PROMPT_MISSING);

        return validationResults.entrySet().stream()
            .filter(entry -> Objects.isNull(entry.getKey())
                || entry.getKey().toString().trim().isEmpty())
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Sends a request to the OpenAI API using the provided headers and body.
     *
     * @param headers the headers to include in the request
     * @param body    the body of the request
     * @return the response from the OpenAI API as a String
     * @throws OpenAIRequestException if the request is invalid or the OpenAI
     *                                service is unavailable
     */
    private OpenAIResponseDTO sendRequest(HttpHeaders headers, Map<String, Object> body) throws OpenAIRequestException {
        Map<String, Object> responseBody = restClient.post()
            .uri(apiUrl)
            .headers(headersConsumer -> headersConsumer.addAll(headers))
            .body(body)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {
            });

        if (responseBody == null) {
            throw new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE);
        }

        return parseResponse(responseBody);
    }
    private OpenAIResponseDTO sendEmbeddingRequest(HttpHeaders headers, Map<String, Object> body) throws OpenAIRequestException {
        Map<String, Object> responseBody = restClient.post()
                .uri(embeddingApiUrl)
                .headers(headersConsumer -> headersConsumer.addAll(headers))
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (responseBody == null) {
            throw new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE);
        }

        return parseResponseEmbedding(responseBody);
    }

    /**
     * Parses the response from the OpenAI API into a {@link OpenAIResponseDTO}
     * object.
     *
     * @param responseBody the response from the OpenAI API as a Map
     * @return the parsed response as an {@link OpenAIResponseDTO} object
     * @throws OpenAIRequestException if the response is invalid
     */
    private OpenAIResponseDTO parseResponse(Map<String, Object> responseBody) {
        OpenAIResponseDTO openAIResponseDTO = new OpenAIResponseDTO();

        try {
            openAIResponseDTO.setId((String) responseBody.get(RESPONSE_ID_KEY));

            var choices = (List<Map<String, Object>>) responseBody.get(RESPONSE_CHOICES_KEY);
            var choice = choices.get(0);
            var message = (Map<String, Object>) choice.get(RESPONSE_MESSAGE_KEY);
            openAIResponseDTO.setContent((String) message.get(RESPONSE_JSON_CONTENT_KEY));

            var usage = (Map<String, Object>) responseBody.get(RESPONSE_USAGE_KEY);
            openAIResponseDTO.setUsedInputTokens((Integer) usage.get(RESPONSE_PROMPT_TOKENS_KEY));
            openAIResponseDTO.setUsedOutputTokens((Integer) usage.get(RESPONSE_COMPLETION_TOKENS_KEY));

            openAIResponseDTO.setResponseDateTime(LocalDateTime.ofEpochSecond(
                ((Integer) responseBody.get(RESPONSE_CREATED_KEY)).longValue(), 0, ZoneOffset.UTC));
        } catch (NullPointerException | ClassCastException e) {
            throw new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE, e);
        }

        return openAIResponseDTO;
    }

    private OpenAIResponseDTO parseResponseEmbedding(Map<String, Object> responseBody) {
        OpenAIResponseDTO openAIResponseDTO = new OpenAIResponseDTO();

        try {
            // Extract embedding from data[0].embedding
            var dataList = (List<Map<String, Object>>) responseBody.get("data");
            if (dataList == null || dataList.isEmpty()) {
                throw new OpenAIResponseException("No embedding data found in OpenAI response.");
            }

            var embeddingEntry = dataList.get(0);
            var embedding = (List<Double>) embeddingEntry.get("embedding");
            openAIResponseDTO.setContent(embedding.toString());


            // Extract usage
            var usage = (Map<String, Object>) responseBody.get("usage");
            openAIResponseDTO.setUsedInputTokens((Integer) usage.get("prompt_tokens"));
            openAIResponseDTO.setUsedOutputTokens((Integer) usage.get("total_tokens"));

            // Optionally: set response time to now
            openAIResponseDTO.setResponseDateTime(LocalDateTime.now(ZoneOffset.UTC));
        } catch (NullPointerException | ClassCastException e) {
            throw new OpenAIResponseException("Invalid OpenAI embedding response format", e);
        }

        return openAIResponseDTO;
    }

}
