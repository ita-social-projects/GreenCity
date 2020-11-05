package greencity.exception.exceptions;

public class UserAlreadyHasMaxNumberOfActiveHabitAssigns extends BadRequestException {
    /**
     * Constructor.
     */
    public UserAlreadyHasMaxNumberOfActiveHabitAssigns(String message) {
        super(message);
    }
}
