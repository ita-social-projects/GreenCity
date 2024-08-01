package greencity.exception.exceptions;

/**
 * Exception we get when a user already has an enrolled habit assign.
 *
 * @version 1.0
 */
public class UserAlreadyHasEnrolledHabitAssign extends BadRequestException {
    /**
     * Constructor for UserAlreadyHasEnrolledHabitAssign.
     *
     * @param message - giving message.
     */
    public UserAlreadyHasEnrolledHabitAssign(String message) {
        super(message);
    }
}
