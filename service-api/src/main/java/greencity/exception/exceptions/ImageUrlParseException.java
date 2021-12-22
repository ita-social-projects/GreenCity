package greencity.exception.exceptions;

/**
 * Exception that is thrown when parsing of image's URL fails.
 */
public class ImageUrlParseException extends RuntimeException {
    /**
     * Constructor with message.
     *
     * @param message message, that explains cause of the exception.
     */
    public ImageUrlParseException(String message) {
        super(message);
    }
}
