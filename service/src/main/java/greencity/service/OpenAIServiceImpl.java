package greencity.service;

import static greencity.utils.OpenAIConstants.*;
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
        String validationError = validateRequestParameters(prompt);

        if (validationError != null) {
            return validationError;
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBody(prompt);

        return sendRequest(headers, body);
    }

    private String sendRequest(HttpHeaders headers, Map<String, Object> body) {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );
            return Optional.ofNullable(response.getBody())
                .map(responseBody -> (List<Map<String, Object>>) responseBody.get(CHOICES_KEY))
                .filter(choices -> !choices.isEmpty())
                .map(choices -> (Map<String, Object>) choices.getFirst().get(MESSAGE_KEY))
                .map(message -> (String) message.get(JSON_CONTENT_KEY))
                .filter(content -> !content.isEmpty())
                .orElse(ERROR_INVALID_RESPONSE);
        } catch (Exception e) {
            return ERROR_NO_RESPONSE;
        }
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put(MODEL_KEY, MODEL_NAME);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(ROLE_KEY, SYSTEM_ROLE, JSON_CONTENT_KEY, LANGUAGE_POLICY));
        messages.add(Map.of(ROLE_KEY, USER_ROLE, JSON_CONTENT_KEY, prompt));
        body.put(MESSAGES_KEY, messages);
        body.put(MAX_TOKENS_KEY, 1000);
        body.put(TEMPERATURE_KEY, 0.5);

        return body;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, BEARER_PREFIX + apiKey);
        headers.add(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE);
        return headers;
    }

    private String validateRequestParameters(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return ERROR_MISSING_API_KEY;
        }
        if (apiUrl == null || apiUrl.isEmpty()) {
            return ERROR_MISSING_API_URL;
        }
        if (prompt == null || prompt.isEmpty()) {
            return ERROR_MISSING_PROMPT;
        }
        return null;
    }
}
