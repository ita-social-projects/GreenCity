package greencity.exception.exceptions;

import jakarta.validation.ValidationException;

/**
 * Exception thrown when an invalid @link EventDto is passed in a request. This
 * exception indicates that the provided EventDto does not meet the required
 * validation constraints.
 *
 */
public class EventDtoValidationException extends ValidationException {
    /**
     * Constructs for EventDtoValidationException.
     *
     * @param message - giving message.
     */
    public EventDtoValidationException(String message) {
        super(message);
    }
}
