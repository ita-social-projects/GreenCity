package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to sign-up with email that already
 * registered.
 *
 * @author Nazar Stasyuk
 */
public class UserAlreadyRegisteredException extends RuntimeException {
    /**
     * Constructor for UserAlreadyRegisteredException.
     *
     * @param message - giving message.
     */
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
