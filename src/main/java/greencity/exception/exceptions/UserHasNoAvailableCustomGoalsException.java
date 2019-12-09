package greencity.exception.exceptions;

public class UserHasNoAvailableCustomGoalsException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoAvailableCustomGoalsException(String message) {
        super(message);
    }
}
