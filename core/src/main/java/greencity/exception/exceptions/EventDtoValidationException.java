package greencity.exception.exceptions;

import javax.validation.ValidationException;

public class EventDtoValidationException extends ValidationException {
    /**
     * Exception we get when we receive where incorrect EventDto passed in request.
     */
    public EventDtoValidationException(String message) {
        super(message);
    }
}
