package greencity.exception;
/**
 * Exception that we get when user trying to sign-up with email that already registered.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class BadEmailException extends RuntimeException {
    public BadEmailException(String message) {
        super(message);
    }
}
