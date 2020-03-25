package greencity.exception.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception that we get when user trying to sign-in with bad email or password.
 *
 * @author Nazar Stasyuk
 */
public class WrongEmailOrPasswordException extends AuthenticationException {
    /**
     * Constructor.
     */
    public WrongEmailOrPasswordException(String message) {
        super(message);
    }
}
