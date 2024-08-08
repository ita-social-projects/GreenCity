package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

/**
 * Exception we get when we try to use not unique (duplicated) tags.
 *
 * @version 1.0
 */
public class DuplicatedTagException extends ConstraintDeclarationException {
    /**
     * Constructor for DuplicatedTagException.
     *
     * @param message - giving message.
     */
    public DuplicatedTagException(String message) {
        super(message);
    }
}
