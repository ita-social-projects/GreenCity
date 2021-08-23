package greencity.exception.exceptions;

public class TranslationNotFoundException extends RuntimeException {
    /**
     * Exception that we get when we try to get not-existing translation of
     * tips-and-tricks.
     */
    public TranslationNotFoundException(String message) {
        super(message);
    }
}
