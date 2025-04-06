package greencity.exception.exceptions;

public class InvalidJsonFormatException extends RuntimeException {
    public InvalidJsonFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
