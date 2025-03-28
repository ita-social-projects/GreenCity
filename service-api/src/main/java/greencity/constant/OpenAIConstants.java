package greencity.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAIConstants {
    public static final String OPENAI_MODEL_NAME = "gpt-4o-mini";
    public static final String OPENAI_AUTH_HEADER = "Authorization";
    public static final String OPENAI_BEARER_PREFIX = "Bearer ";
    public static final String OPENAI_CONTENT_TYPE_HEADER = "Content-Type";
    public static final String OPENAI_APPLICATION_JSON = "application/json";

    public static final String REQUEST_MODEL_KEY = "model";
    public static final String REQUEST_MESSAGES_KEY = "messages";
    public static final String REQUEST_MAX_TOKENS_KEY = "max_tokens";
    public static final String REQUEST_TEMPERATURE_KEY = "temperature";

    public static final String RESPONSE_JSON_CONTENT_KEY = "content";
    public static final String RESPONSE_ROLE_KEY = "role";
    public static final String RESPONSE_CHOICES_KEY = "choices";
    public static final String RESPONSE_MESSAGE_KEY = "message";

    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";

    public static final String ERROR_API_KEY_MISSING = "OpenAI API key is missing!";
    public static final String ERROR_API_URL_MISSING = "OpenAI API URL is missing!";
    public static final String ERROR_PROMPT_MISSING = "The prompt cannot be empty!";
    public static final String ERROR_INVALID_OPENAI_RESPONSE = "Received an invalid response from OpenAI.";
    public static final String ERROR_NO_OPENAI_RESPONSE = "No response received from OpenAI.";
    public static final String ERROR_JSON_INVALID_FORMAT = "Invalid JSON format in OpenAI response.";
    public static final String ERROR_JSON_INCOMPLETE = "Incomplete JSON response received.";
    public static final String ERROR_JSON_PARSE_FAILURE = "Failed to parse OpenAI JSON response.";
    public static final String ERROR_INPUT_CANNOT_BE_NULL = "Input value cannot be null.";
    public static final String ERROR_STRING_CANNOT_BE_EMPTY = "String input cannot be blank.";
    public static final String ERROR_LONG_VALUE_MUST_BE_POSITIVE = "Long input must be greater than zero.";
    public static final String ERROR_PARSING_JSON_AFTER_ATTEMPTS = "Failed to parse JSON response after multiple attempts.";
    public static final String ERROR_NO_TAGS_FOUND = "No tags found for AI-generated content.";
    public static final String ERROR_INVALID_RELEVANCE_SCORE = "Invalid relevance score received from OpenAI.";
    public static final String ERROR_RELEVANCE_SCORE_PARSE_FAILURE = "Failed to parse relevance score from OpenAI response.";
    public static final String LOG_VALID_LONG_INPUT = "Received valid Long input: {}";
    public static final String LOG_VALID_STRING_INPUT = "Received valid String input: '{}'";
    public static final String LOG_UNSUPPORTED_INPUT_TYPE = "Error: Unsupported input type detected - {}";
    public static final String ERROR_UNSUPPORTED_INPUT_TYPE = "Error: Unsupported input type - ";
    public static final String LOG_JSON_VALIDATION_STARTED = "Starting JSON response validation: {}";
    public static final String ERROR_JSON_VALIDATION_FAILURE = "Unexpected error occurred during JSON validation.";
    public static final String WARNING_JSON_MISSING_REQUIRED_FIELDS = "JSON validation warning: Required fields are missing. Title present: {}, Content present: {}";
    public static final String WARNING_JSON_MISSING_BRACES = "JSON validation warning: Response does not start or end with curly braces. Attempting to correct formatting.";
    public static final String LOG_JSON_CORRECTED_WITH_BRACES = "JSON response wrapped with curly braces for proper formatting: {}";
    public static final String ERROR_PARSING_JSON_GENERIC = "Error parsing JSON response.";
    public static final String ERROR_JSON_KEY_NOT_FOUND = "Expected key " + RESPONSE_JSON_CONTENT_KEY + " not found in JSON.";
    public static final String ERROR_JSON_PARSE_CONTENT_FAILED = "Failed to parse content from JSON";

    public static final String ATTEMPT_LOG_MESSAGE = " Attempt: {}";

    public static final String COMBINED_ECO_NEWS_REQUEST = "COMBINED_ECO_NEWS_REQUEST: userId={}, language={}";
    public static final String USER_ID_NULL = "UserId is null, returning general eco news.";
    public static final String FETCHING_RELEVANT_ECO_NEWS = "Fetching relevant eco news for userId={}";
    public static final String COMBINED_ECO_NEWS_SIZE_AFTER_SORTING = "Combined eco news size after sorting: {}";

    public static final Integer MAX_ALLOWED_TOKENS = 2048;
    public static final String MESSAGE_ECO_NEWS_LIMIT = "Eco-news can only be generated once per week.";
    public static final String MESSAGE_JSON_VALIDATION_HINT = "Ensure the JSON response is complete and properly formatted.";

    public static final String AI_LANGUAGE_POLICY = "You are an AI assistant. Always respond in the language provided by the user.";

    public static final String FORMAT_TITLE_PREFIX = "Title: ";
    public static final String FORMAT_EMPTY_STRING = "";
    public static final String FORMAT_TITLE_KEY = "title";
    public static final String FORMAT_NEW_LINE = "\n";
    public static final String FORMAT_JSON_CODE_BLOCK_START = "```json";
    public static final String FORMAT_JSON_CODE_BLOCK_END = "```";
    public static final String FORMAT_ASTERISKS_ESCAPE = "\\*\\*";
    public static final String FORMAT_ATTEMPTS_SUFFIX = " attempts";
    public static final String FORMAT_BOLD_PATTERN = "\\*\\*(.*?)\\*\\*";
    public static final String FORMAT_ITALIC_PATTERN = "\\*(.*?)\\*";
    public static final String FORMAT_JSON_BLOCK_PATTERN = "(?s)```json\\s*";
    public static final String FORMAT_CODE_BLOCK_PATTERN = "(?s)```\\s*$";
    public static final String FORMAT_QUOTES_PATTERN = "[“”]";
    public static final String TEXT_FORMAT_REPLACEMENT = "$1";
    public static final String EMPTY_REPLACEMENT = "";
    public static final String QUOTES_REPLACEMENT = "\"";
    public static final String OPENING_CURLY_BRACE = "{";
    public static final String CLOSING_CURLY_BRACE = "}";

    public static final String AI_USER_NAME = "AI Generated";
    public static final String AI_USER_EMAIL = "ai.generated@example.com";
    public static final String AI_MOCKED_REFRESH_TOKEN = "mocked-refresh-token-key";

    public static final String RECEIVED_JSON_RESPONSE = "Received JSON response for eco news generation: {}";
    public static final String RAW_JSON_LOG_MESSAGE = "Raw JSON response: {}";

    public static final int MAX_JSON_PARSE_ATTEMPTS = 3;
}


