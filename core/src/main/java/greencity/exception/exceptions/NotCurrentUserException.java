package greencity.exception.exceptions;

public class NotCurrentUserException extends RuntimeException {
    /**
     * Constructor.
     */
    public NotCurrentUserException(String message) {
        super(message);
    }
}
