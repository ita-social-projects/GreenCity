package greencity.exception;

/**
 * Exception that we get when trying to find nonexistent favorite place by place id and user email .
 *
 * @author Zakhar Skaletskyi
 * @version 1.0
 */
public class BadIdOrEmailException extends RuntimeException {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    public BadIdOrEmailException(String message) {
        super(message);
    }
}
