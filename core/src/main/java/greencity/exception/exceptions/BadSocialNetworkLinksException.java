package greencity.exception.exceptions;

import javax.validation.ConstraintDeclarationException;

public class BadSocialNetworkLinksException extends ConstraintDeclarationException {
    /**
     * Exception we get when we receive wrong social network links.
     */
    public BadSocialNetworkLinksException(String message) {
        super(message);
    }
}
