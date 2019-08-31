package greencity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    public NotFoundException(String message) {
        super(message);
    }
}