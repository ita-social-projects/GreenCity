package greencity.exception.exceptions;

import javax.validation.ConstraintDeclarationException;

public class InvalidNumOfTagsException extends ConstraintDeclarationException {
    /**
     * Exception we get when we try to use more than 3 tags.
     */
    public InvalidNumOfTagsException(String message) {
        super(message);
    }
}
