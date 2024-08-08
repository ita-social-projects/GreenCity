package greencity.exception.exceptions;

/**
 * Exception thrown when a user has no shopping list items.
 *
 * @version 1.0
 */
public class UserHasNoShoppingListItemsException extends RuntimeException {
    /**
     * Constructor for UserHasNoShoppingListItemsException.
     *
     * @param message - providing the exception message.
     */
    public UserHasNoShoppingListItemsException(String message) {
        super(message);
    }
}
