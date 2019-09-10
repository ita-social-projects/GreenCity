package greencity.exception;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructor for NotFoundException.
     *
     * @param message - giving message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}