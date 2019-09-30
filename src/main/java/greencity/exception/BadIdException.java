package greencity.exception;

/**
 * Exception that we get when in some logic we have bad ID.
 *
 * @author Nazar Stasyuk
 */
public class BadIdException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message {@link String}
     */
    public BadIdException(String message) {
        super(message);
    }
}
