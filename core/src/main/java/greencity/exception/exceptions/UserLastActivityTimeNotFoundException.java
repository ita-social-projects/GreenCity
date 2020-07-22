package greencity.exception.exceptions;

/**
 * Exception that we get when we cannot get a user last activity time.
 *
 * @author Yurii Zhurakovskyi
 * @version 1.0
 */
public class UserLastActivityTimeNotFoundException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserLastActivityTimeNotFoundException(String message) {
        super(message);
    }
}
