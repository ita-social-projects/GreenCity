package greencity.exception.exceptions;

public class InvalidUnsubscribeToken extends BadRequestException {
    /**
     * constructor.
     */
    public InvalidUnsubscribeToken(String message) {
        super(message);
    }
}
