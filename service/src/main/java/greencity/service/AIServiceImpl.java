package greencity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static greencity.constant.OpenAIRequest.*;
import static greencity.log.OpenAILogMessages.*;
import static greencity.utils.OpenAIConstants.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.*;
import static greencity.enums.Role.ROLE_USER;
import static greencity.enums.TagType.ECO_NEWS;
import greencity.exception.exceptions.*;
import greencity.repository.*;
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
import org.springframework.stereotype.Service;
import java.util.List;

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
    private final ModelMapper modelMapper;
    private LocalDate lastGeneratedDate = LocalDate.now().minusWeeks(1);

    @Override
    public String getForecast(Long userId, String language) {
        log.info(FORECAST_REQUEST_INITIATED, userId, language);
        validateInputs(userId, language);

        List<HabitAssign> habitAssigns = fetchHabitAssignsByUserId(userId);

        if (habitAssigns.isEmpty()) {
            log.info(NO_HABIT_ASSIGNMENTS_DETECTED, userId);
            return getAdvice(userId, language);
        } else {
            log.info(HABIT_ASSIGNMENTS_RETRIEVED, userId);
            return fetchForecast(language, habitAssigns);
        }
    }

    @Override
    public String getAdvice(Long userId, String language) {
        log.info(ADVICE_REQUEST_INITIATED, userId, language);
        validateInputs(userId, language);
        Habit habit = fetchRandomHabit();
        log.info(RANDOM_HABIT_SELECTED, habit);
        return fetchAdvice(language, habit);
    }

    @Override
    public String getNews(String language, String query) {
        log.info(NEWS_REQUEST_INITIATED, language, query);
        validateInputs(language);
        String jsonResponse = openAIService.makeRequest(createNewsRequest(language, query));
        log.info(API_RESPONSE_RECEIVED, jsonResponse);
        return extractContentFromJson(jsonResponse);
    }

    @Override
    public String generateEcoNewsBasedOnHabits(String language) {
        log.info(ECO_NEWS_GENERATION_REQUEST, language);
        validateInputs(language);
        if (!isWeekPassed()) {
            log.warn(ECO_NEWS_GENERATION_LIMIT_EXCEEDED, lastGeneratedDate);
            throw new IllegalStateException(MESSAGE_ECO_NEWS_LIMIT);
        }
        lastGeneratedDate = LocalDate.now();

        String jsonResponse = fetchNewsWithoutQuery(language);
        log.info("Received JSON response for eco news generation: {}", jsonResponse);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse);
        ecoNews = ecoNewsRepo.save(ecoNews);
        log.info(ECO_NEWS_SAVED_SUCCESSFULLY, ecoNews.getId());

        return ecoNews.getText();
    }

    @Override
    public List<EcoNewsDto> getRelevantEcoNewsForUser(Long userId, String language) {
        log.info(USER_RELEVANT_ECO_NEWS_REQUEST, userId, language);
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
        List<String> habitAssigns = habitAssignRepo.fetchHabitNamesByUserId(userId);

        List<EcoNewsDto> relevantEcoNews = ecoNewsList.stream()
            .map(ecoNews -> {
                EcoNewsDto ecoNewsDto = modelMapper.map(ecoNews, EcoNewsDto.class);
                if (habitAssigns != null) {
                    double relevanceScore = calculateRelevanceScore(ecoNews, habitAssigns);
                    ecoNewsDto.setRelevanceScore(relevanceScore);
                }
                return ecoNewsDto;
            }).sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();

        log.info(USER_RELEVANT_ECO_NEWS_RETRIEVED, userId, relevantEcoNews.size());
        return relevantEcoNews;
    }

    @Override
    public List<EcoNewsDto> getCombinedEcoNewsForUser(Long userId, String language) {
        log.info(COMBINED_ECO_NEWS_REQUEST, userId, language);
        List<EcoNewsDto> relevantEcoNews = getRelevantEcoNewsForUser(userId, language);
        List<EcoNewsDto> generalEcoNews = getGeneralEcoNews(language);

        List<EcoNewsDto> combinedNews = new ArrayList<>(relevantEcoNews);
        combinedNews.addAll(generalEcoNews);

        List<EcoNewsDto> sortedCombinedNews = combinedNews.stream()
            .sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();

        log.info(COMBINED_ECO_NEWS_RETRIEVED, userId, sortedCombinedNews.size());
        return sortedCombinedNews;
    }

    private List<EcoNewsDto> getGeneralEcoNews(String language) {
        log.info(GENERAL_ECO_NEWS_REQUEST, language);
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
        return ecoNewsList.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsDto.class))
            .toList();
    }

    private double calculateRelevanceScore(EcoNews ecoNews, List<String> habitNames) {
        log.info(RELEVANCE_SCORE_CALCULATION_STARTED, ecoNews, habitNames);
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
        log.info(RELEVANCE_ANALYSIS_STARTED, topic1, topic2);
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
        log.info(USER_HABIT_ASSIGNMENTS_FETCH_INITIATED, userId);
        return habitAssignRepo.findAllByUserId(userId);
    }

    private Habit fetchRandomHabit() {
        log.info(RANDOM_HABIT_FETCH_INITIATED);
        return habitRepo.findRandomHabit();
    }

    private String createNewsRequest(String language, String query) {
        log.info(NEWS_QUERY_CREATION_STARTED, language, query);
        String baseRequest = query == null
            ? NEWS_WITHOUT_QUERY
            : NEWS_BY_QUERY + query;

        return language + baseRequest + MESSAGE_JSON_VALIDATION_HINT
            + REQUEST_MAX_TOKENS_KEY + MAX_ALLOWED_TOKENS;
    }

    private String fetchForecast(String language, List<HabitAssign> habitAssigns) {
        log.info(FORECAST_FETCH_INITIATED, language, habitAssigns);
        List<DurationHabitDto> durationHabitDtos = habitAssigns.stream()
            .map(this::mapToDurationHabitDto)
            .toList();
        return openAIService.makeRequest(language + FORECAST + durationHabitDtos);
    }

    private DurationHabitDto mapToDurationHabitDto(HabitAssign habitAssign) {
        log.info(HABIT_DTO_MAPPING_STARTED, habitAssign);
        return modelMapper.map(habitAssign, DurationHabitDto.class);
    }

    private String fetchAdvice(String language, Habit habit) {
        log.info(ADVICE_FETCH_INITIATED, language, habit);
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        return openAIService.makeRequest(language + ADVICE + shortHabitDto);
    }

    private String fetchNewsWithoutQuery(String language) {
        log.info(NEWS_FETCH_WITHOUT_QUERY_INITIATED, language);
        return openAIService.makeRequest(language + NEWS_WITHOUT_QUERY);
    }

    private JsonNode parseJsonResponse(String jsonResponse) {
        log.info(JSON_RESPONSE_PARSING_STARTED, jsonResponse);
        String sanitizedResponse = jsonResponse.replace(FORMAT_ASTERISKS_ESCAPE, FORMAT_EMPTY_STRING).trim();

        if (sanitizedResponse.startsWith(FORMAT_TITLE_PREFIX)) {
            return getJsonNodes(sanitizedResponse);
        } else {
            return parseJsonString(sanitizedResponse);
        }
    }

    private JsonNode parseJsonString(String sanitizedResponse) {
        log.info(JSON_STRING_PARSING_STARTED, sanitizedResponse);
        try {
            sanitizedResponse = sanitizedResponse.replace(FORMAT_JSON_CODE_BLOCK_START, FORMAT_EMPTY_STRING)
                .replace(FORMAT_JSON_CODE_BLOCK_END, FORMAT_EMPTY_STRING).trim();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(sanitizedResponse);
        } catch (Exception e) {
            throw new EcoNewsCreationException(ERROR_JSON_INVALID_FORMAT + sanitizedResponse, e);
        }
    }

    private static @NotNull ObjectNode getJsonNodes(String sanitizedResponse) {
        log.info(JSON_NODES_EXTRACTION_STARTED, sanitizedResponse);
        String[] parts = splitResponse(sanitizedResponse);
        String title = extractTitle(parts);
        String content = extractContent(parts);

        return createJsonNode(title, content);
    }

    private static ObjectNode createJsonNode(String title, String content) {
        log.info(JSON_NODE_CREATION_STARTED, title, content);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(FORMAT_TITLE_KEY, title);
        jsonNode.put(RESPONSE_JSON_CONTENT_KEY, content);
        return jsonNode;
    }

    private static String[] splitResponse(String response) {
        log.info(RESPONSE_SPLIT_STARTED, response);
        return response.split(FORMAT_NEW_LINE, 2);
    }

    private static String extractTitle(String[] parts) {
        log.info(RESPONSE_TITLE_EXTRACTION_STARTED, (Object) parts);
        return parts[0].replace(FORMAT_TITLE_PREFIX, FORMAT_EMPTY_STRING).trim();
    }

    private static String extractContent(String[] parts) {
        log.info(RESPONSE_CONTENT_EXTRACTION_STARTED, (Object) parts);
        return parts.length > 1 ? parts[1].trim() : FORMAT_EMPTY_STRING;
    }

    private EcoNews createEcoNewsInstance(String jsonResponse) {
        log.info(ECO_NEWS_INSTANCE_CREATION_STARTED, jsonResponse);
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
        log.info(AI_USER_FETCH_OR_CREATION_INITIATED);
        return userRepo.findByEmail(AI_USER_EMAIL)
            .orElseGet(this::createAiGeneratedUser);
    }

    private String extractTitleFromJson(JsonNode jsonNode) {
        log.info(JSON_TITLE_EXTRACTION_STARTED, jsonNode);
        return jsonNode.get(FORMAT_TITLE_KEY).asText();
    }

    private String extractContentFromJsonNode(JsonNode jsonNode) {
        log.info(JSON_CONTENT_EXTRACTION_STARTED, jsonNode);
        return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
    }

    private EcoNews buildEcoNews(String title, String content, User aiGeneratedUser, Tag tag) {
        log.info(ECO_NEWS_BUILD_STARTED, title, content, aiGeneratedUser, tag);
        return EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .author(aiGeneratedUser)
            .title(title)
            .text(content)
            .tags(List.of(tag))
            .build();
    }

    private User createAiGeneratedUser() {
        log.info(AI_GENERATED_USER_CREATION_STARTED);
        User user = User.builder()
            .name(AI_USER_NAME)
            .dateOfRegistration(LocalDateTime.now())
            .email(AI_USER_EMAIL)
            .role(ROLE_USER)
            .refreshTokenKey(AI_MOCKED_REFRESH_TOKEN)
            .build();

        return userRepo.save(user);
    }

    private String extractContentFromJson(String jsonResponse) {
        log.info(JSON_CONTENT_EXTRACTION_PROCESS, jsonResponse);
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                jsonResponse = sanitizeJsonResponse(jsonResponse);

                if (!isJsonResponseComplete(jsonResponse)) {
                    throw new IncompleteJsonException(ERROR_JSON_INCOMPLETE);
                }
                return parseContentFromJson(jsonResponse);
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS
                        + maxRetries + FORMAT_ATTEMPTS_SUFFIX, e);
                }
            }
        }
        throw new JsonResponseParseException(ERROR_PARSING_JSON_AFTER_ATTEMPTS);
    }

    private String parseContentFromJson(String jsonResponse) {
        log.info(JSON_CONTENT_PARSING_STARTED, jsonResponse);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.get(RESPONSE_JSON_CONTENT_KEY).asText();
        } catch (Exception e) {
            throw new JsonResponseParseException(ERROR_JSON_PARSE_FAILURE, e);
        }
    }

    private boolean isJsonResponseComplete(String jsonResponse) {
        log.info(JSON_RESPONSE_VALIDATION_STARTED, jsonResponse);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.has(FORMAT_TITLE_KEY)
                && jsonNode.has(RESPONSE_JSON_CONTENT_KEY);
        } catch (Exception e) {
            return false;
        }
    }

    private String sanitizeJsonResponse(String jsonResponse) {
        log.info(JSON_SANITIZATION_STARTED, jsonResponse);
        return jsonResponse.replace(FORMAT_ASTERISKS_ESCAPE, FORMAT_EMPTY_STRING).trim();
    }

    private boolean isWeekPassed() {
        log.info(WEEK_PASSED_CHECK_INITIATED);
        return ChronoUnit.WEEKS.between(lastGeneratedDate, LocalDate.now()) >= 1;
    }

    private void validateInputs(Object... inputs) {
        log.info(INPUT_VALIDATION_STARTED, (Object) inputs);
        for (Object input : inputs) {
            switch (input) {
                case null -> throw new InvalidInputException(ERROR_INPUT_CANNOT_BE_NULL);
                case String str when str.isBlank() -> throw new InvalidInputException(ERROR_STRING_CANNOT_BE_EMPTY);
                case Long l when l <= 0 -> throw new InvalidInputException(ERROR_LONG_VALUE_MUST_BE_POSITIVE);
                default -> {
                    log.error(INVALID_INPUT_TYPE, input.getClass().getName());
                    throw new InvalidInputException(INVALID_INPUT_TYPE + input.getClass().getName());
                }
            }
        }
    }
}