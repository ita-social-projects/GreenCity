package greencity.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import greencity.converters.FloatArrayConverter;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.openai.OpenAIResponseDTO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.OpenAIResponseFormat;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.EcoNewsCreationException;
import greencity.exception.exceptions.JsonResponseParseException;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.OpenAIRequestException;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static greencity.constant.ErrorMessage.ECO_NEW_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.HABIT_NOT_FOUND;
import static greencity.constant.OpenAIRequest.*;
import static greencity.constant.OpenAIConstants.*;

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
    private final FloatArrayConverter floatArrayConverter;
    private final LanguageService languageService;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    /**
     * Generates a personalized ecological habit forecast for a given user and
     * language.
     *
     * <p>
     * If the user has any assigned habits, each {@link HabitAssign} is mapped to a
     * {@link DurationHabitDto}, and a forecast prompt is created and sent to the
     * OpenAI service. The response from OpenAI is sanitized to remove any markdown
     * or formatting artifacts before returning.
     * </p>
     *
     * <p>
     * If the user has no assigned habits, this method falls back to
     * {@link #getAdvice(Long, String)}, which generates general ecological advice
     * based on a randomly selected habit.
     * </p>
     *
     * @param userId   the ID of the user for whom the forecast is generated
     * @param language the language code (e.g. "en", "ua") in which the advice
     *                 should be generated
     * @return a sanitized string response containing either a personalized forecast
     *         or fallback advice
     * @throws OpenAIRequestException    if the OpenAI API call fails (e.g. no
     *                                   response or internal error)
     * @throws LanguageNotFoundException if the specified language code is not found
     *                                   in the system
     * @throws NullPointerException      or MappingException if required mapping
     *                                   data is missing
     */
    @Override
    public String getForecast(Long userId, String language) {
        List<HabitAssign> habitAssigns = habitAssignRepo.findAllByUserId(userId);
        if (habitAssigns.isEmpty()) {
            return getAdvice(userId, language);
        }
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
            .map(habitAssign -> modelMapper.map(habitAssign, DurationHabitDto.class)).toList();
        LanguageDTO languageDTO = languageService.findByCode(language);
        OpenAIResponseDTO forecastResponse = openAIService.makeRequest(languageDTO,
            FORECAST.formatted(durationHabitDtos),
            OpenAIResponseFormat.TEXT);
        return sanitizeJsonResponse(forecastResponse.getContent());
    }

    /**
     * Generates general ecological advice for a user in the specified language.
     *
     * <p>
     * This method randomly selects a habit from the system, maps it to a
     * {@link ShortHabitDto}, formats a request using a predefined advice prompt,
     * and sends it to the OpenAI service to generate human-like advice content. The
     * response is then sanitized to remove any unnecessary formatting or markdown
     * artifacts before returning.
     * </p>
     *
     * <p>
     * This method is typically used as a fallback when the user has no assigned
     * habits, or when personalized forecasting is not possible.
     * </p>
     *
     * @param userId   the ID of the user requesting advice (not used internally but
     *                 part of the method signature for consistency)
     * @param language the language code (e.g. "en", "ua") in which the advice
     *                 should be generated
     * @return a sanitized string containing the generated ecological advice
     * @throws OpenAIRequestException    if the OpenAI service fails to generate a
     *                                   response
     * @throws LanguageNotFoundException if the given language code is not supported
     * @throws NullPointerException      or MappingException if habit mapping fails
     *                                   or the habit is null
     */
    @Override
    public String getAdvice(Long userId, String language) {
        Habit habit = habitRepo.findRandomHabit();
        if (habit == null) {
            log.error(HABIT_NOT_FOUND);
            throw new NotFoundException(HABIT_NOT_FOUND);
        }
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        LanguageDTO languageDTO = languageService.findByCode(language);
        OpenAIResponseDTO forecastResponse = openAIService.makeRequest(languageDTO,
            ADVICE.formatted(shortHabitDto),
            OpenAIResponseFormat.TEXT);
        return sanitizeJsonResponse(forecastResponse.getContent());
    }

    /**
     * Fetches AI-generated eco-news content based on the specified language and
     * optional query.
     *
     * <p>
     * Constructs a prompt using the provided query (or a default template if the
     * query is {@code null}), sends it to the OpenAI service, and attempts to parse
     * the returned JSON response to extract the content field. The method includes
     * retry logic for failed or malformed responses and sanitizes the response
     * before parsing.
     * </p>
     *
     * @param language the language code (e.g. "en", "ua") in which the news content
     *                 should be generated
     * @param query    an optional keyword or phrase to guide news generation; if
     *                 {@code null}, a generic request is used
     * @return a string containing the parsed and cleaned news content
     * @throws JsonResponseParseException if the JSON content is invalid or cannot
     *                                    be parsed after all retries
     * @throws OpenAIRequestException     if OpenAI does not return a usable
     *                                    response
     * @throws LanguageNotFoundException  if the given language code does not exist
     *                                    in the system
     */
    @Override
    public String getNews(String language, String query) {
        return makeRequestWithJsonAnswer(language, query);
    }

    /**
     * Generates and saves AI-generated eco-news content in the specified language.
     *
     * <p>
     * Sends a predefined eco-news prompt (without any user query) to the OpenAI
     * service using the specified language. Parses the returned JSON response to
     * extract the title and content, then constructs a new {@link EcoNews} entity
     * using a system AI-generated user and the first available eco-news tag. The
     * generated news is then saved to the database via {@link EcoNewsRepo}.
     * </p>
     *
     * @param language the language code (e.g. "en", "ua") in which the eco-news
     *                 should be generated
     * @throws LanguageNotFoundException  if the specified language code is not
     *                                    recognized
     * @throws OpenAIRequestException     if the OpenAI service fails to respond
     *                                    with valid data
     * @throws EcoNewsCreationException   if required metadata (e.g. tags) is
     *                                    missing or the JSON format is invalid
     * @throws JsonResponseParseException if the OpenAI response cannot be parsed
     *                                    into valid JSON
     */
    @Transactional
    @Override
    public void generateAndSaveEcoNews(String language) {
        LanguageDTO languageDTO = languageService.findByCode(language);
        OpenAIResponseDTO jsonResponse = openAIService.makeRequest(languageDTO, NEWS_WITHOUT_QUERY,
            OpenAIResponseFormat.JSON_SCHEMA);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse.getContent());

        ecoNewsRepo.save(ecoNews);
    }

    /**
     * Calculates and stores the title relevance vector for the specified
     * {@link EcoNews} entity.
     *
     * <p>
     * Steps performed by this method:
     * <ul>
     * <li>Retrieves the {@link EcoNews} entity by the given ID.</li>
     * <li>Sends the news title to the OpenAI service to get an embedding
     * vector.</li>
     * <li>Creates an {@link EcoNewsRelevance} entity containing the title
     * vector.</li>
     * <li>Saves the relevance entity to the database via
     * {@code ecoNewsRelevanceRepo}.</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no news is found with the specified ID, a {@link NotFoundException} is
     * thrown.
     * </p>
     *
     * <p>
     * This method is transactional, ensuring atomicity of the operations.
     * </p>
     *
     * @param id the ID of the EcoNews entity for which to calculate title relevance
     * @throws NotFoundException if the EcoNews entity with the given ID does not
     *                           exist
     */
    @Transactional
    public void getRelevanceForEcoNews(Long id) {
        EcoNews ecoNews = ecoNewsRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ECO_NEW_NOT_FOUND_BY_ID));

        String title = ecoNews.getTitle();
        OpenAIResponseDTO response = openAIService.makeRequestEmbedding(title);

        EcoNewsRelevance relevance = EcoNewsRelevance.builder()
            .id(ecoNews.getId())
            .ecoNews(ecoNews)
            .titleVector(floatArrayConverter.convertToEntityAttribute(response.getContent()))
            .build();

        ecoNewsRelevanceRepo.save(relevance);
    }

    /**
     * Inserts new relevance vectors for a batch of EcoNews entries.
     * <p>
     * This method retrieves EcoNews entities by their IDs, generates embeddings for
     * their titles using the OpenAI service, converts the embeddings into float
     * arrays, and creates new {@link EcoNewsRelevance} entities. Each relevance
     * entry is marked as not outdated and persisted in the database.
     * </p>
     *
     * @param ids the collection of EcoNews IDs for which relevance should be
     *            created
     */
    @Transactional
    public void insertRelevanceBatch(Collection<Long> ids) {
        if (ids.isEmpty())
            return;

        List<EcoNews> ecoNewsList = ecoNewsRepo.findAllById(ids);
        List<String> titles = ecoNewsList.stream().map(EcoNews::getTitle).toList();

        List<OpenAIResponseDTO> embeddings = openAIService.makeRequestEmbeddings(titles);

        List<EcoNewsRelevance> relevanceList = new ArrayList<>();
        for (int i = 0; i < ecoNewsList.size(); i++) {
            EcoNews news = ecoNewsList.get(i);
            OpenAIResponseDTO response = embeddings.get(i);

            relevanceList.add(
                EcoNewsRelevance.builder()
                    .ecoNews(news)
                    .titleVector(floatArrayConverter.convertToEntityAttribute(response.getContent()))
                    .isOutdated(false)
                    .build());
        }

        ecoNewsRelevanceRepo.saveAll(relevanceList);
    }

    /**
     * Updates existing relevance vectors for a batch of EcoNews entries.
     * <p>
     * This method retrieves EcoNews entities by their IDs and their corresponding
     * {@link EcoNewsRelevance} entries. It then generates new embeddings for the
     * titles using the OpenAI service, updates the relevance vectors, and marks
     * each entry as not outdated. Finally, the updated relevance entries are
     * persisted in the database.
     * </p>
     *
     * @param ids the collection of EcoNews IDs for which relevance should be
     *            updated
     */
    @Transactional
    public void updateRelevanceBatch(Collection<Long> ids) {
        if (ids.isEmpty())
            return;

        List<EcoNews> ecoNewsList = ecoNewsRepo.findAllById(ids);
        List<String> titles = ecoNewsList.stream()
            .map(EcoNews::getTitle)
            .toList();

        List<OpenAIResponseDTO> embeddings = openAIService.makeRequestEmbeddings(titles);

        List<EcoNewsRelevance> relevanceList = ecoNewsRelevanceRepo.findAllById(ids);

        for (int i = 0; i < relevanceList.size(); i++) {
            relevanceList.get(i).setTitleVector(
                floatArrayConverter.convertToEntityAttribute(embeddings.get(i).getContent()));
            relevanceList.get(i).setIsOutdated(false);
        }

        ecoNewsRelevanceRepo.saveAll(relevanceList);
    }

    /**
     * Builds a news generation prompt for the OpenAI service based on an optional
     * user query.
     *
     * <p>
     * If the {@code query} is {@code null}, a default eco-news prompt is used.
     * Otherwise, a query-specific prompt is created using
     * {@link String#formatted(Object...)}. In both cases, a JSON validation hint is
     * appended to the end of the prompt to increase the likelihood of receiving
     * well-structured JSON from OpenAI.
     * </p>
     *
     * @param query an optional user-defined search phrase for generating eco-news;
     *              if {@code null}, a generic prompt will be used
     * @return the final prompt string to be sent to OpenAI
     */
    private String createNewsRequest(String query) {
        String baseRequest = query == null
            ? NEWS_WITHOUT_QUERY
            : NEWS_BY_QUERY.formatted(query);

        return String.join(" ", baseRequest, MESSAGE_JSON_VALIDATION_HINT);
    }

    /**
     * Sends a news generation request to the OpenAI service and extracts the
     * content field from the JSON response.
     *
     * <p>
     * Constructs a prompt based on the provided query (or a default one if
     * {@code query} is {@code null}), then sends the request using the specified
     * language. The response is sanitized and parsed for content. If parsing fails
     * or OpenAI does not respond correctly, the request is retried up to a fixed
     * number of attempts. If all attempts fail, a
     * {@link JsonResponseParseException} is thrown.
     * </p>
     *
     * @param language the language code (e.g. "en", "ua") for the news generation
     *                 request
     * @param query    an optional keyword or phrase to customize the news content;
     *                 if {@code null}, a generic prompt is used
     * @return the textual content extracted from the AI-generated JSON response
     * @throws JsonResponseParseException if the response is malformed or parsing
     *                                    fails after all retries
     * @throws OpenAIRequestException     if OpenAI fails to respond and retrying is
     *                                    not possible
     * @throws LanguageNotFoundException  if the language code is not recognized by
     *                                    {@code languageService}
     */
    private String makeRequestWithJsonAnswer(String language, String query) {
        LanguageDTO languageDTO = languageService.findByCode(language);

        for (int i = 1; i <= MAX_REQUEST_ATTEMPTS; i++) {
            try {
                OpenAIResponseDTO response = openAIService.makeRequest(languageDTO,
                    createNewsRequest(query),
                    OpenAIResponseFormat.JSON_SCHEMA);
                String jsonResponse = sanitizeJsonResponse(response.getContent());
                return parseContentFromJson(jsonResponse);
            } catch (JsonResponseParseException e) {
                log.error(ERROR_JSON_PARSE_FAILURE, e.getMessage());
                log.error(MESSAGE_CURRENT_ATTEMPT, i);
            } catch (OpenAIRequestException e) {
                log.error(OPEN_AI_REQUEST_FAILURE, e.getMessage());
                if (e.getMessage().equals(ERROR_NO_OPENAI_RESPONSE)) {
                    throw e;
                }
            }
        }

        log.error(ERROR_MAX_ATTEMPTS_REACHED);
        throw new JsonResponseParseException(ERROR_MAX_ATTEMPTS_REACHED);
    }

    /**
     * Sanitizes the raw JSON response from the OpenAI service by removing or
     * replacing unwanted formatting elements.
     *
     * <p>
     * This includes cleaning up markdown artifacts such as bold, italic, code
     * blocks, quoted formatting, and custom JSON formatting hints. The purpose is
     * to ensure that the JSON is clean and easier to parse reliably.
     * </p>
     *
     * @param jsonResponse the raw JSON string received from OpenAI
     * @return a sanitized version of the JSON string, free from formatting
     *         characters that might break parsing
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
     * Parses the sanitized JSON response and extracts the textual content
     * associated with a specific key.
     *
     * <p>
     * Ensures the response is properly wrapped in curly braces if necessary, then
     * attempts to parse it into a {@link JsonNode}. Validates the presence and type
     * of both the title and content fields. If the required fields are missing,
     * invalid, or the structure is malformed, a {@link JsonResponseParseException}
     * is thrown.
     * </p>
     *
     * @param jsonResponse the sanitized JSON string to be parsed
     * @return the textual content from the {@code "content"} field of the parsed
     *         JSON
     * @throws JsonResponseParseException if the JSON is malformed, missing required
     *                                    fields, or cannot be parsed into a valid
     *                                    structure
     */
    private String parseContentFromJson(String jsonResponse) {
        try {
            jsonResponse = jsonResponse.trim();
            if (!jsonResponse.startsWith(OPENING_CURLY_BRACE)
                || !jsonResponse.endsWith(CLOSING_CURLY_BRACE)) {
                jsonResponse = OPENING_CURLY_BRACE + jsonResponse + CLOSING_CURLY_BRACE;
            }
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            boolean hasTitle = jsonNode.path(FORMAT_TITLE_KEY).isTextual();
            boolean hasContent = jsonNode.path(RESPONSE_JSON_CONTENT_KEY).isTextual();

            if (!hasTitle || !hasContent) {
                log.error(ERROR_INVALID_TITLE_OR_CONTENT);
                throw new JsonResponseParseException(ERROR_INVALID_TITLE_OR_CONTENT);
            }

            if (jsonNode.has(RESPONSE_JSON_CONTENT_KEY)) {
                return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
            } else {
                log.error(ERROR_JSON_KEY_NOT_FOUND);
                throw new JsonResponseParseException(ERROR_JSON_KEY_NOT_FOUND);
            }
        } catch (JsonParseException | JsonProcessingException e) {
            log.error(ERROR_JSON_INVALID_FORMAT);
            throw new JsonResponseParseException(ERROR_JSON_INVALID_FORMAT, e);
        }
    }

    /**
     * Creates a new {@link EcoNews} entity instance from the given JSON response
     * string.
     *
     * <p>
     * Parses the JSON to extract the title and content fields. Retrieves or creates
     * a system AI-generated user, then fetches eco-news tags from the repository.
     * If no tags are found, throws an {@link EcoNewsCreationException}. Finally,
     * constructs and returns a new {@link EcoNews} object with the extracted data,
     * author, and tag.
     * </p>
     *
     * @param jsonResponse the raw JSON string containing eco-news data
     * @return a fully constructed {@link EcoNews} entity ready to be saved
     * @throws EcoNewsCreationException   if no eco-news tags are found or the JSON
     *                                    format is invalid
     * @throws JsonResponseParseException if parsing the JSON response fails
     */
    private EcoNews createEcoNewsInstance(String jsonResponse) {
        JsonNode jsonNode = parseJsonResponse(jsonResponse);
        String title = jsonNode.get(FORMAT_TITLE_KEY).asText();
        String content = jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
        User aiGeneratedUser = userRepo.findByEmail(AI_USER_EMAIL)
            .orElseGet(this::createAiGeneratedUser);
        List<Tag> tags = tagsRepo.findTagsByType(TagType.ECO_NEWS);

        if (tags.isEmpty()) {
            log.error(ERROR_NO_TAGS_FOUND);
            throw new EcoNewsCreationException(ERROR_NO_TAGS_FOUND);
        }

        Tag tag = tags.getFirst();
        return buildEcoNews(title, content, aiGeneratedUser, tag);
    }

    /**
     * Parses the given raw JSON response string into a {@link JsonNode}.
     *
     * <p>
     * First sanitizes the response by removing escaped asterisks and trimming
     * whitespace. Then, if the response starts with a title prefix, it processes it
     * as a formatted title-content block using {@link #getJsonNodes(String)};
     * otherwise, it parses the string as a regular JSON object via
     * {@link #parseJsonString(String)}.
     * </p>
     *
     * @param jsonResponse the raw JSON response string to parse
     * @return a {@link JsonNode} representing the parsed JSON structure
     * @throws EcoNewsCreationException if parsing the JSON fails or the format is
     *                                  invalid
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
     * Creates and persists a system user representing the AI-generated content
     * author.
     *
     * <p>
     * This user has predefined attributes such as a fixed name, email, role, and
     * language ("ua"). The user is saved in the repository and returned.
     * </p>
     *
     * @return the newly created and saved {@link User} entity representing the AI
     *         author
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
     * Constructs a new {@link EcoNews} entity with the specified title, content,
     * author, and tag.
     *
     * <p>
     * Sets the creation date to the current date and time.
     * </p>
     *
     * @param title           the title of the eco-news article
     * @param content         the body text of the eco-news article
     * @param aiGeneratedUser the author of the news, typically the AI-generated
     *                        user
     * @param tag             the tag categorizing the news item
     * @return a fully built {@link EcoNews} entity ready for persistence
     */
    private EcoNews buildEcoNews(String title,
        String content,
        User aiGeneratedUser,
        Tag tag) {
        return EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .author(aiGeneratedUser)
            .title(title)
            .text(content)
            .tags(List.of(tag))
            .build();
    }

    /**
     * Parses a sanitized response string into a JSON object node containing title
     * and content.
     *
     * <p>
     * Splits the response by the first newline. The first part is treated as the
     * title (after removing the title prefix), and the second part as the content.
     * Both are trimmed before being added to the JSON node.
     * </p>
     *
     * @param sanitizedResponse the sanitized string containing a title and content
     *                          separated by a newline
     * @return an {@link ObjectNode} with "title" and "content" fields populated
     */
    private @NotNull ObjectNode getJsonNodes(String sanitizedResponse) {
        String[] parts = sanitizedResponse.split(FORMAT_NEW_LINE, 2);
        String title = parts[0].replace(FORMAT_TITLE_PREFIX, FORMAT_EMPTY_STRING).trim();
        String content = parts.length > 1 ? parts[1].trim() : FORMAT_EMPTY_STRING;

        return createJsonNode(title, content);
    }

    /**
     * Creates a JSON object node with specified title and content fields.
     *
     * @param title   the title text to set in the JSON node
     * @param content the content text to set in the JSON node
     * @return an {@link ObjectNode} containing the title and content fields
     */
    private ObjectNode createJsonNode(String title, String content) {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(FORMAT_TITLE_KEY, title);
        jsonNode.put(RESPONSE_JSON_CONTENT_KEY, content);
        return jsonNode;
    }

    /**
     * Parses a sanitized JSON string into a {@link JsonNode}.
     *
     * <p>
     * Before parsing, removes various formatting artifacts such as JSON code block
     * markers, title prefixes, escaped asterisks, markdown asterisks, and markdown
     * headers to ensure clean JSON. If parsing fails, logs an error and throws an
     * {@link EcoNewsCreationException}.
     * </p>
     *
     * @param sanitizedResponse the JSON string cleaned of formatting artifacts
     * @return the parsed {@link JsonNode} representing the JSON structure
     * @throws EcoNewsCreationException if the JSON is malformed or cannot be parsed
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

            return objectMapper.readTree(sanitizedResponse);
        } catch (JsonProcessingException e) {
            log.error(ERROR_ECO_NEWS_CREATION_FAILED);
            throw new EcoNewsCreationException(ERROR_JSON_INVALID_FORMAT + sanitizedResponse, e);
        }
    }
}
