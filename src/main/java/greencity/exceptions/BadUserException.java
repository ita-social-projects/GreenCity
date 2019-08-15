package greencity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadUserException extends RuntimeException {
    public BadUserException(String message) {
        super(message);
    }
}
