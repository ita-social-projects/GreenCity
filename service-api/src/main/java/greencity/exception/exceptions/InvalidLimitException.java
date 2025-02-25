package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when we receive the offset value out of limit.
 */
@StandardException
public class InvalidLimitException extends RuntimeException {
}