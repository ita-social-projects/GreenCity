package greencity.log;

public class OpenAILogMessages {
    public static final String FORECAST_REQUEST_INITIATED = "getForecast called with userId: {}, language: {}";
    public static final String FORECAST_FETCH_INITIATED = "fetchForecast called with language: {}, habitAssigns: {}";

    public static final String NO_HABIT_ASSIGNMENTS_DETECTED = "No habit assignments found for userId: {}";
    public static final String HABIT_ASSIGNMENTS_RETRIEVED = "Habit assignments found for userId: {}";
    public static final String USER_HABIT_ASSIGNMENTS_FETCH_INITIATED = "fetchHabitAssignsByUserId called with userId: {}";
    public static final String RANDOM_HABIT_SELECTED = "Random habit fetched: {}";
    public static final String RANDOM_HABIT_FETCH_INITIATED = "fetchRandomHabit called";
    public static final String HABIT_DTO_MAPPING_STARTED = "mapToDurationHabitDto called with habitAssign: {}";

    public static final String ADVICE_REQUEST_INITIATED = "getAdvice called with userId: {}, language: {}";
    public static final String ADVICE_FETCH_INITIATED = "fetchAdvice called with language: {}, habit: {}";

    public static final String NEWS_REQUEST_INITIATED = "getNews called with language: {}, query: {}";
    public static final String NEWS_FETCH_WITHOUT_QUERY_INITIATED = "fetchNewsWithoutQuery called with language: {}";
    public static final String NEWS_QUERY_CREATION_STARTED = "createNewsRequest called with language: {}, query: {}";

    public static final String ECO_NEWS_GENERATION_REQUEST = "generateEcoNewsBasedOnHabits called with language: {}";
    public static final String ECO_NEWS_GENERATION_LIMIT_EXCEEDED = "Eco news generation limit reached. Last generated date: {}";
    public static final String ECO_NEWS_SAVED_SUCCESSFULLY = "Eco news saved with ID: {}";

    public static final String USER_RELEVANT_ECO_NEWS_REQUEST = "getRelevantEcoNewsForUser called with userId: {}, language: {}";
    public static final String USER_RELEVANT_ECO_NEWS_RETRIEVED = "Relevant eco news for userId: {} found: {}";
    public static final String COMBINED_ECO_NEWS_REQUEST = "getCombinedEcoNewsForUser called with userId: {}, language: {}";
    public static final String COMBINED_ECO_NEWS_RETRIEVED = "Combined eco news for userId: {} found: {}";
    public static final String GENERAL_ECO_NEWS_REQUEST = "getGeneralEcoNews called with language: {}";

    public static final String ECO_NEWS_INSTANCE_CREATION_STARTED = "createEcoNewsInstance called with jsonResponse: {}";
    public static final String ECO_NEWS_BUILD_STARTED = "buildEcoNews called with title: {}, content: {}, aiGeneratedUser: {}, tag: {}";

    public static final String AI_USER_FETCH_OR_CREATION_INITIATED = "fetchOrCreateAiGeneratedUser called";
    public static final String AI_GENERATED_USER_CREATION_STARTED = "createAiGeneratedUser called";

    public static final String API_RESPONSE_RECEIVED = "Received JSON response: {}";
    public static final String JSON_RESPONSE_PARSING_STARTED = "parseJsonResponse called with jsonResponse: {}";
    public static final String JSON_STRING_PARSING_STARTED = "parseJsonString called with sanitizedResponse: {}";
    public static final String JSON_NODES_EXTRACTION_STARTED = "getJsonNodes called with sanitizedResponse: {}";
    public static final String JSON_NODE_CREATION_STARTED = "createJsonNode called with title: {}, content: {}";

    public static final String JSON_TITLE_EXTRACTION_STARTED = "extractTitleFromJson called with jsonNode: {}";
    public static final String JSON_CONTENT_EXTRACTION_STARTED = "extractContentFromJsonNode called with jsonNode: {}";
    public static final String JSON_CONTENT_EXTRACTION_PROCESS = "extractContentFromJson called with jsonResponse: {}";
    public static final String JSON_CONTENT_PARSING_STARTED = "parseContentFromJson called with jsonResponse: {}";

    public static final String JSON_RESPONSE_VALIDATION_STARTED = "isJsonResponseComplete called with jsonResponse: {}";
    public static final String JSON_SANITIZATION_STARTED = "sanitizeJsonResponse called with jsonResponse: {}";
    public static final String RESPONSE_SPLIT_STARTED = "splitResponse called with response: {}";
    public static final String RESPONSE_TITLE_EXTRACTION_STARTED = "extractTitle called with parts: {}";
    public static final String RESPONSE_CONTENT_EXTRACTION_STARTED = "extractContent called with parts: {}";

    public static final String RELEVANCE_SCORE_CALCULATION_STARTED = "calculateRelevanceScore called with ecoNews: {}, habitNames: {}";
    public static final String RELEVANCE_ANALYSIS_STARTED = "analyzeRelevance called with topic1: {}, topic2: {}";

    public static final String OPENAI_REQUEST_INITIATED = "Making request to OpenAI with prompt: {}";
    public static final String OPENAI_REQUEST_VALIDATION_FAILED = "Validation error: {}";
    public static final String OPENAI_SENDING_REQUEST = "Sending request to OpenAI API at {} with body: {}";
    public static final String OPENAI_RESPONSE_RECEIVED = "Received response from OpenAI API: {}";
    public static final String OPENAI_REQUEST_FAILED = "Failed to send request to OpenAI API";
    public static final String OPENAI_REQUEST_BODY_CREATION = "Creating request body for prompt: {}";
    public static final String OPENAI_HTTP_HEADERS_SETUP = "Creating HTTP headers";
    public static final String OPENAI_REQUEST_PARAMETER_VALIDATION = "Validating request parameters";

    public static final String INPUT_VALIDATION_STARTED = "validateInputs called with inputs: {}";
    public static final String INVALID_INPUT_TYPE = "Invalid input type: {}";

    public static final String WEEK_PASSED_CHECK_INITIATED = "isWeekPassed called";

    private OpenAILogMessages() {
    }
}
