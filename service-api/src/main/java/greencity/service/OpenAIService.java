package greencity.service;

import greencity.dto.language.LanguageDTO;
import greencity.exception.exceptions.OpenAIRequestException;

/**
 * Interface for interacting with the OpenAI API. The purpose of this interface
 * is to send requests to the OpenAI service and receive responses.
 */
public interface OpenAIService {
    /**
     * Sends a request to the OpenAI API and returns the response as a string.
     *
     * @param request The request as a string to be sent to the OpenAI API.
     * @throws OpenAIRequestException If the request is invalid or the OpenAI
     *                                service is unavailable.
     * @return The response from the service as a string.
     */
    String makeRequest(LanguageDTO language, String request);
}
