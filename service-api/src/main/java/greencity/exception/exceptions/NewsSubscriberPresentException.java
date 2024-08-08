package greencity.exception.exceptions;

/**
 * Exception we get when a news subscriber is already present.
 *
 * @version 1.0
 */
public class NewsSubscriberPresentException extends BadRequestException {
    /**
     * Constructor for NewsSubscriberPresentException.
     *
     * @param message - giving message.
     */
    public NewsSubscriberPresentException(String message) {
        super(message);
    }
}
