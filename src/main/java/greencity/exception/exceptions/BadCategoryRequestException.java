package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to save category with bad parameters.
 *
 * @author Kateryna Horokh
 */
public class BadCategoryRequestException extends RuntimeException {
    /**
     * Constructor for BadCategoryRequestException.
     *
     * @param message - giving message.
     */
    public BadCategoryRequestException(String message) {
        super(message);
    }
}
