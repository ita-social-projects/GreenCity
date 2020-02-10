package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
public class NotImplementedMethodException extends RuntimeException {
    /**
     * Constructor for NotFoundException.
     *
     * @param message - giving message.
     */
    public NotImplementedMethodException(String message) {
        super(message);
    }
}