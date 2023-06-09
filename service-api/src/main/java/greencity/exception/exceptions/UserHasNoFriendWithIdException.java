package greencity.exception.exceptions;

/**
 * Exception that we get when user has no friend with certain id.
 *
 * @author Volodymyr Kharchenko.
 * @version 1.0.
 */

public class UserHasNoFriendWithIdException extends RuntimeException {
    /**
     * Constructor for UserHasNoFriendWithIdException.
     *
     * @param message - message.
     */
    public UserHasNoFriendWithIdException(String message) {
        super(message);
    }
}
