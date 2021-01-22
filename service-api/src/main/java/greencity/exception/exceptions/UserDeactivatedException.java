package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that we get when user trying to sign-in to account that is
 * deactivated.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDeactivatedException extends AuthenticationException {
    /**
     * Constructor.
     */
    public UserDeactivatedException(String message) {
        super(message);
    }
}
