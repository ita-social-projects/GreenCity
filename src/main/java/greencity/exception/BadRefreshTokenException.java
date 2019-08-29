package greencity.exception;

public class BadRefreshTokenException extends RuntimeException {
    public BadRefreshTokenException(String message) {
        super(message);
    }
}
