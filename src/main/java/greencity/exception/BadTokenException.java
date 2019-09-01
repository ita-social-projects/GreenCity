package greencity.exception;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class BadTokenException extends RuntimeException {
    public BadTokenException(String message) {
        super(message);
    }
}
