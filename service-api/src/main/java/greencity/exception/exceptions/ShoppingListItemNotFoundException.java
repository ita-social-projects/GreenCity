package greencity.exception.exceptions;

/**
 * Exception that is thrown when a shopping list item is not found.
 *
 * @version 1.0
 */
public class ShoppingListItemNotFoundException extends NotFoundException {
    /**
     * Constructor for ShoppingListItemNotFoundException.
     *
     * @param message - giving message.
     */
    public ShoppingListItemNotFoundException(String message) {
        super(message);
    }
}
