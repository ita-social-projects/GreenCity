package greencity.exception;

/**
 * Exception that we get when user trying to save place with bad parameters.
 *
 * @author Kateryna Horokh
 * @version 1.0
 */
public class BadPlaceRequestException extends RuntimeException {
    /**
     * Constructor for BadPlaceRequestException.
     *
     * @param message - giving message.
     */
    public BadPlaceRequestException(String message) {
        super(message);
    }
}
