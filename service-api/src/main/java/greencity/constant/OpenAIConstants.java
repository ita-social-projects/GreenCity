package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenAIConstants {
    public static final String OPENAI_AUTH_HEADER = "Authorization";
    public static final String OPENAI_BEARER_PREFIX = "Bearer ";
    public static final String OPENAI_CONTENT_TYPE_HEADER = "Content-Type";
    public static final String OPENAI_APPLICATION_JSON = "application/json";

    public static final String REQUEST_MODEL_KEY = "model";
    public static final String REQUEST_MESSAGES_KEY = "messages";
    public static final String REQUEST_MAX_TOKENS_KEY = "max_completion_tokens";
    public static final String REQUEST_TEMPERATURE_KEY = "temperature";
    public static final String REQUEST_RESPONSE_FORMAT_KEY = "response_format";

    public static final String RESPONSE_ID_KEY = "id";
    public static final String RESPONSE_JSON_CONTENT_KEY = "content";
    public static final String RESPONSE_ROLE_KEY = "role";
    public static final String RESPONSE_CHOICES_KEY = "choices";
    public static final String RESPONSE_MESSAGE_KEY = "message";
    public static final String RESPONSE_USAGE_KEY = "usage";
    public static final String RESPONSE_PROMPT_TOKENS_KEY = "prompt_tokens";
    public static final String RESPONSE_COMPLETION_TOKENS_KEY = "completion_tokens";
    public static final String RESPONSE_CREATED_KEY = "created";

    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";

    public static final String ERROR_API_KEY_MISSING = "OpenAI API key is missing!";
    public static final String ERROR_API_URL_MISSING = "OpenAI API URL is missing!";
    public static final String ERROR_PROMPT_MISSING = "The prompt cannot be empty!";
    public static final String ERROR_INVALID_OPENAI_RESPONSE = "Received an invalid response from OpenAI.";
    public static final String ERROR_NO_OPENAI_RESPONSE = "No response received from OpenAI.";
    public static final String ERROR_JSON_INVALID_FORMAT = "Invalid JSON format in OpenAI response.";
    public static final String ERROR_INVALID_TITLE_OR_CONTENT = "JSON format is invalid: missing 'title' or 'content'.";
    public static final String ERROR_JSON_PARSE_FAILURE = "Failed to parse OpenAI JSON response: {}";
    public static final String ERROR_NO_TAGS_FOUND = "No tags found for AI-generated content.";
    public static final String ERROR_JSON_KEY_NOT_FOUND =
        "Expected key " + RESPONSE_JSON_CONTENT_KEY + " not found in JSON.";
    public static final String ERROR_MAX_ATTEMPTS_REACHED = "Maximum number of attempts reached.";
    public static final String ERROR_ATTEMPTING_STOPPED = "Critical error - Attempting stopped.";
    public static final String ERROR_ECO_NEWS_CREATION_FAILED = "Eco-news creation failed.";
    public static final String OPEN_AI_REQUEST_FAILURE = "OpenAI request failure: {}";

    public static final Integer MAX_REQUEST_ATTEMPTS = 3;
    public static final String MESSAGE_CURRENT_ATTEMPT = "Current attempt: {}";
    public static final String MESSAGE_JSON_VALIDATION_HINT =
        "Ensure the JSON response is complete and properly formatted.";

    public static final String AI_ROLE_POLICY = "You are an AI assistant.";
    public static final String AI_LANGUAGE_POLICY = "Answer in specified language: %s.";
    public static final String AI_HEADINGS_POLICY =
        "Do not use headings, titles, or formatting styles to divide sections. "
            + "Present all content in plain text, using full sentences and \\n\\n to separate ideas."
            + "Use only single quotes inside text blocks.";
    public static final String AI_REQUEST_NEWS_LOCATION = "Generate eco-news about: %s.";
    public static final String AI_REQUEST_KNOWLEDGE_CUT_DATE = "Knowledge cut: %s.";
    public static final String AI_REQUEST_IDENTIFIER = "Request datetime identifier: %s.";

    public static final String FORMAT_TITLE_PREFIX = "Title: ";
    public static final String FORMAT_EMPTY_STRING = "";
    public static final String FORMAT_TITLE_KEY = "title";
    public static final String FORMAT_NEW_LINE = "\n";
    public static final String FORMAT_JSON_CODE_BLOCK_START = "```json";
    public static final String FORMAT_JSON_CODE_BLOCK_END = "```";
    public static final String FORMAT_ASTERISKS_ESCAPE = "\\*\\*";
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
    public static final String REGEX_MD_HEADERS = "(^|\\s)#{1,6}(\\s|$)";
    public static final String REGEX_ASTERISKS = "\\*";
    public static final String REGEX_MARKDOWN_ASTERISKS = "\\*+";

    public static final String AI_USER_NAME = "AI Generated";
    public static final String AI_USER_EMAIL = "ai.generated@example.com";
    public static final String AI_MOCKED_REFRESH_TOKEN = "mocked-refresh-token-key";
}
