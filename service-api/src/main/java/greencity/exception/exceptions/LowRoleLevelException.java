package greencity.exception.exceptions;

/**
 * Exception that we get when moderator trying to update user status of admin or
 * another moderator.
 *
 * @author Rostyslav Khasanov
 */
public class LowRoleLevelException extends RuntimeException {
    /**
     * Constructor for LowRoleLevelException.
     *
     * @param message - giving message.
     */
    public LowRoleLevelException(String message) {
        super(message);
    }
}
