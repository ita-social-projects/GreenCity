package greencity.exception.exceptions;

public class UserHasNoAvailableShoppingListItemException extends RuntimeException {
    /**
     * Constructor.
     */
    public UserHasNoAvailableShoppingListItemException(String message) {
        super(message);
    }
}
