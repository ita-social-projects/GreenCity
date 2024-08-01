package greencity.exception.exceptions;

/**
 * Exception that we get when in some logic we have bad ID.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class WrongIdException extends BadRequestException {
    /**
     * Constructor.
     *
     * @param message {@link String}
     */
    public WrongIdException(String message) {
        super(message);
    }
}
