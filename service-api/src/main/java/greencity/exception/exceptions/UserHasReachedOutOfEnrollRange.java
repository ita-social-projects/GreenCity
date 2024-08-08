package greencity.exception.exceptions;

/**
 * Exception thrown when a user has reached beyond the allowed enrollment range.
 *
 * @version 1.0
 */
public class UserHasReachedOutOfEnrollRange extends BadRequestException {
    /**
     * Constructor for UserHasReachedOutOfEnrollRange.
     *
     * @param message - providing the exception message.
     */
    public UserHasReachedOutOfEnrollRange(String message) {
        super(message);
    }
}
