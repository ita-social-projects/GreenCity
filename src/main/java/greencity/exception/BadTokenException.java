package greencity.exception;

/**
 * Exception that we get when user trying to verify email with bad token.
 *
 * @author Nazar Stasyuk
 */
public class BadTokenException extends RuntimeException {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    public BadTokenException(String message) {
        super(message);
    }
}
