package greencity.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a user with the given email already exists.
 * Indicates a conflict in resource creation (HTTP 409).
 */
public class UserAlreadyExistsException extends ResponseStatusException {
    public UserAlreadyExistsException(HttpStatus status, String message) {
        super(status, message);
    }
}
