package greencity.exception.exceptions;

import javax.validation.ConstraintDeclarationException;

public class DuplicatedTagException extends ConstraintDeclarationException {
    /**
     * Exception we get when we try to use not unique (duplicated) tags.
     */
    public DuplicatedTagException(String message) {
        super(message);
    }
}
