package greencity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static greencity.constant.OpenAIRequest.*;
import greencity.dto.econews.EcoNewsGenericDto;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    private final ModelMapper modelMapper;
    private LocalDate lastGeneratedDate = LocalDate.now().minusWeeks(1);

    @Transactional
    @Override
    public String getForecast(Long userId, String language) {
        validateInputs(userId, language);

        List<HabitAssign> habitAssigns = fetchHabitAssignsByUserId(userId);
        String forecastResponse;

        if (habitAssigns.isEmpty()) {
            forecastResponse = getAdvice(userId, language);
        } else {
            forecastResponse = fetchForecast(language, habitAssigns);
        }

        try {
            forecastResponse = String.valueOf(grammarChecker.checkGrammar(forecastResponse));
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }

        return sanitizeJsonResponse(forecastResponse);
    }

    @Override
    public String getAdvice(Long userId, String language) {
        validateInputs(userId, language);
        Habit habit = fetchRandomHabit();
        String adviceResponse = fetchAdvice(language, habit);

        try {
            adviceResponse = String.valueOf(grammarChecker.checkGrammar(adviceResponse));
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        return adviceResponse;
    }

    @Override
    public String getNews(String language, String query) {
        validateInputs(language);
        String jsonResponse = openAIService.makeRequest(createNewsRequest(language, query));
        String newResponse = extractContentFromJson(jsonResponse);

        try {
            newResponse = String.valueOf(grammarChecker.checkGrammar(newResponse));
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        return newResponse;
    }

    @Override
    public String generateEcoNewsBasedOnHabits(String language) {
        validateInputs(language);
        if (!isWeekPassed()) {
            throw new EcoNewsGenerationLimitException(MESSAGE_ECO_NEWS_LIMIT);
        }
        lastGeneratedDate = LocalDate.now();

        String jsonResponse = fetchNewsWithoutQuery(language);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse);
        ecoNews = ecoNewsRepo.save(ecoNews);
        String ecoNewsText = ecoNews.getText();

        try {
            ecoNewsText = String.valueOf(grammarChecker.checkGrammar(ecoNewsText));
        } catch (IOException e) {
            throw new GrammarCheckException(ERROR_GRAMMAR_CHECK_FAILURE, e);
        }
        return ecoNewsText;
    }

    @Override
    public List<EcoNewsDto> getRelevantEcoNewsForUser(Long userId, String language,
                                                      List<String> tags, String title,
                                                      Long authorId, boolean favorite)
    {
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
        ecoNewsList = ecoNewsList.stream()
            .filter(ecoNews -> filterByTags(ecoNews, tags))
            .filter(ecoNews -> filterByTitle(ecoNews, title))
            .filter(ecoNews -> filterByAuthor(ecoNews, authorId))
            .toList();

        List<String> habitAssigns = habitAssignRepo.fetchHabitNamesByUserId(userId);

        return ecoNewsList.stream()
            .map(ecoNews -> {
                EcoNewsDto ecoNewsDto = modelMapper.map(ecoNews, EcoNewsDto.class);
                if (habitAssigns != null) {
                    double relevanceScore = calculateRelevanceScore(ecoNews, habitAssigns);
                    ecoNewsDto.setRelevanceScore(relevanceScore);
                }
                return ecoNewsDto;
            }).sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();
    }

    @Override
    public Page<EcoNewsGenericDto> getCombinedEcoNewsForUser(Long userId, String language,
                                                             Pageable pageable, List<String> tags,
                                                             String title, Long authorId,
                                                             boolean favorite)
    {
        List<EcoNewsDto> combinedNews;
        if (userId == null) {
            combinedNews = getGeneralEcoNews(tags, title, authorId);
        } else {
            List<EcoNewsDto> relevantEcoNews = getRelevantEcoNewsForUser(userId, language,
                tags, title,
                authorId, favorite);
            List<EcoNewsDto> generalEcoNews = getGeneralEcoNews(tags, title, authorId);

            combinedNews = new ArrayList<>(relevantEcoNews);
            combinedNews.addAll(generalEcoNews);
        }

        List<EcoNewsDto> sortedCombinedNews = combinedNews.stream()
            .sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedCombinedNews.size());
        List<EcoNewsGenericDto> pageContent = sortedCombinedNews.subList(start, end)
            .stream()
            .map(this::convertToGenericDto)
            .toList();
        return new PageImpl<>(pageContent, pageable, sortedCombinedNews.size());
    }

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


    private double calculateRelevanceScore(EcoNews ecoNews, List<String> habitNames) {
        double maxRelevance = 0.0;
        for (String habitName : habitNames) {
            double relevance = analyzeRelevance(ecoNews.getTitle(), habitName);
            if (relevance > maxRelevance) {
                maxRelevance = relevance;
            }
        }
        return maxRelevance;
    }

    private double analyzeRelevance(String topic1, String topic2) {
        String prompt = String.format(OPENAI_SIMILARITY_PROMPT, topic1, topic2);
        String response = openAIService.makeRequest(prompt);

        try {
            double score = Double.parseDouble(response.trim());
            if (score < 0 || score > 1) {
                throw new OpenAIRelevanceException(ERROR_INVALID_RELEVANCE_SCORE + score);
            }
            return score;
        } catch (NumberFormatException e) {
            throw new OpenAIRelevanceException(ERROR_RELEVANCE_SCORE_PARSE_FAILURE + response, e);
        }
    }

    private List<HabitAssign> fetchHabitAssignsByUserId(Long userId) {
        return habitAssignRepo.findAllByUserId(userId);
    }

    private Habit fetchRandomHabit() {
        return habitRepo.findRandomHabit();
    }

    private String createNewsRequest(String language, String query) {
        String baseRequest = query == null
            ? NEWS_WITHOUT_QUERY
            : NEWS_BY_QUERY + query;

        return language + baseRequest + MESSAGE_JSON_VALIDATION_HINT
            + REQUEST_MAX_TOKENS_KEY + MAX_ALLOWED_TOKENS;
    }

    private String fetchForecast(String language, List<HabitAssign> habitAssigns) {
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
            .map(this::mapToDurationHabitDto)
            .toList();
        return openAIService.makeRequest(language + FORECAST + durationHabitDtos);
    }

    private DurationHabitDto mapToDurationHabitDto(HabitAssign habitAssign) {
        return modelMapper.map(habitAssign, DurationHabitDto.class);
    }

    private EcoNewsGenericDto convertToGenericDto(EcoNewsDto ecoNewsDto) {
        return modelMapper.map(ecoNewsDto, EcoNewsGenericDto.class);
    }

    private String fetchAdvice(String language, Habit habit) {
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        return openAIService.makeRequest(language + ADVICE + shortHabitDto);
    }

    private String fetchNewsWithoutQuery(String language) {
        return openAIService.makeRequest(language + NEWS_WITHOUT_QUERY);
    }

    private JsonNode parseJsonResponse(String jsonResponse) {
        String sanitizedResponse = jsonResponse.replace(FORMAT_ASTERISKS_ESCAPE,
            FORMAT_EMPTY_STRING).trim();

        if (sanitizedResponse.startsWith(FORMAT_TITLE_PREFIX)) {
            return getJsonNodes(sanitizedResponse);
        } else {
            return parseJsonString(sanitizedResponse);
        }
    }

    private JsonNode parseJsonString(String sanitizedResponse) {
        try {
            sanitizedResponse = sanitizedResponse
                .replace(FORMAT_JSON_CODE_BLOCK_START, FORMAT_EMPTY_STRING)
                .replace(FORMAT_JSON_CODE_BLOCK_END, FORMAT_EMPTY_STRING)
                .replace("Title: ", "")
                .replace("\\*\\*", "")
                .replace("\\*", "")
                .replaceAll("\\*+", "")
                .trim();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(sanitizedResponse);
        } catch (Exception e) {
            throw new EcoNewsCreationException(ERROR_JSON_INVALID_FORMAT + sanitizedResponse, e);
        }
    }

    private static @NotNull ObjectNode getJsonNodes(String sanitizedResponse) {
        String[] parts = splitResponse(sanitizedResponse);
        String title = extractTitle(parts);
        String content = extractContent(parts);

        return createJsonNode(title, content);
    }

    private static ObjectNode createJsonNode(String title, String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(FORMAT_TITLE_KEY, title);
        jsonNode.put(RESPONSE_JSON_CONTENT_KEY, content);
        return jsonNode;
    }

    private static String[] splitResponse(String response) {
        return response.split(FORMAT_NEW_LINE, 2);
    }

    private static String extractTitle(String[] parts) {
        return parts[0].replace(FORMAT_TITLE_PREFIX, FORMAT_EMPTY_STRING).trim();
    }

    private static String extractContent(String[] parts) {
        return parts.length > 1 ? parts[1].trim() : FORMAT_EMPTY_STRING;
    }

    private EcoNews createEcoNewsInstance(String jsonResponse) {
        JsonNode jsonNode = parseJsonResponse(jsonResponse);
        String title = extractTitleFromJson(jsonNode);
        String content = extractContentFromJsonNode(jsonNode);
        User aiGeneratedUser = fetchOrCreateAiGeneratedUser();
        List<Tag> tags = tagsRepo.findTagsByType(ECO_NEWS);

        if (tags.isEmpty()) {
            throw new EcoNewsCreationException(ERROR_NO_TAGS_FOUND);
        }

        Tag tag = tags.getFirst();
        return buildEcoNews(title, content, aiGeneratedUser, tag);
    }

    private User fetchOrCreateAiGeneratedUser() {
        return userRepo.findByEmail(AI_USER_EMAIL)
            .orElseGet(this::createAiGeneratedUser);
    }

    private String extractTitleFromJson(JsonNode jsonNode) {
        return jsonNode.get(FORMAT_TITLE_KEY).asText();
    }

    private String extractContentFromJsonNode(JsonNode jsonNode) {
        return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
    }

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

    private String sanitizeJsonResponse(String jsonResponse) {
        jsonResponse = jsonResponse.trim()
            .replaceAll(FORMAT_BOLD_PATTERN, TEXT_FORMAT_REPLACEMENT)
            .replaceAll(FORMAT_ITALIC_PATTERN, TEXT_FORMAT_REPLACEMENT)
            .replaceAll(FORMAT_JSON_BLOCK_PATTERN, EMPTY_REPLACEMENT)
            .replaceAll(FORMAT_CODE_BLOCK_PATTERN, EMPTY_REPLACEMENT)
            .replaceAll(FORMAT_QUOTES_PATTERN, QUOTES_REPLACEMENT);
        return jsonResponse;
    }

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

    private String extractContentFromJson(String jsonResponse) {
        int retryCount = 0;

        while (retryCount < MAX_JSON_PARSE_ATTEMPTS) {
            try {
                jsonResponse = sanitizeJsonResponse(jsonResponse);

                if (!isJsonResponseComplete(jsonResponse)) {
                    retryCount++;
                    if (retryCount >= MAX_JSON_PARSE_ATTEMPTS) {
                        throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS
                            + MAX_JSON_PARSE_ATTEMPTS + FORMAT_ATTEMPTS_SUFFIX);
                    }
                    continue;
                }
                return parseContentFromJson(jsonResponse);
            } catch (IncompleteJsonException e) {
                retryCount++;
                if (retryCount >= MAX_JSON_PARSE_ATTEMPTS) {
                    throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS
                        + MAX_JSON_PARSE_ATTEMPTS + FORMAT_ATTEMPTS_SUFFIX, e);
                }
            } catch (Exception e) {
                throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS, e);
            }
        }
        throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS);
    }

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

    private boolean isWeekPassed() {
        return ChronoUnit.WEEKS.between(lastGeneratedDate, LocalDate.now()) >= 1;
    }

    private boolean filterByTags(EcoNews ecoNews, List<String> tags) {
        return tags == null ||
            tags.isEmpty() ||
            ecoNews.getTags()
                .stream()
                .flatMap(tag -> tag.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .anyMatch(tags::contains);
    }

    private boolean filterByTitle(EcoNews ecoNews, String title) {
        return title == null ||
            title.isEmpty() ||
            ecoNews.getTitle()
                .toLowerCase()
                .contains(title.toLowerCase());
    }

    private boolean filterByAuthor(EcoNews ecoNews, Long authorId) {
        return authorId == null ||
            ecoNews.getAuthor() == null ||
            ecoNews.getAuthor()
                .getId().equals(authorId);
    }

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