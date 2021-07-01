package greencity.exception.exceptions;

/**
 * Exception is thrown when entity have invalid status for current operation.
 */
public class InvalidStatusException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message {@link String}
     */
    public InvalidStatusException(String message) {
        super(message);
    }
}
