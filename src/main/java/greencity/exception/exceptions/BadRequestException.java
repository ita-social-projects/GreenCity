package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to pass bad request.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
public class BadRequestException extends RuntimeException {
    /**
     * Constructor for BadRequestException.
     *
     * @param message - giving message.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
