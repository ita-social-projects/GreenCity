package greencity.constant;

import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AIEcoNewsRelevanceConstants {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    public static final String SEPARATOR = "-".repeat(80);
    public static final String LOG_ENTER_METHOD = "⭐ ENTERING: {}.{}() at {}";
    public static final String LOG_NO_ARGUMENTS = "📥 No arguments provided";
    public static final String LOG_METHOD_ARGS = "📥 METHOD ARGUMENTS:";
    public static final String LOG_ARG_NULL = "   Arg[{}]: null";
    public static final String LOG_ARG_LONG = "   Arg[{}] (Long): {}";
    public static final String LOG_ARG_GENERIC = "   Arg[{}] ({}): {}";
    public static final String LOG_ARG_ECONEWS = "   Arg[{}] (EcoNewsDto): id={}, title=\"{}\", content_length={}, author_id={}, creation_date={}";
    public static final String LOG_ARG_LIST = "   Arg[{}] (List<{}>): size={}, values=[{}]";
    public static final String LOG_PERFORMANCE = "⏱️ PERFORMANCE: {}.{}() execution time: {} ms, memory: {} KB";
    public static final String LOG_RETURN_NULL = "📤 RETURN VALUE: null";
    public static final String LOG_RETURN_SCORE = "📤 RETURN VALUE: score={} (classification: {})";
    public static final String LOG_RETURN_LIST = "📤 RETURN VALUE: List with {} items";
    public static final String LOG_RETURN_GENERIC = "📤 RETURN VALUE: {} ({})";
    public static final String LOG_RETURN_VALUE = "📤 RETURN VALUE from {}.{}(): {}";
    public static final String LOG_EXIT_METHOD = "✅ EXITING: {}.{}()";
    public static final String LOG_EXCEPTION = "❌ EXCEPTION in {}.{}(): {} - {}";
    public static final String LOG_EXCEPTION_DETAILS = "❌ EXCEPTION DETAILS for {}.{}():";
    public static final String BATCH_PROCESSING_FAILED = "Batch processing failed, triggering rollback";
    public static final String ECONEWS_OR_TITLE_CONTENT_NULL = "Eco-news or its title/content is null";
    public static final String HABITS_OR_TAGS_NULL = "Habits or tags are null";
    public static final String LOG_EXCEPTION_TYPE = "   Exception Type: {}";
    public static final String LOG_EXCEPTION_MESSAGE = "   Message: {}";
    public static final String LOG_EXCEPTION_STACKTRACE = "   Stack Trace: ";
    public static final String LOG_EXCEPTION_ARGS = "   Method arguments at time of exception:";
    public static final String LOG_EXCEPTION_CAUSE = "   Root Cause: {} - {}";
    public static final String LOG_RETRY = "🔄 RETRY ATTEMPT: calculateAIRelevanceScore for eco-news ID: {}";
    public static final String LOG_RETRY_ERROR = "Error in retry logging aspect";
    public static final String LOG_BATCH_PRECOMPUTE = "📊 BATCH PROCESSING: Starting precomputation of relevance scores for eco-news ID: {} across all users";
    public static final String LOG_BATCH_RECALCULATE = "📊 BATCH PROCESSING: Starting recalculation of all AI-generated news relevance scores for user ID: {}";
    public static final String LOG_EXCEPTION_IN_METHOD = "Method {}.{} threw an exception: [{}] - {}";
    public static final String LOG_EXCEPTION_FULL_STACKTRACE = "Full exception stacktrace in {}.{}:";
    public static final String FORMAT_FOUR_DECIMALS = "%.4f";
    public static final String TRUNCATION_SUFFIX = "...";
    public static final String COMMA_SEPARATOR = ", ";
    public static final String UNKNOWN_TYPE = "unknown";
    public static final String NULL_STRING = "null";
    public static final String NONE_STRING = "none";
    public static final String RELEVANCE_HIGH = "High relevance";
    public static final String RELEVANCE_MEDIUM_HIGH = "Medium-high relevance";
    public static final String RELEVANCE_MEDIUM = "Medium relevance";
    public static final String RELEVANCE_LOW_MEDIUM = "Low-medium relevance";
    public static final String RELEVANCE_LOW = "Low relevance";
    public static final String METHOD_PRECOMPUTE = "precomputeRelevanceForNewNews";
    public static final String METHOD_RECALCULATE = "recalculateRelevanceForUser";
    public static final String OPENAI_EXCEPTION_NAME = "OpenAIServiceException";

    public static final int BYTES_IN_KILOBYTE = 1024;
    public static final int MAX_LOGGED_LIST_CONTENT_LENGTH = 100;
    public static final int BATCH_SIZE = 100;
    public static final int MAX_TITLE_PREVIEW_LENGTH = 50;
    public static final int DEFAULT_INDEX = 0;
    public static final int MAX_PARAM_LENGTH = 200;
    public static final double RELEVANCE_THRESHOLD_HIGH = 0.8;
    public static final double RELEVANCE_THRESHOLD_MEDIUM_HIGH = 0.6;
    public static final double RELEVANCE_THRESHOLD_MEDIUM = 0.4;
    public static final double RELEVANCE_THRESHOLD_LOW_MEDIUM = 0.2;
    public static final double RELEVANCE_THRESHOLD_LOW = 0.0;

}
