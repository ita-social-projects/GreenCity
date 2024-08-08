package greencity.exception.exceptions;

/**
 * Exception that is thrown when an entity has an invalid status for the current
 * operation, specifically related to place status.
 *
 * @version 1.0
 */
public class PlaceStatusException extends InvalidStatusException {
    /**
     * Constructor for PlaceStatusException.
     *
     * @param message - giving message.
     */
    public PlaceStatusException(String message) {
        super(message);
    }
}
