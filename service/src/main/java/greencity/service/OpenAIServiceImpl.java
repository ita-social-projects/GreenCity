package greencity.service;

import com.google.common.base.CaseFormat;
import greencity.constant.OpenAIConstants;
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Setter
@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;
    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.max_completion_tokens}")
    private Integer maxCompletionTokens;
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

        for (int i = 1; i <= OpenAIConstants.MAX_REQUEST_ATTEMPTS; i++) {
            try {
                return sendRequest(headers, body);
            } catch (OpenAIResponseException e) {
                log.error(e.getMessage());
                log.error(OpenAIConstants.MESSAGE_CURRENT_ATTEMPT, i);
            } catch (RestClientException e) {
                log.error(e.getMessage());
                log.error(OpenAIConstants.ERROR_ATTEMPTING_STOPPED);
                throw new OpenAIRequestException(OpenAIConstants.ERROR_NO_OPENAI_RESPONSE, e);
            }
        }

        log.error(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED);
        throw new OpenAIRequestException(OpenAIConstants.ERROR_MAX_ATTEMPTS_REACHED);
    }

    private Map<String, Object> createRequestBody(LanguageDTO language,
        String prompt,
        OpenAIResponseFormat responseFormat) {
        Map<String, Object> body = new HashMap<>();
        body.put(OpenAIConstants.REQUEST_MODEL_KEY, model);

        List<Map<String, String>> messages = new ArrayList<>();
        EcoNewsLocation ecoNewsLocation = EcoNewsLocation.values()[random.nextInt(EcoNewsLocation.values().length)];
        String ecoNewsLocationString = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, ecoNewsLocation.name());
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);
        messages.add(Map.of(
            OpenAIConstants.RESPONSE_ROLE_KEY,
            OpenAIConstants.ROLE_SYSTEM,
            OpenAIConstants.RESPONSE_JSON_CONTENT_KEY,
            String.join(" ",
                OpenAIConstants.AI_ROLE_POLICY,
                OpenAIConstants.AI_FORMATTING_POLICY,
                OpenAIConstants.AI_LANGUAGE_POLICY.formatted(language.getName()))));
        messages.add(Map.of(
            OpenAIConstants.RESPONSE_ROLE_KEY,
            OpenAIConstants.ROLE_USER,
            OpenAIConstants.RESPONSE_JSON_CONTENT_KEY,
            String.join(" ", prompt,
                OpenAIConstants.AI_REQUEST_NEWS_LOCATION.formatted(ecoNewsLocationString),
                OpenAIConstants.AI_REQUEST_KNOWLEDGE_CUT_DATE.formatted(monthYearFormat.format(currentDateTime)),
                OpenAIConstants.AI_REQUEST_IDENTIFIER.formatted(fullDateTimeFormat.format(currentDateTime)),
                OpenAIConstants.AI_LANGUAGE_POLICY.formatted(language.getName()))));
        body.put(OpenAIConstants.REQUEST_MESSAGES_KEY, messages);
        body.put(OpenAIConstants.REQUEST_MAX_TOKENS_KEY, maxCompletionTokens);
        body.put(OpenAIConstants.REQUEST_TEMPERATURE_KEY, temperature);
        body.put(OpenAIConstants.REQUEST_RESPONSE_FORMAT_KEY, responseFormat.getFormat());

        return body;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(OpenAIConstants.OPENAI_AUTH_HEADER, OpenAIConstants.OPENAI_BEARER_PREFIX + apiKey);
        headers.add(OpenAIConstants.OPENAI_CONTENT_TYPE_HEADER, OpenAIConstants.OPENAI_APPLICATION_JSON);
        return headers;
    }

    private String validateRequestParameters(String prompt) {
        Map<Object, String> validationResults = new HashMap<>();
        validationResults.put(apiKey, OpenAIConstants.ERROR_API_KEY_MISSING);
        validationResults.put(apiUrl, OpenAIConstants.ERROR_API_URL_MISSING);
        validationResults.put(prompt, OpenAIConstants.ERROR_PROMPT_MISSING);

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
            throw new OpenAIResponseException(OpenAIConstants.ERROR_INVALID_OPENAI_RESPONSE);
        }

        return parseResponse(responseBody);
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
            openAIResponseDTO.setId((String) responseBody.get(OpenAIConstants.RESPONSE_ID_KEY));

            var choices = (List<Map<String, Object>>) responseBody.get(OpenAIConstants.RESPONSE_CHOICES_KEY);
            var choice = choices.get(0);
            var message = (Map<String, Object>) choice.get(OpenAIConstants.RESPONSE_MESSAGE_KEY);
            openAIResponseDTO.setContent((String) message.get(OpenAIConstants.RESPONSE_JSON_CONTENT_KEY));

            var usage = (Map<String, Object>) responseBody.get(OpenAIConstants.RESPONSE_USAGE_KEY);
            openAIResponseDTO.setUsedInputTokens((Integer) usage.get(OpenAIConstants.RESPONSE_PROMPT_TOKENS_KEY));
            openAIResponseDTO.setUsedOutputTokens((Integer) usage.get(OpenAIConstants.RESPONSE_COMPLETION_TOKENS_KEY));

            openAIResponseDTO.setResponseDateTime(LocalDateTime.ofEpochSecond(((Integer) responseBody
                .get(OpenAIConstants.RESPONSE_CREATED_KEY)).longValue(), 0, ZoneOffset.UTC));
        } catch (NullPointerException | ClassCastException e) {
            throw new OpenAIResponseException(OpenAIConstants.ERROR_INVALID_OPENAI_RESPONSE, e);
        }

        return openAIResponseDTO;
    }
}
