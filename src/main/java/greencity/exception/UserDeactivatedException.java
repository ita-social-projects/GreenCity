package greencity.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception that we get when user trying to sign-in to account that is deactivated.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class UserDeactivatedException extends AuthenticationException {
    public UserDeactivatedException(String message) {
        super(message);
    }
}
