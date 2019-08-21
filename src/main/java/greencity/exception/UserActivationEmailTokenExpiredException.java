package greencity.exception;

public class UserActivationEmailTokenExpiredException extends RuntimeException {
    public UserActivationEmailTokenExpiredException(String message) {
        super(message);
    }
}
