package greencity.exception.exceptions;

/**
 * Exception that user password is wrong.
 */
public class WrongPasswordException extends RuntimeException {
    /**
     * Constructor.
     */
    public WrongPasswordException(String message) {
        super(message);
    }
}
