package greencity.exception;

public class UserHasNoGoalsException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoGoalsException(String message) {
        super(message);
    }
}
