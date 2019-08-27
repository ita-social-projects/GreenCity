package greencity.exception;

public class BadPlaceRequestException extends RuntimeException {
    public BadPlaceRequestException(String message) {
        super(message);
    }
}
