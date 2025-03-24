package greencity.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrammarCheckConstants {
    public static final String ENGLISH_PREFIX = "en";
    public static final String LANGUAGE_CACHE_NAME = "languageCache";
    public static final String GRAMMAR_CACHE_NAME = "grammarCache";
    public static final String LANG_DETECT_MODEL_PATH = "/models/langdetect-183.bin";
    public static final String CACHE_CLEARED_LOG_MESSAGE = "Successfully cleared cache for the text: {}";
    public static final String CACHE_NOT_FOUND_LOG_MESSAGE = "Cache for '{}' not found. Cache operation failed.";

    public static final String ERROR_LANG_DETECT_MODEL_NOT_FOUND = "Language detection model file not found";
    public static final String ERROR_CHECKING_GRAMMAR = "Error checking grammar";
    public static final String ERROR_GRAMMAR_CHECKING_MESSAGE = "An error occurred while checking the grammar of the provided text.";

    public static final String DEFAULT_LANGUAGE_CODE = "unknown"; 

}
