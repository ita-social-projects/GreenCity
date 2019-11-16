package greencity.exception;

public class UserHasNoAvailableGoalsException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoAvailableGoalsException(String message) {
        super(message);
    }
}
