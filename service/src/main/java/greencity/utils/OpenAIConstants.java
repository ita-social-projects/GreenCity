package greencity.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenAIConstants {
    public static final String MODEL_NAME = "gpt-4o-mini";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON_TYPE = "application/json";
    public static final String MODEL_KEY = "model";
    public static final String MESSAGES_KEY = "messages";
    public static final String MAX_TOKENS_KEY = "max_tokens";
    public static final String TEMPERATURE_KEY = "temperature";

    public static final String JSON_CONTENT_KEY = "content";
    public static final String ROLE_KEY = "role";
    public static final String CHOICES_KEY = "choices";
    public static final String MESSAGE_KEY = "message";
    public static final String SYSTEM_ROLE = "system";
    public static final String USER_ROLE = "user";

    public static final String ERROR_MISSING_API_KEY = "OpenAI API key is missing!";
    public static final String ERROR_MISSING_API_URL = "OpenAI API URL is missing!";
    public static final String ERROR_MISSING_PROMPT = "Prompt is missing!";
    public static final String ERROR_INVALID_RESPONSE = "Could not get a valid response from OpenAI.";
    public static final String ERROR_NO_RESPONSE = "Could not get a response from OpenAI.";
    public static final String INVALID_JSON_MESSAGE = "Invalid JSON response: ";
    public static final String INCOMPLETE_JSON_MESSAGE = "Incomplete JSON response: ";
    public static final String JSON_PARSE_FAILURE_MESSAGE = "Failed to parse JSON response";
    public static final String INPUT_CANNOT_BE_NULL_MESSAGE = "Input cannot be null";
    public static final String STRING_INPUT_CANNOT_BE_BLANK_MESSAGE = "String input cannot be blank";
    public static final String LONG_INPUT_MUST_BE_GREATER_THAN_ZERO_MESSAGE = "Long input must be greater than zero";
    public static final String FAILED_TO_PARSE_JSON_RESPONSE = "Failed to parse JSON response after ";
    public static final String NO_TAGS_FOUND_FOR_AI_GENERATED = "No tags found for AI Generated.";
    public static final String OPENAI_INVALID_RELEVANCE_SCORE = "Invalid relevance score from OpenAI: ";
    public static final String OPENAI_PARSE_FAILURE = "Failed to parse relevance score from OpenAI response: ";

    public static final String MAX_TOKENS = " Max tokens: ";
    public static final Integer MAX_TOKENS_VALUE = 2048;

    public static final String ECO_NEWS_GENERATION_LIMIT_MESSAGE = "Eco-news can only be generated once a week.";
    public static final String JSON_VALIDATION_INSTRUCTION = " Please ensure the JSON response is complete and properly closed.";
    public static final String LANGUAGE_POLICY = "You are an AI assistant. Always respond in the language provided by the user.";

    public static final String TITLE_PREFIX = "Title:";
    public static final String EMPTY_STRING = "";
    public static final String TITLE = "title";
    public static final String NEW_LINE = "\n";
    public static final String CODE_BLOCK_JSON = "```json";
    public static final String ASTERISKS_ESCAPE = "\\*\\*";
    public static final String ATTEMPTS_SUFFIX = " attempts";
    public static final String CURLY_BRACE = "}";
    public static final String CODE_BLOCK_END = "```";

    public static final String AI_GENERATED_USER_NAME = "AI Generated";
    public static final String AI_GENERATED_USER_EMAIL = "ai.generated@example.com";
    public static final String MOCKED_AI_USER_REFRESH_TOKEN_KEY = "mocked-refresh-token-key";
}

