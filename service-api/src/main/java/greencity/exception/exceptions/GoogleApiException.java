package greencity.exception.exceptions;

/**
 * Exception could be thrown if application have issues to connecting to Google
 * servers.
 */
public class GoogleApiException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public GoogleApiException(String message) {
        super(message);
    }
}
