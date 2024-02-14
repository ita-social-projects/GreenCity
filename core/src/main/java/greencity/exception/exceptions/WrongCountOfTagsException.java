package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

public class WrongCountOfTagsException extends ConstraintDeclarationException {
    /**
     * Exception we get when we receive less than one tag or more than three.
     */
    public WrongCountOfTagsException(String message) {
        super(message);
    }
}
