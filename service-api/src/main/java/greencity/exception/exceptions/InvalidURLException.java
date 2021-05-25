package greencity.exception.exceptions;

import javax.validation.ConstraintDeclarationException;

public class InvalidURLException extends ConstraintDeclarationException {
    /**
     * Exception we get when we receive a malformed URL or received string could not
     * be parsed as a URI reference.
     */
    public InvalidURLException(String message) {
        super(message);
    }
}
