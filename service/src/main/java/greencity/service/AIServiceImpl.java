package greencity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static greencity.constant.OpenAIRequest.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import greencity.dto.habit.DurationHabitDto;
import static greencity.constant.OpenAIConstants.*;
import greencity.dto.habit.ShortHabitDto;
import greencity.dto.language.LanguageDTO;
import greencity.entity.*;
import greencity.entity.Language;
import greencity.enums.*;
import greencity.exception.exceptions.*;
import greencity.repository.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class AIServiceImpl implements AIService {
    private final OpenAIService openAIService;
    private final EcoNewsRepo ecoNewsRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final TagsRepo tagsRepo;
    private final UserRepo userRepo;
    private final HabitRepo habitRepo;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final LanguageService languageService;

    @Override
    public String getForecast(Long userId, String language) {
        List<HabitAssign> habitAssigns = habitAssignRepo.findAllByUserId(userId);
        if (habitAssigns.isEmpty()) {
            return getAdvice(userId, language);
        }
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
            .map(habitAssign -> modelMapper.map(habitAssign, DurationHabitDto.class)).toList();
        LanguageDTO languageDTO = languageService.findByCode(language);
        String forecastResponse = openAIService.makeRequest(languageDTO, FORECAST.formatted(durationHabitDtos));
        return sanitizeJsonResponse(forecastResponse);
    }

    @Override
    public String getAdvice(Long userId, String language) {
        Habit habit = habitRepo.findRandomHabit();
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        LanguageDTO languageDTO = languageService.findByCode(language);
        String forecastResponse = openAIService.makeRequest(languageDTO, ADVICE.formatted(shortHabitDto));
        return sanitizeJsonResponse(forecastResponse);
    }

    /**
     * Fetches news content based on a language and query.
     *
     * @param language the language in which the news should be fetched.
     * @param query    the query to filter news content.
     * @return a string containing the news content.
     */
    @Override
    public String getNews(String language, String query) {
        return makeRequestWithJsonAnswer(language, query);
    }

    /**
     * Generates AI-based eco news content, performs grammar correction,
     * and saves the final result to the database.
     * <p>
     * The method performs the following:
     * <ul>
     *   <li>Validates input language</li>
     *   <li>Checks if a week has passed since the last generation (rate limiting)</li>
     *   <li>Fetches AI-generated eco news without a specific query</li>
     *   <li>Fetches AI-generated eco news without a specific query</li>
     *   <li>Performs grammar correction on the generated text</li>
     * </ul>
     *
     * @param language the language code (e.g., "en", "uk") for content generation
     */
    @Override
    public void generateAndSaveEcoNews(String language) {
        LanguageDTO languageDTO = languageService.findByCode(language);
        String jsonResponse = openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse);

        ecoNewsRepo.save(ecoNews);
    }
    /**
     * Creates a request string for fetching news based on language and query.
     *
     * @param query    the query to filter news content.
     * @return a formatted request string.
     */
    private String createNewsRequest(String query) {
        String baseRequest = query == null
                ? NEWS_WITHOUT_QUERY
                : NEWS_BY_QUERY.formatted(query);

        return String.join(" ", baseRequest, MESSAGE_JSON_VALIDATION_HINT);
    }

    /**
     * Makes a request to the OpenAI API to fetch news content based on the provided language and query,
     * and returns the parsed content from the JSON response.
     * <p>
     * The method attempts to fetch the news content multiple times if necessary and handles exceptions
     * related to JSON parsing and API response issues.
     *
     * @param language the language in which the news should be fetched.
     * @param query    the query to filter news content.
     * @return the parsed news content as a string.
     * @throws JsonResponseParseException if the JSON response cannot be parsed or the content key is missing.
     * @throws OpenAIRequestException if the API request fails due to server issues or validation errors.
     */
    private String makeRequestWithJsonAnswer(String language, String query) {
        LanguageDTO languageDTO = languageService.findByCode(language);

        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                String response = openAIService.makeRequest(languageDTO, createNewsRequest(query));
                String jsonResponse = sanitizeJsonResponse(response);
                return parseContentFromJson(jsonResponse);
            } catch (JsonResponseParseException e) {
                log.error(ERROR_JSON_PARSE_FAILURE, e.getMessage());
                log.error(MESSAGE_CURRENT_ATTEMPT, i);
            } catch (OpenAIRequestException e) {
                if (e.getMessage().equals(ERROR_NO_OPENAI_RESPONSE)) {
                    throw e;
                }
            }
        }

        log.error(ERROR_MAX_ATTEMPTS_REACHED);
        throw new JsonResponseParseException(ERROR_MAX_ATTEMPTS_REACHED);
    }

    /**
     * Sanitizes a raw JSON response string by removing or replacing unwanted formatting characters.
     *
     * @param jsonResponse the raw response to sanitize.
     * @return cleaned and standardized JSON string.
     */
    private String sanitizeJsonResponse(String jsonResponse) {
        jsonResponse = jsonResponse.trim()
                .replaceAll(FORMAT_BOLD_PATTERN, TEXT_FORMAT_REPLACEMENT)
                .replaceAll(FORMAT_ITALIC_PATTERN, TEXT_FORMAT_REPLACEMENT)
                .replaceAll(FORMAT_JSON_BLOCK_PATTERN, EMPTY_REPLACEMENT)
                .replaceAll(FORMAT_CODE_BLOCK_PATTERN, EMPTY_REPLACEMENT)
                .replaceAll(FORMAT_QUOTES_PATTERN, QUOTES_REPLACEMENT);
        return jsonResponse;
    }

    /**
     * Parses a sanitized JSON string to extract the "content" field.
     *
     * @param jsonResponse sanitized JSON string.
     * @return extracted content string.
     * @throws JsonResponseParseException if parsing fails or content key is missing.
     */
    private String parseContentFromJson(String jsonResponse) {
        try {
            jsonResponse = jsonResponse.trim();
            if (!jsonResponse.startsWith(OPENING_CURLY_BRACE) ||
                    !jsonResponse.endsWith(CLOSING_CURLY_BRACE))
            {
                jsonResponse = OPENING_CURLY_BRACE + jsonResponse + CLOSING_CURLY_BRACE;
            }
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            boolean hasTitle = jsonNode.path(FORMAT_TITLE_KEY).isTextual();
            boolean hasContent = jsonNode.path(RESPONSE_JSON_CONTENT_KEY).isTextual();

            if (!hasTitle || !hasContent) {
                throw new JsonResponseParseException(ERROR_JSON_INVALID_FORMAT);
            }

            if (jsonNode.has(RESPONSE_JSON_CONTENT_KEY)) {
                return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
            } else {
                throw new JsonResponseParseException(ERROR_JSON_KEY_NOT_FOUND);
            }
        } catch (JsonParseException | JsonProcessingException e) {
            throw new JsonResponseParseException(ERROR_JSON_INVALID_FORMAT, e);
        }
    }

    /**
     * Constructs an {@link EcoNews} instance from a raw JSON response.
     *
     * @param jsonResponse the raw JSON response string.
     * @return a new {@link EcoNews} object populated with parsed content and metadata.
     */
    private EcoNews createEcoNewsInstance(String jsonResponse) {
        JsonNode jsonNode = parseJsonResponse(jsonResponse);
        String title = jsonNode.get(FORMAT_TITLE_KEY).asText();
        String content = jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
        User aiGeneratedUser = userRepo.findByEmail(AI_USER_EMAIL)
                .orElseGet(this::createAiGeneratedUser);
        List<Tag> tags = tagsRepo.findTagsByType(TagType.ECO_NEWS);

        if (tags.isEmpty()) {
            throw new EcoNewsCreationException(ERROR_NO_TAGS_FOUND);
        }

        Tag tag = tags.getFirst();
        return buildEcoNews(title, content, aiGeneratedUser, tag);
    }
    /**
     * Parses a JSON response string into a {@link JsonNode}.
     *
     * @param jsonResponse the JSON response string to parse.
     * @return a {@link JsonNode} representing the parsed JSON data.
     * @throws EcoNewsCreationException if the JSON format is invalid.
     */
    private JsonNode parseJsonResponse(String jsonResponse) {
        String sanitizedResponse = jsonResponse.replace(FORMAT_ASTERISKS_ESCAPE,
                FORMAT_EMPTY_STRING).trim();

        if (sanitizedResponse.startsWith(FORMAT_TITLE_PREFIX)) {
            return getJsonNodes(sanitizedResponse);
        } else {
            return parseJsonString(sanitizedResponse);
        }
    }
    /**
     * Creates a new AI-generated user and saves it in the repository.
     *
     * @return newly created {@link User} instance.
     */
    private User createAiGeneratedUser() {
        User user = User.builder()
                .name(AI_USER_NAME)
                .dateOfRegistration(LocalDateTime.now())
                .email(AI_USER_EMAIL)
                .role(Role.ROLE_USER)
                .refreshTokenKey(AI_MOCKED_REFRESH_TOKEN)
                .language(modelMapper.map(languageService.findByCode("ua"), Language.class))
                .build();
        return userRepo.save(user);
    }
    /**
     * Constructs and returns an {@link EcoNews} entity with provided metadata.
     *
     * @param title   the news title.
     * @param content the news content.
     * @param aiGeneratedUser the AI-generated author user.
     * @param tag     the tag to assign to this news.
     * @return a populated {@link EcoNews} instance.
     */
    private EcoNews buildEcoNews(String title,
                                 String content,
                                 User aiGeneratedUser,
                                 Tag tag)
    {
        return EcoNews.builder()
                .creationDate(ZonedDateTime.now())
                .author(aiGeneratedUser)
                .title(title)
                .text(content)
                .tags(List.of(tag))
                .build();
    }
    /**
     * Converts a sanitized response string into a JSON ObjectNode containing title and content fields.
     *
     * @param sanitizedResponse cleaned response string from an AI or other service.
     * @return an {@link ObjectNode} with extracted "title" and "content".
     */
    private static @NotNull ObjectNode getJsonNodes(String sanitizedResponse) {
        String[] parts = sanitizedResponse.split(FORMAT_NEW_LINE, 2);
        String title = parts[0].replace(FORMAT_TITLE_PREFIX, FORMAT_EMPTY_STRING).trim();
        String content = parts.length > 1 ? parts[1].trim() : FORMAT_EMPTY_STRING;

        return createJsonNode(title, content);
    }
    /**
     * Builds a JSON ObjectNode with provided title and content.
     *
     * @param title   the title string.
     * @param content the content string.
     * @return ObjectNode with the title and content keys populated.
     */
    private static ObjectNode createJsonNode(String title, String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(FORMAT_TITLE_KEY, title);
        jsonNode.put(RESPONSE_JSON_CONTENT_KEY, content);
        return jsonNode;
    }
    /**
     * Parses a sanitized JSON string into a {@link JsonNode}.
     *
     * @param sanitizedResponse the sanitized JSON string to parse.
     * @return a {@link JsonNode} representing the parsed JSON data.
     * @throws EcoNewsCreationException if the JSON format is invalid.
     */
    private JsonNode parseJsonString(String sanitizedResponse) {
        try {
            sanitizedResponse = sanitizedResponse
                    .replace(FORMAT_JSON_CODE_BLOCK_START, FORMAT_EMPTY_STRING)
                    .replace(FORMAT_JSON_CODE_BLOCK_END, FORMAT_EMPTY_STRING)
                    .replace(FORMAT_TITLE_PREFIX, FORMAT_EMPTY_STRING)
                    .replace(FORMAT_ASTERISKS_ESCAPE, FORMAT_EMPTY_STRING)
                    .replace(REGEX_ASTERISKS, FORMAT_EMPTY_STRING)
                    .replace(REGEX_MARKDOWN_ASTERISKS, FORMAT_EMPTY_STRING)
                    .replace(REGEX_MD_HEADERS, FORMAT_EMPTY_STRING)
                    .trim();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(sanitizedResponse);
        } catch (Exception e) {
            throw new EcoNewsCreationException(ERROR_JSON_INVALID_FORMAT + sanitizedResponse, e);
        }
    }
}
