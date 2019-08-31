package greencity.exception;

public class BadIdAndEmailException  extends RuntimeException {
    public BadIdAndEmailException(String message) {
        super(message);
    }
}
