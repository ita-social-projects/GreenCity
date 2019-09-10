package greencity.exception;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class BadVerifyEmailTokenException extends RuntimeException {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    public BadVerifyEmailTokenException(String message) {
        super(message);
    }
}
