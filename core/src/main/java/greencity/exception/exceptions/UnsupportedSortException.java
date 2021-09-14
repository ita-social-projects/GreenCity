package greencity.exception.exceptions;

public class UnsupportedSortException extends UnsupportedOperationException {
    /**
     * Exception that get if user write not supported sorting operation.
     */
    public UnsupportedSortException(String message) {
        super(message);
    }
}
