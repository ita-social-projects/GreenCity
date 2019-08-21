package greencity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadIdException extends RuntimeException {
    public BadIdException(String message) {
        super(message);
    }
}