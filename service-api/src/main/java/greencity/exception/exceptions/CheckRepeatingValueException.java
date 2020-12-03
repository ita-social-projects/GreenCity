package greencity.exception.exceptions;

/**
 * Exception that we get when user trying to save schedule with repeating week
 * days.
 *
 * @author Kateryna Horokh
 */
public class CheckRepeatingValueException extends RuntimeException {
    /**
     * Constructor for CheckRepeatingValueException.
     *
     * @param message - giving message.
     */
    public CheckRepeatingValueException(String message) {
        super(message);
    }
}
