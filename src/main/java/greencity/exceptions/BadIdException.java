package greencity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadIdException extends RuntimeException {
    public BadIdException(String message) {
        super(message);
    }
}
