package greencity.exception.exceptions;

public class UserHasNoShoppingListItemsException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoShoppingListItemsException(String message) {
        super(message);
    }
}
