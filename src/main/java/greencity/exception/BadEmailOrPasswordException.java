package greencity.exception;

/**
 * Exception that we get when user trying to sign-in with bad email or password.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class BadEmailOrPasswordException extends RuntimeException {
    public BadEmailOrPasswordException(String message) {
        super(message);
    }
}
