package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

/**
 * Exception we get when we try to use more than 3 tags.
 *
 * @version 1.0
 */
public class InvalidNumOfTagsException extends ConstraintDeclarationException {
    /**
     * Constructor for InvalidNumOfTagsException.
     *
     * @param message - giving message.
     */
    public InvalidNumOfTagsException(String message) {
        super(message);
    }
}
