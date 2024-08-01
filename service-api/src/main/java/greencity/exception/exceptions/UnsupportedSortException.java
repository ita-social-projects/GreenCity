package greencity.exception.exceptions;

/**
 * Exception that get if user write not supported sorting operation.
 *
 * @version 1.0
 */
public class UnsupportedSortException extends UnsupportedOperationException {
    /**
     * Constructor for UnsupportedSortException.
     *
     * @param message - giving message.
     */
    public UnsupportedSortException(String message) {
        super(message);
    }
}
