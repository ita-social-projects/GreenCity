package greencity.exception.exceptions;

/**
 * Exception that we get when admin/moderator trying to update himself.
 *
 * @author Rostyslav Khasanov
 */
public class BadUpdateRequestException extends RuntimeException {
    /**
     * Constructor for BadUpdateRequestException.
     *
     * @param message - giving message.
     */
    public BadUpdateRequestException(String message) {
        super(message);
    }
}
