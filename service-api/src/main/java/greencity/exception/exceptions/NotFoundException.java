package greencity.exception.exceptions;

/**
 * Exception that we get when we send request(for e.x. to findById) and there is
 * no record with this id, then we get {@link NotFoundException}.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructor for NotFoundException.
     *
     * @param message - giving message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}