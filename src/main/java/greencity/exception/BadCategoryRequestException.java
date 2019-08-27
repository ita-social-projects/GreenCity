package greencity.exception;

public class BadCategoryRequestException extends RuntimeException {
    public BadCategoryRequestException(String message) {
        super(message);
    }
}
