package greencity.exception.exceptions;

/**
 * Exception thrown when a user already has a habit assigned.
 *
 * @version 1.0
 */

public class UserAlreadyHasHabitAssignedException extends BadRequestException {
    /**
     * Constructor for UserAlreadyHasHabitAssignedException.
     *
     * @param message - providing the exception message.
     */
    public UserAlreadyHasHabitAssignedException(String message) {
        super(message);
    }
}
