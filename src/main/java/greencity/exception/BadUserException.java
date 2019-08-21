package greencity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadUserException extends RuntimeException {
    public BadUserException(String message) {
        super(message);
    }
}
