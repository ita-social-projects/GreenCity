package greencity.exception.exceptions;

/**
 * This exception is threw when event instance cant not be created.
 */
public class EventCreationException extends RuntimeException {
    /**
     * constructor.
     *
     * @param message message, that describes exception cause.
     */
    public EventCreationException(String message) {
        super(message);
    }
}
