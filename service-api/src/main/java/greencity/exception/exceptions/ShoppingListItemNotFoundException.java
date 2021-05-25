package greencity.exception.exceptions;

public class ShoppingListItemNotFoundException extends RuntimeException {
    /**
     * Constructor.
     */
    public ShoppingListItemNotFoundException(String message) {
        super(message);
    }
}
