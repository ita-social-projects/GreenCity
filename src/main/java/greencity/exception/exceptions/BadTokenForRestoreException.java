package greencity.exception.exceptions;

public class BadTokenForRestoreException extends RuntimeException {
    /**
     * Constructor for BadTokenForRestoreException.
     *
     * @param message - giving message.
     */
    public BadTokenForRestoreException(String message) {
        super(message);
    }
}
