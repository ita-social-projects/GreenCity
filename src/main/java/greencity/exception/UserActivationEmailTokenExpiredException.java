package greencity.exception;

/**
 * Exception that we get when user trying to verify email with token that has expired.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class UserActivationEmailTokenExpiredException extends RuntimeException {
    public UserActivationEmailTokenExpiredException(String message) {
        super(message);
    }
}
