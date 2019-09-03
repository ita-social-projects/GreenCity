package greencity.exception;

/**
 * Exception that we get when user trying to save schedule with repeating week days.
 *
 * @author Kateryna Horokh
 * @version 1.0
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
