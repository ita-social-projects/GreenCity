package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that user password is wrong.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends RuntimeException {
    /**
     * Constructor.
     */
    public WrongPasswordException(String message) {
        super(message);
    }
}
