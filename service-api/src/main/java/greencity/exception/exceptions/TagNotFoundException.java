package greencity.exception.exceptions;

public class TagNotFoundException extends NotFoundException {
    /**
     * Exception that we get when we try to get a tag that is not in the database.
     */
    public TagNotFoundException(String message) {
        super(message);
    }
}
