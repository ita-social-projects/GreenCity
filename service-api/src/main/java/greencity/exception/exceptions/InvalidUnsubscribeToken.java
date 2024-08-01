package greencity.exception.exceptions;

/**
 * Exception we get when an unsubscribe token is invalid.
 *
 * @version 1.0
 */
public class InvalidUnsubscribeToken extends BadRequestException {
    /**
     * Constructor for InvalidUnsubscribeToken.
     *
     * @param message - giving message.
     */
    public InvalidUnsubscribeToken(String message) {
        super(message);
    }
}
