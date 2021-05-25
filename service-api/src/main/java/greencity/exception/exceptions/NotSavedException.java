package greencity.exception.exceptions;

/**
 * Exception that we get when we try saving some object but such object already
 * exist, then we get {@link NotSavedException}.
 *
 * @author Vitaliy Dzen
 * @version 1.0
 */
public class NotSavedException extends RuntimeException {
    /**
     * Constructor for NotSavedException.
     *
     * @param message - giving message.
     */
    public NotSavedException(String message) {
        super(message);
    }
}