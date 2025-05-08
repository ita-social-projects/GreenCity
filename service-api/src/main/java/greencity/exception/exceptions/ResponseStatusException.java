package greencity.exception.exceptions;

/**
 * Exception that indicates an HTTP-specific error with a custom response status.
 * Typically, thrown to signal that a request cannot be processed as expected,
 * such as when a resource already exists or input data is invalid. This
 * exception allows specifying both the HTTP status code and an optional reason
 * message.
 */
public class ResponseStatusException extends RuntimeException {
    public ResponseStatusException(String message) {
        super(message);
    }
}
