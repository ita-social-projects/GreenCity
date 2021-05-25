package greencity.exception.exceptions;

/**
 * Exception that we get when user has no permission for certain action.
 *
 */
public class UserHasNoPermissionToAccessException extends RuntimeException {
    /**
     * Constructor for UserHasNoPermissionToAccessException.
     *
     * @param message - message.
     */
    public UserHasNoPermissionToAccessException(String message) {
        super(message);
    }
}
