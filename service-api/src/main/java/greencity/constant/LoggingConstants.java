package greencity.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LoggingConstants {
    public static final String METHOD_INVOCATION = "Invoking method '{}' in class '{}' on thread '{}'";
    public static final String METHOD_MODIFIERS = "Method modifiers: {}";
    public static final String RETURN_TYPE = "Return type: {}";
    public static final String ARGUMENT = "Argument[{}]: name='{}', type='{}', value='{}'";
    public static final String CALLED_FROM = "Called from: {}.{}():{}";
    public static final String METHOD_EXECUTION_TIME = "Method '{}' executed in {} ms";
    public static final String EXCEPTION_IN_METHOD = "Exception in method '{}' after {} ms: {}";
    public static final String METHOD_THROW_EXCEPTION = "Method: {} threw exception: {}";

    public static final String RETURN_VALUE = "Return value: type='{}', value='{}'";
    public static final String RETURN_VALUE_NULL = "Return value: null";
    public static final String NULL = "null";
    public static final String TRUNCATED = "... [truncated]";

    public static final String ERROR_SERIALIZING_TO_JSON = "[Error serializing to JSON]";
}
