package greencity.constant;

import lombok.experimental.UtilityClass;


@UtilityClass
public class OpenAILogMessages {
    public static final String OPENAI_REQUEST_INITIATED = "Making request to OpenAI with prompt: {}";
    public static final String OPENAI_RESPONSE_RECEIVED = "Received response from OpenAI API: {}";
    public static final String OPENAI_REQUEST_FAILED = "Failed to send request to OpenAI API";
    public static final String OPENAI_REQUEST_PARAMETER_VALIDATION = "Validating request parameters";

    public static final String METHOD_EXECUTION_STARTED = "Method 'makeRequest' execution started at {}";
    public static final String METHOD_EXECUTED_IN_MS = "Method 'makeRequest' executed in {} ms";
    public static final String EXECUTION_TIME_FOR_METHOD = "Execution time for 'makeRequest': {} ms for prompt: \"{}\"";
    public static final String COMPLETE_EXCEPTION_STACK_TRACE = "Complete exception stack trace: ";
    public static final String STACK_TRACE_OF_THE_ERROR = "Stack trace of the error";
    public static final String RESPONSE_DETAILS = "Response details: {}";
    public static final String FULL_RESPONSE_FROM_OPENAI = "Full response from OpenAI API: {}";
    public static final String START_REQUEST_PARAMETER_VALIDATION = "Start request parameter validation for prompt: \"{}\"";
}


