package greencity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static greencity.constant.OpenAIRequest.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.habit.DurationHabitDto;
import greencity.dto.habit.ShortHabitDto;
import greencity.entity.*;
import static greencity.enums.Role.ROLE_USER;
import static greencity.enums.TagType.ECO_NEWS;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import static greencity.utils.OpenAIConstants.*;
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
        validateInputs(userId, language);

        List<HabitAssign> habitAssigns = fetchHabitAssignsByUserId(userId);

        return habitAssigns.isEmpty()
            ? getAdvice(userId, language)
            : fetchForecast(language, habitAssigns);
    }

    @Override
    public String getAdvice(Long userId, String language) {
        validateInputs(userId, language);
        Habit habit = fetchRandomHabit();

        return fetchAdvice(language, habit);
    }

    @Override
    public String getNews(String language, String query) {
        validateInputs(language);
        String jsonResponse = openAIService.makeRequest(createNewsRequest(language, query));
        return extractContentFromJson(jsonResponse);
    }

    @Override
    public String generateEcoNewsBasedOnHabits(String language) {
        validateInputs(language);
        if (!isWeekPassed()) {
            throw new IllegalStateException(ECO_NEWS_GENERATION_LIMIT_MESSAGE);
        }
        lastGeneratedDate = LocalDate.now();

        String jsonResponse = fetchNewsWithoutQuery(language);
        EcoNews ecoNews = createEcoNewsInstance(jsonResponse);
        ecoNews = ecoNewsRepo.save(ecoNews);

        return ecoNews.getText();
    }

    @Override
    public List<EcoNewsDto> getRelevantEcoNewsForUser(Long userId, String language) {
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
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
    public List<EcoNewsDto> getCombinedEcoNewsForUser(Long userId, String language) {
        List<EcoNewsDto> relevantEcoNews = getRelevantEcoNewsForUser(userId, language);
        List<EcoNewsDto> generalEcoNews = getGeneralEcoNews(language);

        List<EcoNewsDto> combinedNews = new ArrayList<>(relevantEcoNews);
        combinedNews.addAll(generalEcoNews);

        return combinedNews.stream()
            .sorted(Comparator.comparingDouble(EcoNewsDto::getRelevanceScore).reversed())
            .toList();
    }

    private List<EcoNewsDto> getGeneralEcoNews(String language) {
        List<EcoNews> ecoNewsList = ecoNewsRepo.findAll();
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
                throw new OpenAIRelevanceException(OPENAI_INVALID_RELEVANCE_SCORE + score);
            }
            return score;
        } catch (NumberFormatException e) {
            throw new OpenAIRelevanceException(OPENAI_PARSE_FAILURE + response, e);
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

        return language + baseRequest + JSON_VALIDATION_INSTRUCTION
            + MAX_TOKENS + MAX_TOKENS_VALUE;
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

    private String fetchAdvice(String language, Habit habit) {
        ShortHabitDto shortHabitDto = modelMapper.map(habit, ShortHabitDto.class);
        return openAIService.makeRequest(language + ADVICE + shortHabitDto);
    }

    private String fetchNewsWithoutQuery(String language) {
        return openAIService.makeRequest(language + NEWS_WITHOUT_QUERY);
    }

    private JsonNode parseJsonResponse(String jsonResponse) {
        String sanitizedResponse = jsonResponse.replace(ASTERISKS_ESCAPE, EMPTY_STRING).trim();

        if (sanitizedResponse.startsWith(TITLE_PREFIX)) {
            return getJsonNodes(sanitizedResponse);
        } else {
            return parseJsonString(sanitizedResponse);
        }
    }

    private JsonNode parseJsonString(String sanitizedResponse) {
        try {
            sanitizedResponse = sanitizedResponse.replace(CODE_BLOCK_JSON, EMPTY_STRING)
                .replace(CODE_BLOCK_END, EMPTY_STRING).trim();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(sanitizedResponse);
        } catch (Exception e) {
            throw new EcoNewsCreationException(INVALID_JSON_MESSAGE + sanitizedResponse, e);
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
        jsonNode.put(TITLE, title);
        jsonNode.put(JSON_CONTENT_KEY, content);
        return jsonNode;
    }

    private static String[] splitResponse(String response) {
        return response.split(NEW_LINE, 2);
    }

    private static String extractTitle(String[] parts) {
        return parts[0].replace(TITLE_PREFIX, EMPTY_STRING).trim();
    }

    private static String extractContent(String[] parts) {
        return parts.length > 1 ? parts[1].trim() : EMPTY_STRING;
    }

    private EcoNews createEcoNewsInstance(String jsonResponse) {
        JsonNode jsonNode = parseJsonResponse(jsonResponse);
        String title = extractTitleFromJson(jsonNode);
        String content = extractContentFromJsonNode(jsonNode);
        User aiGeneratedUser = fetchOrCreateAiGeneratedUser();
        List<Tag> tags = tagsRepo.findTagsByType(ECO_NEWS);

        if (tags.isEmpty()) {
            throw new EcoNewsCreationException(NO_TAGS_FOUND_FOR_AI_GENERATED);
        }

        Tag tag = tags.getFirst();
        return buildEcoNews(title, content, aiGeneratedUser, tag);
    }

    private User fetchOrCreateAiGeneratedUser() {
        return userRepo.findByEmail(AI_GENERATED_USER_EMAIL)
            .orElseGet(this::createAiGeneratedUser);
    }

    private String extractTitleFromJson(JsonNode jsonNode) {
        return jsonNode.get(TITLE).asText();
    }

    private String extractContentFromJsonNode(JsonNode jsonNode) {
        return jsonNode.get(JSON_CONTENT_KEY).asText();
    }

    private EcoNews buildEcoNews(String title, String content, User aiGeneratedUser, Tag tag) {
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
            .name(AI_GENERATED_USER_NAME)
            .dateOfRegistration(LocalDateTime.now())
            .email(AI_GENERATED_USER_EMAIL)
            .role(ROLE_USER)
            .refreshTokenKey(MOCKED_AI_USER_REFRESH_TOKEN_KEY)
            .build();

        return userRepo.save(user);
    }

    private String extractContentFromJson(String jsonResponse) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                jsonResponse = sanitizeJsonResponse(jsonResponse);

                if (!isJsonResponseComplete(jsonResponse)) {
                    throw new IncompleteJsonException(INCOMPLETE_JSON_MESSAGE);
                }
                return parseContentFromJson(jsonResponse);
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new JsonResponseParseException(FAILED_TO_PARSE_JSON_RESPONSE
                        + maxRetries + ATTEMPTS_SUFFIX, e);
                }
            }
        }
        throw new JsonResponseParseException(JSON_PARSE_FAILURE_MESSAGE);
    }

    private String parseContentFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.get(JSON_CONTENT_KEY).asText();
        } catch (Exception e) {
            throw new JsonResponseParseException(JSON_PARSE_FAILURE_MESSAGE, e);
        }
    }

    private boolean isJsonResponseComplete(String jsonResponse) {
        return jsonResponse.endsWith(CURLY_BRACE);
    }

    private String sanitizeJsonResponse(String jsonResponse) {
        return jsonResponse.replace(ASTERISKS_ESCAPE, EMPTY_STRING).trim();
    }

    private boolean isWeekPassed() {
        return ChronoUnit.WEEKS.between(lastGeneratedDate, LocalDate.now()) >= 1;
    }

    private void validateInputs(Object... inputs) {
        for (Object input : inputs) {
            if (input == null) {
                throw new InvalidInputException(INPUT_CANNOT_BE_NULL_MESSAGE);
            }

            switch (input) {
                case String str when str.isBlank():
                    throw new InvalidInputException(STRING_INPUT_CANNOT_BE_BLANK_MESSAGE);
                case Long l when l <= 0:
                    throw new InvalidInputException(LONG_INPUT_MUST_BE_GREATER_THAN_ZERO_MESSAGE);
                default:
                    break;
            }
        }
    }
}