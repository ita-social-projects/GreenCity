package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

/**
 * Exception we get when we receive less than one tag or more than three.
 *
 * @version 1.0
 */
public class WrongCountOfTagsException extends ConstraintDeclarationException {
    /**
     * Constructor for WrongCountOfTagsException.
     *
     * @param message - giving message.
     */
    public WrongCountOfTagsException(String message) {
        super(message);
    }
}
