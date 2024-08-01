package greencity.exception.exceptions;

/**
 * Exception that we get when we try to get a tag that is not in the database.
 *
 * @version 1.0
 */
public class TagNotFoundException extends NotFoundException {
    /**
     * Constructor for TagNotFoundException.
     *
     * @param message - giving message.
     */
    public TagNotFoundException(String message) {
        super(message);
    }
}
