package greencity.exception.exceptions;

public class UserHasNoAvailableCustomShoppingListItemsException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoAvailableCustomShoppingListItemsException(String message) {
        super(message);
    }
}
