package greencity.exception.exceptions;

/**
 * Exception we get when we receive wrong social network links.
 *
 * @version 1.0
 */
public class BadSocialNetworkLinksException extends BadRequestException {
    /**
     * Constructor for BadSocialNetworkLinksException.
     *
     * @param message - giving message.
     */
    public BadSocialNetworkLinksException(String message) {
        super(message);
    }
}
