package greencity.exception.exceptions;

import javax.validation.ConstraintDeclarationException;

public class WrongCountOfTagsException extends ConstraintDeclarationException {
    public WrongCountOfTagsException(String message) {
        super(message);
    }
}
