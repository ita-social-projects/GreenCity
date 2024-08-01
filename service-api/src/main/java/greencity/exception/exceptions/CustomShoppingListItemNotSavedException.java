package greencity.exception.exceptions;

public class CustomShoppingListItemNotSavedException extends NotSavedException {
    /**
     * Constructor for CustomShoppingListItemNotSavedException.
     *
     * @param message - giving message.
     */
    public CustomShoppingListItemNotSavedException(String message) {
        super(message);
    }
}
