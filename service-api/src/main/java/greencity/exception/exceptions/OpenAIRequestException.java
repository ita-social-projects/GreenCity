package greencity.exception.exceptions;

public class OpenAIRequestException extends RuntimeException {
    public OpenAIRequestException(String message) {
        super(message);
    }

    public OpenAIRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
