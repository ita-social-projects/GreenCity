package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when user trying to pass bad request.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@StandardException
public class BadRequestException extends RuntimeException {
}
