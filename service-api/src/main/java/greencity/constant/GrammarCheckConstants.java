package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GrammarCheckConstants {
    public static final String ENGLISH_PREFIX = "en";
    public static final String LANGUAGE_CACHE_NAME = "languageCache";
    public static final String GRAMMAR_CACHE_NAME = "grammarCache";
    public static final String LANG_DETECT_MODEL_PATH = "/models/langdetect-183.bin";

    public static final String ERROR_GRAMMAR_CHECKING_MESSAGE = "An error occurred while checking the grammar of the provided text.";

    public static final String DEFAULT_LANGUAGE_CODE = "unknown";

    public static final String CHECK_GRAMMAR_LOG_MESSAGE = "▶️ Method 'checkGrammar' called with text: \"{}\" at {}";
    public static final String CHECK_GRAMMAR_DEBUG_LOG = "Parameters for method 'checkGrammar': text = \"{}\"";
    public static final String START_PROCESSING_LOG = "Start processing text for grammar check...";
    public static final String PROCESSING_RESULT_LOG = "Processing result from GrammarChecker...";
    public static final String NO_ERRORS_LOG = "✅ No grammar errors found in the text.";
    public static final String FOUND_ERRORS_LOG = "⚠️ Found {} grammar error(s) in the text:";
    public static final String ERROR_DETAILS_LOG = "🔸 Error: \"{}\" → \"{}\" [{}] at position {}";
    public static final String GRAMMAR_CHECK_COMPLETED_LOG = "🏁 Grammar check completed. Corrected text length: {} characters";
    public static final String FINAL_CORRECTED_TEXT_LOG = "Final corrected text: \"{}\"";
    public static final String GRAMMAR_CHECK_ERROR_LOG = "❌ Error occurred during grammar check for text: \"{}\". Exception: {}";
    public static final String METHOD_EXECUTION_DURATION_LOG = "Method 'checkGrammar' executed in {} ms.";
    public static final String CLEAR_CACHE_LOG_MESSAGE = "🗑️ Method 'clearCache' called to clear cache for text: \"{}\" at {}";
    public static final String CLEARING_CACHE_LOG = "Clearing cache for text: \"{}\"...";
}

