package greencity.exception.exceptions;

/**
 * Exception that we get when user by this email not found.
 */
public class WrongEmailException extends RuntimeException {
    /**
     * Constructor.
     */
    public WrongEmailException(String message) {
        super(message);
    }
}