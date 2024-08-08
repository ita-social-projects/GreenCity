package greencity.exception.exceptions;

import jakarta.validation.ConstraintDeclarationException;

/**
 * Exception we get when we receive wrong social network links.
 *
 * @version 1.0
 */
public class BadSocialNetworkLinksException extends ConstraintDeclarationException {
    /**
     * Constructor for BadSocialNetworkLinksException.
     *
     * @param message - giving message.
     */
    public BadSocialNetworkLinksException(String message) {
        super(message);
    }
}
