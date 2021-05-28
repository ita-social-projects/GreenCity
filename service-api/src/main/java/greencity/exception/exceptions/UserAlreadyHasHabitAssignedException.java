package greencity.exception.exceptions;

public class UserAlreadyHasHabitAssignedException extends BadRequestException {
    /**
     * Constructor.
     */
    public UserAlreadyHasHabitAssignedException(String message) {
        super(message);
    }
}
