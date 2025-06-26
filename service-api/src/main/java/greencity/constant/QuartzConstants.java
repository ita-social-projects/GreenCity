package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QuartzConstants {
    public static final String ECO_NEWS_GENERATION_JOB_IDENTITY = "ecoNewsGenerationJob";
    public static final String ECO_NEWS_GENERATION_TRIGGER_IDENTITY = "ecoNewsGenerationTrigger";

    public static final String CRON_DAY_OF_MONTH_PLACEHOLDER = "?";
    public static final String CRON_WILDCARD = "*";
    public static final String CRON_SPACE_SEPARATOR = " ";
    public static final String CRON_FIELD_SPLIT_REGEX = "\\s+";

    public static final int CRON_FIELDS_COUNT_EXPECTED = 6;
    public static final int CRON_FIELD_DAY_OF_MONTH_INDEX = 3;
    public static final int CRON_FIELD_DAY_OF_WEEK_INDEX = 5;

    public static final String CREATION_CRON_FAILED_MESSAGE = "Failed to create trigger with cron: ";
    public static final String INVALID_CRON_EXPRESSION_ERROR = "Invalid cron expression (must have 6 fields): ";
    public static final String METHOD_CALLED = "🔍 [TRACE] Method '{}' called with parameters: {}";
    public static final String METHOD_SUCCESS = "✅ [INFO] Method '{}' executed successfully in {} ms. Result: {}";
    public static final String METHOD_EXCEPTION = "❌ [ERROR] Method '{}' threw an exception after {} ms: {}";
    public static final String METHOD_EXECUTION_TIME = "⏱️ [INFO] Method '{}' executed in {} ms";
    public static final String METHOD_ENTERING = "🔍 [TRACE] Entering method '{}' with parameters: {}";
    public static final String METHOD_COMPLETED_EXECUTION =
        "⚙️ [DEBUG] Completed execution of method '{}' with parameters: {}";
    public static final String METHOD_FINISHED = "✅ [TRACE] Method '{}' finished with parameters: {}";
    public static final String CRON_EXPRESSION = "⚙️ [DEBUG] Cron expression received: {}";
    public static final String CRON_VALIDATION_STARTED = "⚙️ [DEBUG] Validating cron expression before execution...";
    public static final String CRON_EXECUTION_STARTED = "🔒 [INFO] Starting execution of cron job with expression: {}";
    public static final String METHOD_CRON_VALIDATION_SUCCESS = "⚙️ [DEBUG] Cron expression validated successfully";
    public static final String METHOD_CRON_VALIDATION_FAILED_EXCEPTION =
        "⚙️ [DEBUG] Cron validation failed due to exception: {}";
    public static final String CRON_EXPRESSION_WARNING =
        "⚠️ [WARN] The provided cron expression might be outdated or invalid: {}";
    public static final String CRON_EXPRESSION_CRITICAL_ERROR = "💥 [ERROR] Critical error in cron expression: {}";
    public static final String NEXT_FIRE_TIME = "🔁 Next fire time: {}";
    public static final String PREVIOUS_FIRE_TIME = "✅ Previous fire time: {}";
    public static final String NONE_EXECUTION = "None";
    public static final String JOB_LISTENER_NAME = "LoggingJobListener";
    public static final String JOB_EXECUTION_START = "🔄 Job '{}' is about to execute";
    public static final String JOB_EXECUTION_VETOED =
        "Job execution vetoed: Job '{}' was vetoed at fire time {} (scheduled for {})";
    public static final String JOB_EXECUTION_FAILED = "❌ Job '{}' failed with exception: ";
    public static final String JOB_EXECUTION_SUCCESS = "✅ Job '{}' executed successfully";

    public static final String MDC_REQUEST_ID = "requestId";
}
