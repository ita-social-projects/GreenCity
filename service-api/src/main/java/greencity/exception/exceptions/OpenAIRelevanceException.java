package greencity.exception.exceptions;

public class OpenAIRelevanceException extends RuntimeException {
    public OpenAIRelevanceException(String message) {
        super(message);
    }

    public OpenAIRelevanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
