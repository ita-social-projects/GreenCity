package greencity.service;

import static greencity.constant.OpenAIConstants.*;

import greencity.dto.language.LanguageDTO;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.exception.exceptions.OpenAIResponseException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    /**
     * Makes a request to the OpenAI API using the provided language and prompt.
     *
     * @param language the language settings for the request
     * @param prompt   the prompt to send to the OpenAI API
     * @return the response from the OpenAI API as a String
     * @throws OpenAIRequestException if there is a validation error, server is unavailable
     *                                or the maximum number of request attempts is reached
     */
    @Override
    public String makeRequest(LanguageDTO language, String prompt) {
        String validationError = validateRequestParameters(prompt);
        if (validationError != null) {
            throw new OpenAIRequestException(validationError);
        }

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> body = createRequestBody(language, prompt);

        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                return sendRequest(headers, body);
            } catch (OpenAIResponseException e) {
                log.error(e.getMessage());
                log.error(MESSAGE_CURRENT_ATTEMPT, i);
            } catch (RestClientException e) {
                log.error(e.getMessage());
                log.error(ERROR_ATTEMPTING_STOPPED);
                throw new OpenAIRequestException(ERROR_NO_OPENAI_RESPONSE);
            }
        }

        log.error(ERROR_MAX_ATTEMPTS_REACHED);
        throw new OpenAIRequestException(ERROR_MAX_ATTEMPTS_REACHED);
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
    private String sendRequest(HttpHeaders headers, Map<String, Object> body) throws OpenAIRequestException {
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<>() {
            }
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE);
        }

        return Optional.ofNullable(responseBody.get(RESPONSE_CHOICES_KEY))
            .filter(choices -> choices instanceof List<?> && !((List<?>) choices).isEmpty())
            .map(choices -> (List<?>) choices)
            .flatMap(choices -> Optional.ofNullable(choices.getFirst()))
            .filter(choice -> choice instanceof Map<?, ?>)
            .map(choice -> (Map<?, ?>) choice)
            .flatMap(choice -> Optional.ofNullable(choice.get(RESPONSE_MESSAGE_KEY)))
            .filter(message -> message instanceof Map<?, ?>)
            .map(message -> (Map<?, ?>) message)
            .flatMap(message -> Optional.ofNullable(message.get(RESPONSE_JSON_CONTENT_KEY)))
            .filter(content -> content instanceof String && !((String) content).isEmpty())
            .map(content -> (String) content)
            .orElseThrow(() -> new OpenAIResponseException(ERROR_INVALID_OPENAI_RESPONSE));
    }

    private Map<String, Object> createRequestBody(LanguageDTO language, String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_MODEL_KEY, OPENAI_MODEL_NAME);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                RESPONSE_ROLE_KEY,
                ROLE_SYSTEM,
                RESPONSE_JSON_CONTENT_KEY,
                String.join(" ",
                        AI_ROLE_POLICY,
                        AI_MAX_TOKENS_POLICY,
                        AI_HEADINGS_POLICY,
                        AI_LANGUAGE_POLICY.formatted(language.getName())
                )
        ));
        messages.add(Map.of(
            RESPONSE_ROLE_KEY,
            ROLE_USER,
            RESPONSE_JSON_CONTENT_KEY,
            String.join(" ", prompt, AI_LANGUAGE_POLICY.formatted(language.getName()))
        ));
        body.put(REQUEST_MESSAGES_KEY, messages);
        body.put(REQUEST_MAX_TOKENS_KEY, 1000);
        body.put(REQUEST_TEMPERATURE_KEY, 0.5);

        return body;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(OPENAI_AUTH_HEADER, OPENAI_BEARER_PREFIX + apiKey);
        headers.add(OPENAI_CONTENT_TYPE_HEADER, OPENAI_APPLICATION_JSON);
        return headers;
    }

    private String validateRequestParameters(String prompt) {
        Map<Object, String> validationResults = Map.of(
                apiKey, ERROR_API_KEY_MISSING,
                apiUrl, ERROR_API_URL_MISSING,
                prompt, ERROR_PROMPT_MISSING
        );

        return validationResults.entrySet().stream()
                .filter(entry -> Objects.isNull(entry.getKey()) ||
                        entry.getKey().toString().isEmpty())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
