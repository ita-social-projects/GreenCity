package greencity.service;

import static greencity.log.OpenAILogMessages.*;
import static greencity.constant.OpenAIConstants.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@Slf4j
@Service
public class OpenAIServiceImpl implements OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;
    private final RestTemplate restTemplate;

    public OpenAIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Override
    public String makeRequest(String prompt) {
        log.info(OPENAI_REQUEST_INITIATED, prompt);

        String validationError = validateRequestParameters(prompt);
        if (validationError != null) {
            log.error(OPENAI_REQUEST_VALIDATION_FAILED, validationError);
            return validationError;
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBody(prompt);

        return sendRequest(headers, body);
    }

    private String sendRequest(HttpHeaders headers, Map<String, Object> body) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            log.debug(OPENAI_SENDING_REQUEST, apiUrl, body);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );

            log.info(OPENAI_RESPONSE_RECEIVED, response.getBody());

            return Optional.ofNullable(response.getBody())
                .map(responseBody ->    (List<Map<String, Object>>) responseBody.get(RESPONSE_CHOICES_KEY))
                .filter(choices -> !choices.isEmpty())
                .map(choices -> (Map<String, Object>) choices.getFirst().get(RESPONSE_MESSAGE_KEY))
                .map(message -> (String) message.get(RESPONSE_JSON_CONTENT_KEY))
                .filter(content -> !content.isEmpty())
                .orElse(ERROR_INVALID_OPENAI_RESPONSE);
        } catch (Exception e) {
            log.error(OPENAI_REQUEST_FAILED, e);
            return ERROR_NO_OPENAI_RESPONSE;
        }
    }

    private Map<String, Object> createRequestBody(String prompt) {
        log.debug(OPENAI_REQUEST_BODY_CREATION, prompt);
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_MODEL_KEY, OPENAI_MODEL_NAME);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(RESPONSE_ROLE_KEY, ROLE_SYSTEM, RESPONSE_JSON_CONTENT_KEY, AI_LANGUAGE_POLICY));
        messages.add(Map.of(RESPONSE_ROLE_KEY, ROLE_USER, RESPONSE_JSON_CONTENT_KEY, prompt));
        body.put(REQUEST_MESSAGES_KEY, messages);
        body.put(REQUEST_MAX_TOKENS_KEY, 1000);
        body.put(REQUEST_TEMPERATURE_KEY, 0.5);

        return body;
    }

    private HttpHeaders createHttpHeaders() {
        log.debug(OPENAI_HTTP_HEADERS_SETUP);
        HttpHeaders headers = new HttpHeaders();
        headers.add(OPENAI_AUTH_HEADER, OPENAI_BEARER_PREFIX + apiKey);
        headers.add(OPENAI_CONTENT_TYPE_HEADER, OPENAI_APPLICATION_JSON);
        return headers;
    }

    private String validateRequestParameters(String prompt) {
        log.debug(OPENAI_REQUEST_PARAMETER_VALIDATION);
        if (apiKey == null || apiKey.isEmpty()) {
            log.error(ERROR_API_KEY_MISSING);
            return ERROR_API_KEY_MISSING;
        }
        if (apiUrl == null || apiUrl.isEmpty()) {
            log.error(ERROR_API_URL_MISSING);
            return ERROR_API_URL_MISSING;
        }
        if (prompt == null || prompt.isEmpty()) {
            log.error(ERROR_PROMPT_MISSING);
            return ERROR_PROMPT_MISSING;
        }
        return null;
    }
}