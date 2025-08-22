package greencity.service;

import com.google.common.base.CaseFormat;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.enums.EcoNewsLocation;
import greencity.enums.OpenAIResponseFormat;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.exception.exceptions.OpenAIResponseException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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
    private final SecureRandom random;
    private final DateTimeFormatter monthYearFormat;
    private final DateTimeFormatter fullDateTimeFormat;

    public OpenAIServiceImpl(RestClient restClient) {
        this.restClient = restClient;
        this.random = new SecureRandom();
        this.monthYearFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        this.fullDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        OpenAIRequestType.initializeUrls(apiUrl, embeddingApiUrl);
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
        Map<String, Object> body = createRequestBodyForChatCompletion(language, request, responseFormat);
        List<OpenAIResponseDTO> response = sendRequest(headers, body, 1,
            OpenAIRequestType.CHAT_COMPLETION);
        return response.get(0);
    }

    /**
     * Makes a request to the OpenAI API to generate an embedding vector for the
     * given title.
     *
     * <p>
     * This method attempts to send a request up to a maximum number of attempts
     * ({@code MAX_REQUEST_ATTEMPTS}). It uses the embedding model defined in the
     * configuration and sets the specified input and dimensions.
     * </p>
     *
     * @param title the input text (typically a news title) for which to generate an
     *              embedding vector
     * @return the response from the OpenAI API wrapped in {@link OpenAIResponseDTO}
     * @throws OpenAIRequestException if the OpenAI server is unavailable or the
     *                                maximum number of request attempts is reached
     */
    @Override
    public OpenAIResponseDTO makeRequestEmbedding(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new OpenAIRequestException(OPEN_AI_EMBEDDING_INPUT_MISSING);
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBodyForEmbedding(List.of(title));
        List<OpenAIResponseDTO> response = sendRequest(headers, body, 1,
            OpenAIRequestType.EMBEDDING);
        return response.get(0);
    }

    /**
     * Makes a batch request to the OpenAI API to generate embedding vectors for the
     * given list of titles.
     *
     * <p>
     * This method attempts to send the batch request up to a maximum number of
     * attempts ({@code MAX_REQUEST_ATTEMPTS}). It uses the embedding model defined
     * in the configuration and sets the specified inputs and dimensions.
     * </p>
     *
     * @param titles a list of input texts (typically news titles) for which to
     *               generate embedding vectors
     * @return a list of responses from the OpenAI API, each wrapped in
     *         {@link OpenAIResponseDTO}
     * @throws OpenAIRequestException if the input list is {@code null} or empty, if
     *                                the OpenAI server is unavailable, or if the
     *                                maximum number of request attempts is reached
     */
    @Override
    public List<OpenAIResponseDTO> makeRequestEmbeddings(List<String> titles) {
        if (titles == null || titles.isEmpty()) {
            throw new OpenAIRequestException("Input list for batch embedding is empty.");
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBodyForEmbedding(titles);
        return sendRequest(headers, body, titles.size(),
            OpenAIRequestType.BATCH_EMBEDDING);
    }

    /**
     * Creates the request body for the OpenAI API completion request.
     *
     * <p>
     * The body includes the model name, conversation messages (system and user),
     * maximum token count, temperature, and desired response format.
     * </p>
     *
     * <p>
     * The system message defines the behavior and language constraints of the AI,
     * while the user message contains the actual prompt to be processed.
     * </p>
     *
     * @param language       the language context for the AI response
     * @param prompt         the user prompt to be processed
     * @param responseFormat the format in which the AI should respond (e.g., JSON)
     * @return a map representing the structured request body to be sent to the
     *         OpenAI API
     */
    private Map<String, Object> createRequestBodyForChatCompletion(LanguageDTO language,
        String prompt,
        OpenAIResponseFormat responseFormat) {
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_MODEL_KEY, model);

        List<Map<String, String>> messages = new ArrayList<>();
        EcoNewsLocation ecoNewsLocation = EcoNewsLocation.values()[random.nextInt(EcoNewsLocation.values().length)];
        String ecoNewsLocationString = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, ecoNewsLocation.name());
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);
        messages.add(Map.of(
            RESPONSE_ROLE_KEY,
            ROLE_SYSTEM,
            RESPONSE_JSON_CONTENT_KEY,
            String.join(" ",
                AI_ROLE_POLICY,
                AI_FORMATTING_POLICY,
                AI_LANGUAGE_POLICY.formatted(language.getName()))));
        messages.add(Map.of(
            RESPONSE_ROLE_KEY,
            ROLE_USER,
            RESPONSE_JSON_CONTENT_KEY,
            String.join(" ", prompt,
                AI_REQUEST_NEWS_LOCATION.formatted(ecoNewsLocationString),
                AI_REQUEST_KNOWLEDGE_CUT_DATE.formatted(monthYearFormat.format(currentDateTime)),
                AI_REQUEST_IDENTIFIER.formatted(fullDateTimeFormat.format(currentDateTime)),
                AI_LANGUAGE_POLICY.formatted(language.getName()))));
        body.put(REQUEST_MESSAGES_KEY, messages);
        body.put(REQUEST_MAX_TOKENS_KEY, maxCompletionTokens);
        body.put(REQUEST_TEMPERATURE_KEY, temperature);
        body.put(REQUEST_RESPONSE_FORMAT_KEY, responseFormat.getFormat());

        return body;
    }

    /**
     * Creates the request body for the OpenAI API embedding request.
     *
     * <p>
     * The body includes the input texts, model name, and dimensions.
     * </p>
     *
     * @param titles a list of input texts (typically news titles) for which to
     *               generate embedding vectors
     * @return a map representing the structured request body to be sent to the
     *         OpenAI API
     */
    private Map<String, Object> createRequestBodyForEmbedding(List<String> titles) {
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_INPUT_KEY, titles.size() == 1 ? titles.get(0) : titles);
        body.put(REQUEST_MODEL_KEY, embeddingApiModel);
        body.put(REQUEST_DIMENSIONS_KEY, dimensionsTokens);
        return body;
    }

    /**
     * Constructs the HTTP headers required for communication with the OpenAI API.
     *
     * <p>
     * The headers include:
     * <ul>
     * <li>Authorization header with Bearer token using the configured API key</li>
     * <li>Content-Type header specifying JSON format</li>
     * </ul>
     * </p>
     *
     * @return a {@link HttpHeaders} object containing all necessary OpenAI request
     *         headers
     */
    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(OPENAI_AUTH_HEADER, OPENAI_BEARER_PREFIX + apiKey);
        headers.add(OPENAI_CONTENT_TYPE_HEADER, OPENAI_APPLICATION_JSON);
        return headers;
    }

    /**
     * Validates the basic request parameters required to send a prompt to the
     * OpenAI API.
     *
     * <p>
     * Checks if the API key, API URL, and prompt are all present and non-empty. If
     * any of them are invalid or missing, the method returns a corresponding error
     * message.
     * </p>
     *
     * @param prompt the prompt to be validated
     * @return an error message string if any parameter is invalid, or {@code null}
     *         if all parameters are valid
     */
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

    private List<OpenAIResponseDTO> sendRequest(HttpHeaders headers,
        Map<String, Object> body,
        int bodySize,
        OpenAIRequestType requestType) {
        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                Map<String, Object> responseBody = restClient.post()
                    .uri(requestType.getUrl())
                    .headers(headersConsumer -> headersConsumer.addAll(headers))
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

                if (responseBody == null) {
                    throw new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE);
                }

                return requestType.parseResponse(responseBody, bodySize);
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

    private enum OpenAIRequestType {
        CHAT_COMPLETION((responseBody, expectedSize) -> List.of(parseChatResponse(responseBody))),
        EMBEDDING((responseBody, expectedSize) -> List.of(parseResponseEmbedding(responseBody))),
        BATCH_EMBEDDING(OpenAIRequestType::parseBatchResponseEmbedding);

        @Getter
        private String url;
        private final BiFunction<Map<String, Object>, Integer, List<OpenAIResponseDTO>> parser;

        OpenAIRequestType(BiFunction<Map<String, Object>, Integer, List<OpenAIResponseDTO>> parser) {
            this.parser = parser;
        }

        public List<OpenAIResponseDTO> parseResponse(Map<String, Object> responseBody, int expectedSize) {
            return parser.apply(responseBody, expectedSize);
        }

        public static void initializeUrls(String apiUrl, String embeddingApiUrl) {
            CHAT_COMPLETION.url = apiUrl;
            EMBEDDING.url = embeddingApiUrl;
            BATCH_EMBEDDING.url = embeddingApiUrl;
        }

        /**
         * Parses the response from the OpenAI API into a {@link OpenAIResponseDTO}
         * object.
         *
         * @param responseBody the response from the OpenAI API as a Map
         * @return the parsed response as an {@link OpenAIResponseDTO} object
         * @throws OpenAIRequestException if the response is invalid
         */
        private static OpenAIResponseDTO parseChatResponse(Map<String, Object> responseBody) {
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

        /**
         * Parses the response body from the OpenAI embedding API into an
         * {@link OpenAIResponseDTO}.
         *
         * <p>
         * This method extracts the embedding vector, token usage statistics, and the
         * response timestamp. The embedding is converted to a string and set as the
         * content of the DTO.
         * </p>
         *
         * <p>
         * If the response structure is invalid, missing required fields, or has
         * unexpected types, an {@link OpenAIResponseException} is thrown.
         * </p>
         *
         * @param responseBody the raw response map returned by the OpenAI embedding API
         * @return a populated {@link OpenAIResponseDTO} containing the embedding data
         *         and usage info
         * @throws OpenAIResponseException if the response is malformed or cannot be
         *                                 parsed
         */
        private static OpenAIResponseDTO parseResponseEmbedding(Map<String, Object> responseBody) {
            OpenAIResponseDTO openAIResponseDTO = new OpenAIResponseDTO();

            try {
                var dataList = (List<Map<String, Object>>) responseBody.get(RESPONSE_DATA_KEY);
                if (dataList == null || dataList.isEmpty()) {
                    throw new OpenAIResponseException(ERROR_NO_EMBEDDING_FOUND);
                }

                var embeddingEntry = dataList.get(0);
                var embedding = (List<Double>) embeddingEntry.get(RESPONSE_EMBEDDING_KEY);
                openAIResponseDTO.setContent(embedding.toString());

                var usage = (Map<String, Object>) responseBody.get(RESPONSE_USAGE_KEY);
                openAIResponseDTO.setUsedInputTokens((Integer) usage.get(RESPONSE_PROMPT_TOKENS_KEY));
                openAIResponseDTO.setUsedOutputTokens((Integer) usage.get(RESPONSE_TOTAL_TOKENS_KEY));

                openAIResponseDTO.setResponseDateTime(LocalDateTime.now(ZoneOffset.UTC));
            } catch (NullPointerException | ClassCastException e) {
                throw new OpenAIResponseException(ERROR_INVALID_EMBEDDING_FORMAT, e);
            }

            return openAIResponseDTO;
        }

        /**
         * Parses the response from the OpenAI API for a batch embedding request.
         *
         * <p>
         * This method extracts the embedding vectors, token usage information, and
         * assigns the current UTC timestamp as the response time. The number of
         * embeddings in the response is validated against the expected size.
         * </p>
         *
         * @param responseBody the raw response body returned by the OpenAI API
         * @param expectedSize the expected number of embeddings (must match the number
         *                     of input texts sent)
         * @return a list of {@link OpenAIResponseDTO} objects containing the parsed
         *         embeddings and metadata
         * @throws OpenAIResponseException if the response body is malformed, missing
         *                                 required fields, or the number of embeddings
         *                                 does not match the expected size
         */
        private static List<OpenAIResponseDTO> parseBatchResponseEmbedding(Map<String, Object> responseBody,
            int expectedSize) {
            try {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseBody.get(RESPONSE_DATA_KEY);
                if (dataList == null || dataList.size() != expectedSize) {
                    throw new OpenAIResponseException("Mismatch in number of embeddings returned.");
                }

                Map<String, Object> usage = (Map<String, Object>) responseBody.get(RESPONSE_USAGE_KEY);
                int promptTokens = (Integer) usage.get(RESPONSE_PROMPT_TOKENS_KEY);
                int totalTokens = (Integer) usage.get(RESPONSE_TOTAL_TOKENS_KEY);

                LocalDateTime responseTime = LocalDateTime.now(ZoneOffset.UTC);

                return dataList.stream().map(entry -> {
                    OpenAIResponseDTO dto = new OpenAIResponseDTO();
                    dto.setContent(entry.get(RESPONSE_EMBEDDING_KEY).toString());
                    dto.setUsedInputTokens(promptTokens);
                    dto.setUsedOutputTokens(totalTokens);
                    dto.setResponseDateTime(responseTime);
                    return dto;
                }).toList();
            } catch (Exception e) {
                throw new OpenAIResponseException("Invalid format of batch embedding response.", e);
            }
        }
    }
}
