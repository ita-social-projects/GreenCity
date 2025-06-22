package greencity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static greencity.constant.OpenAIRequest.*;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.UserEcoNewsRelevanceResponseDto;
import greencity.dto.habit.DurationHabitDto;
import greencity.entity.localization.TagTranslation;
import static greencity.constant.OpenAIConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.*;
import static greencity.enums.Role.ROLE_USER;
import static greencity.enums.TagType.ECO_NEWS;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final EcoNewsRepo ecoNewsRepo;
    private final OpenAIService openAIService;
    private final HabitAssignRepo habitAssignRepo;
    private final HabitRepo habitRepo;
    private final TagsRepo tagsRepo;
    private final UserRepo userRepo;
    private final GrammarChecker grammarChecker;
    private final UserEcoNewsRelevanceService userEcoNewsRelevanceService;
    private final ModelMapper modelMapper;

    /**
     * Fetches a forecast for a user based on their habits and language preference.
     *
     * @param userId   the ID of the user.
     * @param language the language in which the forecast should be generated.
     * @return a string containing the forecast.
     * @throws GrammarCheckException if grammar checking fails.
     */
    @Transactional
    @Override
    public String getForecast(Long userId, String language) {
        validateInputs(userId, language);

        List<HabitAssign> habitAssigns = habitAssignRepo.findAllByUserId(userId);
        String forecastResponse = habitAssigns.isEmpty()
                ? getAdvice(userId, language)
                : fetchForecast(language, habitAssigns);
        try {
            forecastResponse = grammarChecker.checkGrammar(forecastResponse);
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }

        return sanitizeJsonResponse(forecastResponse);
    }

    /**
     * Provides advice for a user based on a random habit and language preference.
     *
     * @param userId   the ID of the user.
     * @param language the language in which the advice should be generated.
     * @return a string containing the advice.
     * @throws GrammarCheckException if grammar checking fails.
     */
    @Override
    public String getAdvice(Long userId, String language) {
        validateInputs(userId, language);
        Habit habit = habitRepo.findRandomHabit();
        String adviceResponse = fetchAdvice(language, habit);

        try {
            adviceResponse = grammarChecker.checkGrammar(adviceResponse);
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        return adviceResponse;
    }

    /**
     * Fetches news content based on a language and query.
     *
     * @param language the language in which the news should be fetched.
     * @param query    the query to filter news content.
     * @return a string containing the news content.
     * @throws GrammarCheckException if grammar checking fails.
     */
    @Override
    public String getNews(String language, String query) {
        validateInputs(language);
        String jsonResponse = openAIService.makeRequest(language, createNewsRequest(query));
        String newResponse = extractContentFromJson(jsonResponse);

        try {
            newResponse = grammarChecker.checkGrammar(newResponse);
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        return newResponse;
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
     * If grammar correction fails, the eco news is not saved and a {@link GrammarCheckException} is thrown.
     *
     * @param language the language code (e.g., "en", "uk") for content generation
     * @throws EcoNewsGenerationLimitException if the generation limit (1 per week) is exceeded
     * @throws GrammarCheckException if grammar correction fails
     */
    @Override
    public void generateAndSaveEcoNews(String language) {
        validateInputs(language);
        if (!isWeekPassed()) {
            throw new EcoNewsGenerationLimitException(MESSAGE_ECO_NEWS_LIMIT);
        }

        String jsonResponse = openAIService.makeRequest(language, NEWS_WITHOUT_QUERY);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse);
        String ecoNewsText = ecoNews.getText();

        try {
            ecoNewsText = grammarChecker.checkGrammar(ecoNewsText);
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        ecoNews.setText(ecoNewsText);
        ecoNewsRepo.save(ecoNews);
    }
    /**
     * Generates basic eco news content and return it to user without saving in database.
     * <p>
     * The method performs the following:
     * <ul>
     *   <li>Validates input language</li>
     *   <li>Checks if a week has passed since the last generation (rate limiting)</li>
     *   <li>Fetches AI-generated eco news without a specific query</li>
     *   <li>Creates and stores an {@link EcoNews} entity in the database</li>
     *   <li>Performs grammar correction on the generated text</li>
     * </ul>
     * If grammar correction fails, it throws a {@link GrammarCheckException}.
     *
     * @param language the language code (e.g., "en", "uk") for content generation
     * @return grammatically corrected eco news text
     * @throws EcoNewsGenerationLimitException if the generation limit (1 per week) is exceeded
     * @throws GrammarCheckException if grammar correction fails
     */
    @Override
    public String generateEcoNewsByUserHabits(Long userId, String language) {
        validateInputs(userId, language);

        List<HabitAssign> habitAssigns = habitAssignRepo.findAllByUserId(userId);
        String forecastResponse;
        if (!habitAssigns.isEmpty()) {
            forecastResponse = openAIService.makeRequest(language, NEWS_BY_USER_HABITS.formatted(habitAssigns));
        } else {
            throw new UserHasNoHabitsException(ERROR_USER_HAS_NO_HABITS);
        }
        try {
            forecastResponse = grammarChecker.checkGrammar(forecastResponse);
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }

        return forecastResponse;
    }

    /**
     * Retrieves relevant eco news for a user based on their habits and filters.
     *
     * @param userId    the ID of the user.
     * @param language  the language in which the news should be fetched.
     * @param tags      the tags to filter news.
     * @param title     the title to filter news.
     * @param authorId  the ID of the author to filter news.
     * @param favorite  whether to filter by favorite news.
     * @return a list of {@link EcoNewsDto} containing relevant eco news.
     */
    @Override
    public List<EcoNewsDto> getRelevantEcoNewsForUser(
        Long userId, String language,
        List<String> tags, String title,
        Long authorId, boolean favorite
    ) {
        List<EcoNews> filteredNews = getFilteredEcoNews(tags, title, authorId);
        List<UserEcoNewsRelevanceResponseDto> precomputedRelevances = userEcoNewsRelevanceService.getRelevantNewsForUser(userId);
        List<String> userHabits = habitAssignRepo.fetchHabitNamesByUserId(userId);

        return enrichWithRelevance(filteredNews, precomputedRelevances, userHabits, language);
    }

    /**
     * Combines relevant and general eco news for a user and returns a paginated result.
     *
     * @param userId    the ID of the user.
     * @param language  the language in which the news should be fetched.
     * @param pageable  the pagination information.
     * @param tags      the tags to filter news.
     * @param title     the title to filter news.
     * @param authorId  the ID of the author to filter news.
     * @param favorite  whether to filter by favorite news.
     * @return a {@link Page} of {@link EcoNewsGenericDto} containing combined eco news.
     */
    @Override
    public Page<EcoNewsGenericDto> getCombinedEcoNewsForUser(
        Long userId, String language, Pageable pageable,
        List<String> tags, String title, Long authorId, boolean favorite
    ) {
        List<EcoNewsDto> combinedNews = (userId == null)
            ? getGeneralEcoNews(tags, title, authorId)
            : mergeRelevantAndGeneralNews(userId, language, tags, title, authorId, favorite);

        List<EcoNewsDto> sortedNews = sortNewsByRelevance(combinedNews);
        List<EcoNewsGenericDto> pagedContent = paginateAndConvert(sortedNews, pageable);

        return new PageImpl<>(pagedContent, pageable, sortedNews.size());
    }

    private List<EcoNews> getFilteredEcoNews(List<String> tags, String title, Long authorId) {
        return ecoNewsRepo.findAll().stream()
            .filter(news -> filterByTags(news, tags))
            .filter(news -> filterByTitle(news, title))
            .filter(news -> filterByAuthor(news, authorId))
            .toList();
    }

    private List<EcoNewsDto> enrichWithRelevance(List<EcoNews> ecoNewsList,
                                                 List<UserEcoNewsRelevanceResponseDto> precomputed,
                                                 List<String> userHabits,
                                                 String language) {
        return ecoNewsList.stream()
            .map(news -> {
                EcoNewsDto dto = modelMapper.map(news, EcoNewsDto.class);
                double score = getRelevanceScore(news, dto, precomputed, userHabits, language);
                dto.setRelevanceScore(score);
                return dto;
            })
            .sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();
    }

    private double getRelevanceScore(EcoNews news, EcoNewsDto dto,
                                     List<UserEcoNewsRelevanceResponseDto> precomputed,
                                     List<String> habits, String language) {
        return precomputed.stream()
            .filter(r -> r.getEcoNewsId().equals(news.getId()))
            .findFirst()
            .map(UserEcoNewsRelevanceResponseDto::getRelevance)
            .orElseGet(() -> userEcoNewsRelevanceService.calculateRelevanceScore(dto, habits, language));
    }

    private List<EcoNewsDto> sortNewsByRelevance(List<EcoNewsDto> newsList) {
        return newsList.stream()
            .sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();
    }

    private List<EcoNewsGenericDto> paginateAndConvert(List<EcoNewsDto> sortedNews, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedNews.size());

        return sortedNews.subList(start, end).stream()
                .map(news -> modelMapper.map(news, EcoNewsGenericDto.class))
                .toList();
    }

    private List<EcoNewsDto> mergeRelevantAndGeneralNews(
        Long userId, String language, List<String> tags,
        String title, Long authorId, boolean favorite
    ) {
        List<EcoNewsDto> relevantEcoNews = getRelevantEcoNewsForUser(userId, language, tags, title, authorId, favorite);
        List<EcoNewsDto> generalEcoNews = getGeneralEcoNews(tags, title, authorId);

        List<EcoNewsDto> combined = new ArrayList<>(relevantEcoNews);
        combined.addAll(generalEcoNews);
        return combined;
    }

    /**
     * Retrieves a list of general eco news based on optional filters: tags, title, and author ID.
     *
     * <p>This method fetches all eco news from the repository and applies filtering:</p>
     * <ul>
     *     <li>By tags, if provided</li>
     *     <li>By title, if provided</li>
     *     <li>By author ID, if provided</li>
     * </ul>
     * <p>Then maps the filtered {@link EcoNews} entities to {@link EcoNewsDto} objects using ModelMapper.</p>
     *
     * @param tags     list of tag names to filter by (nullable).
     * @param title    title or part of the title to match (nullable).
     * @param authorId ID of the author to filter by (nullable).
     * @return a list of {@link EcoNewsDto} matching the filter criteria.
     */
    private List<EcoNewsDto> getGeneralEcoNews(List<String> tags,
                                               String title,
                                               Long authorId)
    {
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
        ecoNewsList = ecoNewsList.stream()
            .filter(ecoNews -> filterByTags(ecoNews, tags))
            .filter(ecoNews -> filterByTitle(ecoNews, title))
            .filter(ecoNews -> filterByAuthor(ecoNews, authorId))
            .toList();
        return ecoNewsList.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .toList();
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
     * Fetches a forecast string based on language and habit assignments.
     *
     * @param language      the language in which the forecast should be generated.
     * @param habitAssigns  the list of habit assignments to include in the forecast.
     * @return a string containing the forecast.
     */
    private String fetchForecast(String language, List<HabitAssign> habitAssigns) {
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
                .map(habitAssign -> modelMapper.map(habitAssign, DurationHabitDto.class))
                .toList();
        return openAIService.makeRequest(language, FORECAST.formatted(durationHabitDtos));
    }

    /**
     * Fetches advice for a user based on language and a habit.
     *
     * @param language the language in which the advice should be generated.
     * @param habit    the habit to base the advice on.
     * @return a string containing the advice.
     */
    private String fetchAdvice(String language, Habit habit) {
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        return openAIService.makeRequest(language, ADVICE.formatted(shortHabitDto));
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
        List<Tag> tags = tagsRepo.findTagsByType(ECO_NEWS);

        if (tags.isEmpty()) {
            throw new EcoNewsCreationException(ERROR_NO_TAGS_FOUND);
        }

        Tag tag = tags.getFirst();
        return buildEcoNews(title, content, aiGeneratedUser, tag);
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
     * Creates a new AI-generated user and saves it in the repository.
     *
     * @return newly created {@link User} instance.
     */
    private User createAiGeneratedUser() {
        User user = User.builder()
            .name(AI_USER_NAME)
            .dateOfRegistration(LocalDateTime.now())
            .email(AI_USER_EMAIL)
            .role(ROLE_USER)
            .refreshTokenKey(AI_MOCKED_REFRESH_TOKEN)
            .build();
        return userRepo.save(user);
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
     * Validates if a given JSON string contains both required "title" and "content" fields.
     *
     * @param jsonResponse the JSON response string to check.
     * @return true if both keys are present and contain textual values, false otherwise.
     * @throws InvalidJsonFormatException if JSON parsing fails.
     */
    private boolean isJsonResponseComplete(String jsonResponse) {
        try {
            jsonResponse = jsonResponse.trim();
            if (!jsonResponse.startsWith(OPENING_CURLY_BRACE) ||
                !jsonResponse.endsWith(CLOSING_CURLY_BRACE))
            {
                jsonResponse = OPENING_CURLY_BRACE + jsonResponse + CLOSING_CURLY_BRACE;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            boolean hasTitle = jsonNode.path(FORMAT_TITLE_KEY).isTextual();
            boolean hasContent = jsonNode.path(RESPONSE_JSON_CONTENT_KEY).isTextual();

            return hasTitle && hasContent;
        } catch (JsonParseException e) {
            throw new InvalidJsonFormatException(ERROR_JSON_INVALID_FORMAT, e);
        } catch (Exception e) {
            throw new InvalidJsonFormatException(ERROR_JSON_VALIDATION_FAILURE, e);
        }
    }

    /**
     * Attempts to extract content from a JSON response string with retries if the response is incomplete.
     *
     * @param jsonResponse the raw JSON string to parse.
     * @return the "content" value if parsing is successful.
     * @throws JsonResponseParseException if maximum retry attempts are exceeded or parsing fails.
     */
    private String extractContentFromJson(String jsonResponse) {
        for (int retryCount = 0; retryCount < MAX_JSON_PARSE_ATTEMPTS; retryCount++) {
            try {
                jsonResponse = sanitizeJsonResponse(jsonResponse);

                if (!isJsonResponseComplete(jsonResponse)) {
                    if (retryCount == MAX_JSON_PARSE_ATTEMPTS - 1) {
                        throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS
                            + MAX_JSON_PARSE_ATTEMPTS + FORMAT_ATTEMPTS_SUFFIX);
                    }
                    continue;
                }
                return parseContentFromJson(jsonResponse);
            } catch (IncompleteJsonException e) {
                if (retryCount == MAX_JSON_PARSE_ATTEMPTS - 1) {
                    throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS
                        + MAX_JSON_PARSE_ATTEMPTS + FORMAT_ATTEMPTS_SUFFIX, e);
                }
            } catch (Exception e) {
                throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS, e);
            }
        }
        throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS);
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
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            if (jsonNode.has(RESPONSE_JSON_CONTENT_KEY)) {
                return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
            } else {
                throw new JsonResponseParseException(ERROR_JSON_KEY_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new JsonResponseParseException(ERROR_JSON_PARSE_FAILURE, e);
        }
    }

    /**
     * Checks if a week has passed since the last generated date.
     *
     * @return true if a week has passed, false otherwise.
     */
    private boolean isWeekPassed() {
        Optional<EcoNews> latestEcoNews =
            ecoNewsRepo.findTopByAuthorEmailOrderByCreationDateDesc("ai.generated@example.com");
        return latestEcoNews
            .map(news -> news.getCreationDate().toLocalDate().
                isBefore(LocalDate.now().minusWeeks(1)))
            .orElse(true);
    }

    /**
     * Filters eco news by tags.
     *
     * @param ecoNews the eco news to filter.
     * @param tags    the tags to filter by.
     * @return true if the eco news matches the tags, false otherwise.
     */
    private boolean filterByTags(EcoNews ecoNews, List<String> tags) {
        return tags == null ||
            tags.isEmpty() ||
            ecoNews.getTags()
                .stream()
                .flatMap(tag -> tag.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .anyMatch(tags::contains);
    }

    /**
     * Filters eco news by title.
     *
     * @param ecoNews the eco news to filter.
     * @param title   the title to filter by.
     * @return true if the eco news matches the title, false otherwise.
     */
    private boolean filterByTitle(EcoNews ecoNews, String title) {
        return title == null ||
            title.isEmpty() ||
            ecoNews.getTitle()
                .toLowerCase()
                .contains(title.toLowerCase());
    }

    /**
     * Filters eco news by author ID.
     *
     * @param ecoNews  the eco news to filter.
     * @param authorId the author ID to filter by.
     * @return true if the eco news matches the author ID, false otherwise.
     */
    private boolean filterByAuthor(EcoNews ecoNews, Long authorId) {
        return authorId == null ||
            ecoNews.getAuthor() == null ||
            ecoNews.getAuthor()
                .getId().equals(authorId);
    }

    /**
     * Validates the input parameters to ensure they are not null or invalid.
     *
     * @param inputs the input parameters to validate.
     * @throws InvalidInputException if any input is null, empty, or invalid.
     */
    private void validateInputs(Object... inputs) {
        for (Object input : inputs) {
            switch (input) {
                case null -> throw new InvalidInputException(ERROR_INPUT_CANNOT_BE_NULL);
                case String s -> {
                    if (s.isBlank()) {
                        throw new InvalidInputException(ERROR_STRING_CANNOT_BE_EMPTY);
                    }
                }
                case Long l -> {
                    if (l <= 0) {
                        throw new InvalidInputException(ERROR_LONG_VALUE_MUST_BE_POSITIVE);
                    }
                }
                default -> throw new InvalidInputException(ERROR_UNSUPPORTED_INPUT_TYPE + input.getClass().getName());
            }
        }
    }
}