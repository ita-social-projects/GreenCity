package greencity.exception.exceptions;

/**
 * Exception that indicates an attempt to access a secured resource without
 * proper authentication.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Unauthorized access");
    }
}