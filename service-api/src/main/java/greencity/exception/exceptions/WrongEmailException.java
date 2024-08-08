package greencity.exception.exceptions;

/**
 * Exception that we get when user by this email not found.
 *
 * @version 1.0
 */
public class WrongEmailException extends BadRequestException {
    /**
     * Constructor for WrongEmailException.
     */
    public WrongEmailException(String message) {
        super(message);
    }
}
