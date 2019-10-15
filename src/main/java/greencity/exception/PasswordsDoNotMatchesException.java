package greencity.exception;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Dmytro Dovhal
 * @version 1.0
 */
public class PasswordsDoNotMatchesException extends RuntimeException {
    /**
     * Constructor for PasswordsDoNotMatchesException.
     *
     * @param message - giving message.
     */
    public PasswordsDoNotMatchesException(String message) {
        super(message);
    }
}
