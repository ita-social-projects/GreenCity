package greencity.exception;

public class BadEmailOrPasswordException extends RuntimeException {
    public BadEmailOrPasswordException(String message) {
        super(message);
    }
}
