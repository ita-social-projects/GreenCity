package greencity.exception.exceptions;

/**
 * Exception thrown when the status of a user's shopping list item could not be
 * updated.
 *
 * @version 1.0
 */
public class UserShoppingListItemStatusNotUpdatedException extends NotUpdatedException {
    /**
     * Constructor for UserShoppingListItemStatusNotUpdatedException.
     */
    public UserShoppingListItemStatusNotUpdatedException(String message) {
        super(message);
    }
}
