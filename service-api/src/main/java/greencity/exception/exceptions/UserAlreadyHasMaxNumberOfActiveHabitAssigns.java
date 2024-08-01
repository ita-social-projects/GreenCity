package greencity.exception.exceptions;

/**
 * Exception thrown when a user already has the maximum number of active habit
 * assignments.
 *
 * @version 1.0
 */
public class UserAlreadyHasMaxNumberOfActiveHabitAssigns extends BadRequestException {
    /**
     * Constructor for UserAlreadyHasMaxNumberOfActiveHabitAssigns.
     *
     * @param message - providing the exception message.
     */
    public UserAlreadyHasMaxNumberOfActiveHabitAssigns(String message) {
        super(message);
    }
}
