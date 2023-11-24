package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

public class TagNotFoundDuringValidation extends ConstraintDeclarationException {
    /**
     * Exception that we get when we try to get a tag that is not in the database.
     */
    public TagNotFoundDuringValidation(String message) {
        super(message);
    }
}
