package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to add place or left comment that is
 * blocked.
 *
 * @author Kateryna Horokh
 * @version 1.0
 */
public class UserBlockedException extends RuntimeException {
    /**
     * Constructor for UserBlockedException.
     */
    public UserBlockedException(String message) {
        super(message);
    }
}
