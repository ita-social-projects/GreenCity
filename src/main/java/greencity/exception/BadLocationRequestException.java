package greencity.exception;

/**
 * Exception that we get when user trying to save place with same coordinates.
 *
 * @author Kateryna Horokh
 * @version 1.0
 */
public class BadLocationRequestException extends RuntimeException {
    /**
     * Constructor for BadPlaceRequestException.
     *
     * @param message - giving message.
     */
    public BadLocationRequestException(String message) {
        super(message);
    }
}
