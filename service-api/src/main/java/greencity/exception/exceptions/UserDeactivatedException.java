package greencity.exception.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception that is thrown when a deactivated user tries to send request to the
 * server.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class UserDeactivatedException extends AuthenticationException {
    /**
     * Constructor.
     */
    public UserDeactivatedException(String message) {
        super(message);
    }
}
