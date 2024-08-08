package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

/**
 * Exception we get when we receive a malformed URL or received string could not
 * be parsed as a URI reference.
 *
 * @version 1.0
 */
public class InvalidURLException extends ConstraintDeclarationException {
    /**
     * Constructor for InvalidURLException.
     *
     * @param message - giving message.
     */
    public InvalidURLException(String message) {
        super(message);
    }
}
