package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that indicates an attempt to access a secured resource without
 * proper authentication.
 */
@StandardException
public class UnauthorizedException extends RuntimeException {
}