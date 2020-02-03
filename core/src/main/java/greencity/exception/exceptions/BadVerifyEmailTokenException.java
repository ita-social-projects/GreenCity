package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 */
public class BadVerifyEmailTokenException extends RuntimeException {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    public BadVerifyEmailTokenException(String message) {
        super(message);
    }
}
