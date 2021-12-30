package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that we get when user by this email not found.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongEmailException extends RuntimeException {
    /**
     * Constructor.
     */
    public WrongEmailException(String message) {
        super(message);
    }
}
