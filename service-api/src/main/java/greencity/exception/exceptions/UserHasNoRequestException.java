package greencity.exception.exceptions;

public class UserHasNoRequestException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoRequestException(String message) {
        super(message);
    }
}
