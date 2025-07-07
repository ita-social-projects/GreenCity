package greencity.service;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.enums.OpenAIResponseFormat;
import greencity.exception.exceptions.OpenAIRequestException;

/**
 * Interface for interacting with the OpenAI API. The purpose of this interface
 * is to send requests to the OpenAI service and receive responses.
 */
public interface OpenAIService {
    /**
     * Makes a request to the OpenAI API using the provided language and request.
     *
     * @param language     the language settings for the request
     * @param request      the prompt to send to the OpenAI API
     * @param responseType the format of the response
     * @return The response from the service as a string.
     * @throws OpenAIRequestException If the request is invalid or the OpenAI
     *                                service is unavailable.
     */
    OpenAIResponseDTO makeRequest(LanguageDTO language, String request, OpenAIResponseFormat responseType);
    OpenAIResponseDTO makeRequestEmbedding(String title);
}
