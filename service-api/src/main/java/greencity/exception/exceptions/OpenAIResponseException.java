package greencity.exception.exceptions;

public class OpenAIResponseException extends RuntimeException {
    public OpenAIResponseException(String message) {
        super(message);
    }

    public OpenAIResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
