package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to save place with bad parameters.
 *
 * @author Kateryna Horokh
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
