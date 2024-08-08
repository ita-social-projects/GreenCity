package greencity.exception.exceptions;

/**
 * Exception we get when the user is not the current user.
 *
 * @version 1.0
 */
public class NotCurrentUserException extends RuntimeException {
    /**
     * Constructor for NotCurrentUserException.
     *
     * @param message - giving message.
     */
    public NotCurrentUserException(String message) {
        super(message);
    }
}
