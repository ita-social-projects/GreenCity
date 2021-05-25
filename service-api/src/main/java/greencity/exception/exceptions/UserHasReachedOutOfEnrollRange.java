package greencity.exception.exceptions;

public class UserHasReachedOutOfEnrollRange extends BadRequestException {
    /**
     * Constructor.
     */
    public UserHasReachedOutOfEnrollRange(String message) {
        super(message);
    }
}
