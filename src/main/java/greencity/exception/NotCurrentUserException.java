package greencity.exception;

public class NotCurrentUserException extends RuntimeException {
    /**
     * Constructor.
     */
    public NotCurrentUserException(String message) {
        super(message);
    }
}
