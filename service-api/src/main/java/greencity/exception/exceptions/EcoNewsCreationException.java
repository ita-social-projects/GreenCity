package greencity.exception.exceptions;

public class EcoNewsCreationException extends RuntimeException {
    public EcoNewsCreationException(String message) {
        super(message);
    }

    public EcoNewsCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
