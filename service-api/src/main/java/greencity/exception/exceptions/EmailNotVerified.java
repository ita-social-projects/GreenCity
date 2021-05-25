package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Should be thrown when a use tries to sign in before email verification.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailNotVerified extends RuntimeException {
    /**
     * Constructor.
     */
    public EmailNotVerified() {
    }

    /**
     * Constructor.
     */
    public EmailNotVerified(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public EmailNotVerified(String message, Throwable cause) {
        super(message, cause);
    }
}
