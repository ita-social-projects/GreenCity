package greencity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadEmailException extends RuntimeException {
    public BadEmailException(String message) {
        super(message);
    }
}
