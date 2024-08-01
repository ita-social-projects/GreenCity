package greencity.exception.exceptions;

/**
 * Exception, that is throw after saving object with language code, that doesn't
 * exist in database.
 */
public class LanguageNotFoundException extends NotFoundException {
    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public LanguageNotFoundException(String message) {
        super(message);
    }
}
