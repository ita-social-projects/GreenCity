package greencity.exception.exceptions;

import greencity.entity.Participant;

/**
 * Exception that we get when we send request to find by it's parameters and
 * there is no {@link Participant}, then we get {@link UserNotFoundException}.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructor for UserNotFoundException.
     *
     * @param message - giving message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}